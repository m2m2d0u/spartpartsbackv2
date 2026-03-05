package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import sn.symmetry.spareparts.dto.response.CompanySettingsResponse;
import sn.symmetry.spareparts.entity.Invoice;
import sn.symmetry.spareparts.entity.InvoiceItem;
import sn.symmetry.spareparts.entity.InvoiceTemplate;
import sn.symmetry.spareparts.entity.Store;
import sn.symmetry.spareparts.enums.InvoiceDesign;
import sn.symmetry.spareparts.enums.InvoiceStatus;
import sn.symmetry.spareparts.enums.InvoiceType;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.repository.InvoiceRepository;
import sn.symmetry.spareparts.repository.InvoiceTemplateRepository;
import sn.symmetry.spareparts.service.CompanySettingsService;
import sn.symmetry.spareparts.service.FileStorageService;
import sn.symmetry.spareparts.service.InvoicePdfService;
import sn.symmetry.spareparts.util.NumberToWordsConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class InvoicePdfServiceImpl implements InvoicePdfService {

    private final InvoiceRepository invoiceRepository;
    private final CompanySettingsService companySettingsService;
    private final InvoiceTemplateRepository invoiceTemplateRepository;
    private final FileStorageService fileStorageService;
    private final TemplateEngine templateEngine;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public ByteArrayOutputStream generateInvoicePdf(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        CompanySettingsResponse companySettings = companySettingsService.getSettings();
        InvoiceTemplate template = getInvoiceTemplate(invoice, companySettings);

        InvoiceDesign design = template.getDesign() != null ? template.getDesign() : InvoiceDesign.CLASSIC;
        String templateName = "invoice/" + design.name().toLowerCase();

        log.info("Generating PDF for invoice: {} with template ID: {}, design: {}",
                invoiceId,
                template.getId() != null ? template.getId() : "default",
                design);

        // Build Thymeleaf context
        Context context = buildThymeleafContext(invoice, companySettings, template);

        // Render HTML
        String html = templateEngine.process(templateName, context);

        // Convert HTML to PDF via Flying Saucer
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            renderer.finishPDF();

            log.info("PDF generated successfully for invoice: {} using {} design", invoiceId, design);
            return outputStream;
        } catch (Exception e) {
            log.error("Error generating PDF for invoice: {}", invoiceId, e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private Context buildThymeleafContext(Invoice invoice, CompanySettingsResponse companySettings,
                                          InvoiceTemplate template) {
        Context context = new Context();
        context.setVariable("invoice", invoice);
        context.setVariable("companySettings", companySettings);
        context.setVariable("template", template);

        // Formatted dates
        context.setVariable("issuedDate", invoice.getIssuedDate().format(DATE_FORMATTER));
        if (invoice.getDueDate() != null) {
            context.setVariable("dueDate", invoice.getDueDate().format(DATE_FORMATTER));
        }
        if (invoice.getValidityDate() != null) {
            context.setVariable("validityDate", invoice.getValidityDate().format(DATE_FORMATTER));
        }

        // Labels
        context.setVariable("invoiceTypeLabel", getInvoiceTypeLabel(invoice.getInvoiceType()));
        context.setVariable("statusLabel", getStatusLabel(invoice.getStatus()));

        // Currency formatting using CompanySettings
        String symbol = companySettings.getCurrencySymbol() != null ? companySettings.getCurrencySymbol() : "XOF";
        String position = companySettings.getCurrencyPosition() != null ? companySettings.getCurrencyPosition() : "AFTER";
        int decimals = companySettings.getCurrencyDecimals() != null ? companySettings.getCurrencyDecimals() : 0;

        // Formatted totals
        context.setVariable("formattedSubtotal", formatCurrency(invoice.getSubtotal(), symbol, position, decimals));
        context.setVariable("formattedDiscount", formatCurrency(invoice.getDiscountAmount(), symbol, position, decimals));
        context.setVariable("formattedTax", formatCurrency(invoice.getTaxAmount(), symbol, position, decimals));
        context.setVariable("formattedDeposit", formatCurrency(invoice.getDepositDeduction(), symbol, position, decimals));
        context.setVariable("formattedTotal", formatCurrency(invoice.getTotalAmount(), symbol, position, decimals));

        // Per-item formatted amounts
        BigDecimal taxRate = companySettings.getDefaultTaxRate();
        List<String> itemTaxes = new ArrayList<>();
        List<String> itemUnitPrices = new ArrayList<>();
        List<String> itemTotalPrices = new ArrayList<>();
        for (InvoiceItem item : invoice.getItems()) {
            BigDecimal taxAmount = calculateItemTax(item, taxRate);
            itemTaxes.add(formatCurrency(taxAmount, symbol, position, decimals));
            itemUnitPrices.add(formatCurrency(item.getUnitPrice(), symbol, position, decimals));
            itemTotalPrices.add(formatCurrency(item.getTotalPrice(), symbol, position, decimals));
        }
        context.setVariable("itemTaxes", itemTaxes);
        context.setVariable("itemUnitPrices", itemUnitPrices);
        context.setVariable("itemTotalPrices", itemTotalPrices);

        // Amount in words
        String amountInWords = NumberToWordsConverter.convertToWordsWithSymbol(
                invoice.getTotalAmount(),
                companySettings.getCurrencySymbol(),
                companySettings.getCurrencyDecimals()
        );
        context.setVariable("amountInWords", amountInWords);

        // Company registration info (from store or company settings)
        String taxId = null;
        String ninea = null;
        String rccm = null;
        if (invoice.getSourceWarehouse() != null && invoice.getSourceWarehouse().getStore() != null) {
            Store store = invoice.getSourceWarehouse().getStore();
            taxId = store.getTaxId();
            ninea = store.getNinea();
            rccm = store.getRccm();
        } else {
            taxId = companySettings.getTaxId();
            ninea = companySettings.getNinea();
            rccm = companySettings.getRccm();
        }
        context.setVariable("taxId", taxId);
        context.setVariable("ninea", ninea);
        context.setVariable("rccm", rccm);

        // Logo and stamp as Base64 (scaled to fit max dimensions)
        context.setVariable("logoBase64", loadImageAsBase64(
                template.getLogoUrl(),
                invoice.getSourceWarehouse() != null && invoice.getSourceWarehouse().getStore() != null ?
                        invoice.getSourceWarehouse().getStore().getLogoUrl() : null,
                120, 60
        ));
        context.setVariable("stampBase64", loadImageAsBase64(
                template.getStampImageUrl(),
                invoice.getSourceWarehouse() != null && invoice.getSourceWarehouse().getStore() != null ?
                        invoice.getSourceWarehouse().getStore().getStampImageUrl() : null,
                100, 100
        ));

        return context;
    }

    private String loadImageAsBase64(String primaryUrl, String fallbackUrl, int maxWidth, int maxHeight) {
        String url = null;
        if (primaryUrl != null && !primaryUrl.isEmpty()) {
            url = primaryUrl;
        } else if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
            url = fallbackUrl;
        }

        if (url != null) {
            try {
                String objectName = extractObjectName(url);
                if (objectName != null) {
                    byte[] bytes = fileStorageService.getFileBytes(objectName);
                    byte[] scaled = scaleImage(bytes, maxWidth, maxHeight);
                    return java.util.Base64.getEncoder().encodeToString(scaled);
                }
            } catch (Exception e) {
                log.warn("Could not load image as Base64: {}", e.getMessage());
            }
        }
        return null;
    }

    private byte[] scaleImage(byte[] imageBytes, int maxWidth, int maxHeight) {
        try {
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (original == null) {
                return imageBytes;
            }

            int origWidth = original.getWidth();
            int origHeight = original.getHeight();

            // No scaling needed if already within bounds
            if (origWidth <= maxWidth && origHeight <= maxHeight) {
                return imageBytes;
            }

            double scale = Math.min((double) maxWidth / origWidth, (double) maxHeight / origHeight);
            int newWidth = (int) (origWidth * scale);
            int newHeight = (int) (origHeight * scale);

            BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaled.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
            g2d.dispose();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(scaled, "png", out);
            return out.toByteArray();
        } catch (Exception e) {
            log.warn("Could not scale image, using original: {}", e.getMessage());
            return imageBytes;
        }
    }

    private InvoiceTemplate getInvoiceTemplate(Invoice invoice, CompanySettingsResponse companySettings) {
        if (invoice.getTemplate() != null) {
            InvoiceTemplate template = invoice.getTemplate();
            log.debug("Using invoice's template: ID={}, design={}", template.getId(), template.getDesign());
            return template;
        } else if (companySettings.getDefaultTemplateId() != null) {
            log.debug("Loading default template from company settings: ID={}", companySettings.getDefaultTemplateId());
            return invoiceTemplateRepository.findById(companySettings.getDefaultTemplateId())
                    .map(t -> {
                        log.debug("Loaded template: ID={}, design={}", t.getId(), t.getDesign());
                        return t;
                    })
                    .orElseGet(() -> {
                        log.debug("Default template not found, using fallback");
                        return createDefaultTemplate();
                    });
        }
        log.debug("No template configured, using fallback");
        return createDefaultTemplate();
    }

    private InvoiceTemplate createDefaultTemplate() {
        InvoiceTemplate template = new InvoiceTemplate();
        template.setPrimaryColor("#000000");
        template.setAccentColor("#4F46E5");
        template.setFontFamily("Helvetica");
        template.setDesign(InvoiceDesign.CLASSIC);
        template.setHeaderLayout("LOGO_LEFT");
        template.setShowNinea(true);
        template.setShowRccm(true);
        template.setShowTaxId(true);
        template.setShowWarehouseAddress(false);
        template.setShowCustomerTaxId(true);
        template.setShowPaymentTerms(true);
        template.setShowDiscountColumn(true);
        return template;
    }

    private String getInvoiceTypeLabel(InvoiceType type) {
        return switch (type) {
            case PROFORMA -> "PROFORMA";
            case STANDARD -> "FACTURE";
            case DEPOSIT -> "FACTURE D'ACOMPTE";
        };
    }

    private String getStatusLabel(InvoiceStatus status) {
        return switch (status) {
            case DRAFT -> "Brouillon";
            case PAID -> "Payee";
            case PARTIALLY_PAID -> "Partiellement payee";
            case OVERDUE -> "En retard";
        };
    }

    private BigDecimal calculateItemTax(InvoiceItem item, BigDecimal taxRate) {
        if (taxRate == null) return BigDecimal.ZERO;
        return item.getTotalPrice().multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private String formatCurrency(BigDecimal amount, String symbol, String position, int decimals) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');

        DecimalFormat numberFormat = new DecimalFormat();
        numberFormat.setDecimalFormatSymbols(symbols);
        numberFormat.setGroupingUsed(true);
        numberFormat.setGroupingSize(3);
        numberFormat.setMinimumFractionDigits(decimals);
        numberFormat.setMaximumFractionDigits(decimals);
        String formattedNumber = numberFormat.format(amount);

        if ("BEFORE".equalsIgnoreCase(position)) {
            return symbol + " " + formattedNumber;
        }
        return formattedNumber + " " + symbol;
    }

    private String extractObjectName(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        int lastSlashIndex = fileUrl.lastIndexOf("/spareparts/");
        if (lastSlashIndex != -1) {
            return fileUrl.substring(lastSlashIndex + "/spareparts/".length());
        }
        return null;
    }
}
