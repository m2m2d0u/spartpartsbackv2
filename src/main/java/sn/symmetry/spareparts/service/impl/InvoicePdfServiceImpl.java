package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import sn.symmetry.spareparts.dto.request.CreateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.response.CompanySettingsResponse;
import sn.symmetry.spareparts.entity.Customer;
import sn.symmetry.spareparts.entity.Invoice;
import sn.symmetry.spareparts.entity.InvoiceItem;
import sn.symmetry.spareparts.entity.InvoiceTemplate;
import sn.symmetry.spareparts.entity.Part;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    @Override
    public ByteArrayOutputStream generateDesignPreviewPdf(InvoiceDesign design, CreateInvoiceTemplateRequest request, Map<String, byte[]> uploadedImages) {
        CompanySettingsResponse companySettings = companySettingsService.getSettings();

        // Build template from the user-provided configuration
        InvoiceTemplate template = mapRequestToTemplate(request);
        template.setDesign(design);

        // Build a fake invoice (not persisted)
        Invoice invoice = buildSampleInvoice(companySettings);

        String templateName = "invoice/" + design.name().toLowerCase();

        log.info("Generating design preview PDF for design: {}", design);

        Context context = buildThymeleafContext(invoice, companySettings, template);

        // Override images with uploaded multipart files (takes priority over URLs)
        overrideImagesFromUpload(context, uploadedImages);

        String html = templateEngine.process(templateName, context);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            renderer.finishPDF();

            log.info("Design preview PDF generated successfully for design: {}", design);
            return outputStream;
        } catch (Exception e) {
            log.error("Error generating design preview PDF for design: {}", design, e);
            throw new RuntimeException("Failed to generate design preview PDF", e);
        }
    }

    private void overrideImagesFromUpload(Context context, Map<String, byte[]> uploadedImages) {
        if (uploadedImages == null || uploadedImages.isEmpty()) return;

        if (uploadedImages.containsKey("logo")) {
            byte[] scaled = scaleImage(uploadedImages.get("logo"), 120, 60);
            context.setVariable("logoBase64", java.util.Base64.getEncoder().encodeToString(scaled));
        }
        if (uploadedImages.containsKey("stamp")) {
            byte[] scaled = scaleImage(uploadedImages.get("stamp"), 100, 100);
            context.setVariable("stampBase64", java.util.Base64.getEncoder().encodeToString(scaled));
        }
    }

    private InvoiceTemplate mapRequestToTemplate(CreateInvoiceTemplateRequest request) {
        InvoiceTemplate template = createDefaultTemplate();
        if (request.getPrimaryColor() != null) template.setPrimaryColor(request.getPrimaryColor());
        if (request.getAccentColor() != null) template.setAccentColor(request.getAccentColor());
        if (request.getFontFamily() != null) template.setFontFamily(request.getFontFamily());
        if (request.getDesign() != null) template.setDesign(request.getDesign());
        if (request.getHeaderLayout() != null) template.setHeaderLayout(request.getHeaderLayout());
        if (request.getLogoUrl() != null) template.setLogoUrl(request.getLogoUrl());
        if (request.getHeaderImageUrl() != null) template.setHeaderImageUrl(request.getHeaderImageUrl());
        if (request.getFooterImageUrl() != null) template.setFooterImageUrl(request.getFooterImageUrl());
        if (request.getStampImageUrl() != null) template.setStampImageUrl(request.getStampImageUrl());
        if (request.getSignatureImageUrl() != null) template.setSignatureImageUrl(request.getSignatureImageUrl());
        if (request.getWatermarkImageUrl() != null) template.setWatermarkImageUrl(request.getWatermarkImageUrl());
        if (request.getShowNinea() != null) template.setShowNinea(request.getShowNinea());
        if (request.getShowRccm() != null) template.setShowRccm(request.getShowRccm());
        if (request.getShowTaxId() != null) template.setShowTaxId(request.getShowTaxId());
        if (request.getShowWarehouseAddress() != null) template.setShowWarehouseAddress(request.getShowWarehouseAddress());
        if (request.getShowCustomerTaxId() != null) template.setShowCustomerTaxId(request.getShowCustomerTaxId());
        if (request.getShowPaymentTerms() != null) template.setShowPaymentTerms(request.getShowPaymentTerms());
        if (request.getShowDiscountColumn() != null) template.setShowDiscountColumn(request.getShowDiscountColumn());
        if (request.getDefaultNotes() != null) template.setDefaultNotes(request.getDefaultNotes());
        return template;
    }

    private Invoice buildSampleInvoice(CompanySettingsResponse companySettings) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("FAC-2025-XXXXX");
        invoice.setInvoiceType(InvoiceType.STANDARD);
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setIssuedDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30));

        // Fake customer
        Customer customer = new Customer();
        customer.setName("Jean Dupont");
        customer.setCompany("Auto Pièces Dakar SARL");
        customer.setPhone("+221 77 123 45 67");
        customer.setEmail("jean.dupont@example.com");
        customer.setStreet("123 Avenue Cheikh Anta Diop");
        customer.setCity("Dakar");
        customer.setCountry("Sénégal");
        invoice.setCustomer(customer);

        // Sample items
        List<InvoiceItem> items = new ArrayList<>();

        InvoiceItem item1 = new InvoiceItem();
        Part part1 = new Part();
        part1.setName("Filtre à huile - Toyota Corolla");
        part1.setPartNumber("FH-TOY-001");
        item1.setPart(part1);
        item1.setQuantity(3);
        item1.setUnitPrice(new BigDecimal("4500"));
        item1.setDiscountPercent(BigDecimal.ZERO);
        item1.setDiscountAmount(BigDecimal.ZERO);
        item1.setTotalPrice(new BigDecimal("13500"));
        item1.setInvoice(invoice);
        items.add(item1);

        InvoiceItem item2 = new InvoiceItem();
        Part part2 = new Part();
        part2.setName("Plaquettes de frein avant - Peugeot 308");
        part2.setPartNumber("PF-PEU-042");
        item2.setPart(part2);
        item2.setQuantity(2);
        item2.setUnitPrice(new BigDecimal("12750"));
        item2.setDiscountPercent(new BigDecimal("5"));
        item2.setDiscountAmount(new BigDecimal("1275"));
        item2.setTotalPrice(new BigDecimal("24225"));
        item2.setInvoice(invoice);
        items.add(item2);

        InvoiceItem item3 = new InvoiceItem();
        Part part3 = new Part();
        part3.setName("Courroie de distribution - Renault Clio");
        part3.setPartNumber("CD-REN-015");
        item3.setPart(part3);
        item3.setQuantity(1);
        item3.setUnitPrice(new BigDecimal("28000"));
        item3.setDiscountPercent(BigDecimal.ZERO);
        item3.setDiscountAmount(BigDecimal.ZERO);
        item3.setTotalPrice(new BigDecimal("28000"));
        item3.setInvoice(invoice);
        items.add(item3);

        invoice.setItems(items);

        // Compute totals
        BigDecimal subtotal = new BigDecimal("65725");
        BigDecimal discountAmount = new BigDecimal("1275");
        BigDecimal taxRate = companySettings.getDefaultTaxRate() != null ? companySettings.getDefaultTaxRate() : BigDecimal.ZERO;
        BigDecimal taxableAmount = subtotal.subtract(discountAmount);
        BigDecimal taxAmount = taxableAmount.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        invoice.setSubtotal(subtotal);
        invoice.setDiscountAmount(discountAmount);
        invoice.setTaxAmount(taxAmount);
        invoice.setDepositDeduction(BigDecimal.ZERO);
        invoice.setTotalAmount(taxableAmount.add(taxAmount));

        return invoice;
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
        String thousandsSep = companySettings.getThousandsSeparator() != null ? companySettings.getThousandsSeparator() : " ";

        // Formatted totals
        context.setVariable("formattedSubtotal", formatCurrency(invoice.getSubtotal(), symbol, position, decimals, thousandsSep));
        context.setVariable("formattedDiscount", formatCurrency(invoice.getDiscountAmount(), symbol, position, decimals, thousandsSep));
        context.setVariable("formattedTax", formatCurrency(invoice.getTaxAmount(), symbol, position, decimals, thousandsSep));
        context.setVariable("formattedDeposit", formatCurrency(invoice.getDepositDeduction(), symbol, position, decimals, thousandsSep));
        context.setVariable("formattedTotal", formatCurrency(invoice.getTotalAmount(), symbol, position, decimals, thousandsSep));

        // Per-item formatted amounts
        BigDecimal taxRate = companySettings.getDefaultTaxRate();
        List<String> itemTaxes = new ArrayList<>();
        List<String> itemUnitPrices = new ArrayList<>();
        List<String> itemTotalPrices = new ArrayList<>();
        for (InvoiceItem item : invoice.getItems()) {
            BigDecimal taxAmount = calculateItemTax(item, taxRate);
            itemTaxes.add(formatCurrency(taxAmount, symbol, position, decimals, thousandsSep));
            itemUnitPrices.add(formatCurrency(item.getUnitPrice(), symbol, position, decimals, thousandsSep));
            itemTotalPrices.add(formatCurrency(item.getTotalPrice(), symbol, position, decimals, thousandsSep));
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

    private String formatCurrency(BigDecimal amount, String symbol, String position, int decimals, String thousandsSeparator) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        char sep = (thousandsSeparator != null && !thousandsSeparator.isEmpty()) ? thousandsSeparator.charAt(0) : ' ';
        symbols.setGroupingSeparator(sep);
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
