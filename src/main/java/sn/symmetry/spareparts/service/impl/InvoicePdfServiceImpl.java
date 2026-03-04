package sn.symmetry.spareparts.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final float MARGIN = 36f;

    @Override
    public ByteArrayOutputStream generateInvoicePdf(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        CompanySettingsResponse companySettings = companySettingsService.getSettings();
        InvoiceTemplate template = getInvoiceTemplate(invoice, companySettings);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4, MARGIN, MARGIN, MARGIN, MARGIN);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Route to appropriate design renderer
            InvoiceDesign design = template.getDesign() != null ? template.getDesign() : InvoiceDesign.CLASSIC;

            log.info("Generating PDF for invoice: {} with template ID: {}, design: {}",
                    invoiceId,
                    template.getId() != null ? template.getId() : "default",
                    design);

            switch (design) {
                case MODERN -> renderModernDesign(document, writer, invoice, companySettings, template);
                case ELEGANT -> renderElegantDesign(document, writer, invoice, companySettings, template);
                case COMPACT -> renderCompactDesign(document, writer, invoice, companySettings, template);
                case PROFESSIONAL -> renderProfessionalDesign(document, writer, invoice, companySettings, template);
                default -> renderClassicDesign(document, writer, invoice, companySettings, template);
            }

            document.close();
            log.info("PDF generated successfully for invoice: {} using {} design", invoiceId, design);

        } catch (DocumentException | IOException e) {
            log.error("Error generating PDF for invoice: {}", invoiceId, e);
            throw new RuntimeException("Failed to generate PDF", e);
        }

        return outputStream;
    }

    // ==================== CLASSIC DESIGN ====================
    private void renderClassicDesign(Document document, PdfWriter writer, Invoice invoice,
                                     CompanySettingsResponse companySettings, InvoiceTemplate template)
            throws DocumentException, IOException {
        addClassicHeader(document, invoice, companySettings, template);
        addClassicInvoiceDetails(document, invoice, template);
        addClassicBillingInfo(document, invoice, template);
        addClassicItemsTable(document, invoice, companySettings, template);
        addClassicTotals(document, invoice, companySettings, template);
        addNotes(document, invoice, template);
        addFooter(document, invoice, template);
    }

    private void addClassicHeader(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                  InvoiceTemplate template) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 1});
        headerTable.setSpacingAfter(20f);

        // Left side - Company info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        // Add logo if available
        Image logo = loadLogo(invoice, template);
        if (logo != null) {
            Paragraph logoParagraph = new Paragraph();
            logoParagraph.add(new Chunk(logo, 0, 0));
            logoParagraph.setSpacingAfter(10f);
            leftCell.addElement(logoParagraph);
        }

        Paragraph companyInfo = new Paragraph();
        Font companyNameFont = new Font(Font.HELVETICA, 18, Font.BOLD, parseColor(template.getPrimaryColor()));
        companyInfo.add(new Chunk(companySettings.getCompanyName() != null ?
                companySettings.getCompanyName() : "Company Name", companyNameFont));
        companyInfo.add(Chunk.NEWLINE);

        Font normalFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
        addCompanyAddress(companyInfo, companySettings, normalFont);
        addCompanyRegistration(companyInfo, invoice, companySettings, template, normalFont);
        leftCell.addElement(companyInfo);
        headerTable.addCell(leftCell);

        // Right side - Invoice type (only if not STANDARD)
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        if (invoice.getInvoiceType() != InvoiceType.STANDARD) {
            Font invoiceTypeFont = new Font(Font.HELVETICA, 24, Font.BOLD, parseColor(template.getAccentColor()));
            Paragraph invoiceType = new Paragraph(getInvoiceTypeLabel(invoice.getInvoiceType()), invoiceTypeFont);
            invoiceType.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(invoiceType);
        }
        headerTable.addCell(rightCell);

        document.add(headerTable);
    }

    private void addClassicInvoiceDetails(Document document, Invoice invoice, InvoiceTemplate template)
            throws DocumentException {
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setWidths(new float[]{1, 1});
        detailsTable.setSpacingAfter(15f);

        Font labelFont = new Font(Font.HELVETICA, 9, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        // Left column
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        Paragraph leftDetails = new Paragraph();
        leftDetails.add(new Chunk("Numéro: ", labelFont));
        leftDetails.add(new Chunk(invoice.getInvoiceNumber() + "\n", valueFont));
        leftDetails.add(new Chunk("Date d'émission: ", labelFont));
        leftDetails.add(new Chunk(invoice.getIssuedDate().format(DATE_FORMATTER) + "\n", valueFont));

        if (invoice.getDueDate() != null && template.getShowPaymentTerms()) {
            leftDetails.add(new Chunk("Date d'échéance: ", labelFont));
            leftDetails.add(new Chunk(invoice.getDueDate().format(DATE_FORMATTER) + "\n", valueFont));
        }
        if (invoice.getValidityDate() != null) {
            leftDetails.add(new Chunk("Valide jusqu'au: ", labelFont));
            leftDetails.add(new Chunk(invoice.getValidityDate().format(DATE_FORMATTER) + "\n", valueFont));
        }
        leftCell.addElement(leftDetails);
        detailsTable.addCell(leftCell);

        // Right column - Status
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph status = new Paragraph();
        Font statusFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        status.add(new Chunk("Statut: " + getStatusLabel(invoice.getStatus()), statusFont));
        status.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(status);
        detailsTable.addCell(rightCell);

        document.add(detailsTable);
    }

    private void addClassicBillingInfo(Document document, Invoice invoice, InvoiceTemplate template)
            throws DocumentException {
        PdfPTable billingTable = new PdfPTable(1);
        billingTable.setWidthPercentage(100);
        billingTable.setSpacingAfter(20f);

        PdfPCell cell = new PdfPCell();
        cell.setPadding(10f);
        cell.setBackgroundColor(new Color(245, 245, 245));

        Font titleFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        Paragraph billing = new Paragraph();
        billing.add(new Chunk("CLIENT\n", titleFont));
        addCustomerInfo(billing, invoice, template, normalFont);
        cell.addElement(billing);
        billingTable.addCell(cell);

        document.add(billingTable);
    }

    private void addClassicItemsTable(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                      InvoiceTemplate template) throws DocumentException {
        int columnCount = template.getShowDiscountColumn() ? 6 : 5;
        float[] widths = template.getShowDiscountColumn() ?
                new float[]{3f, 1f, 1.5f, 1f, 1.5f, 1.5f} :
                new float[]{3f, 1f, 1.5f, 1.5f, 1.5f};

        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.setSpacingAfter(15f);

        Color headerColor = parseColor(template.getAccentColor());
        Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
        Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        // Header row
        addTableHeader(table, "Article", headerColor, headerFont);
        addTableHeader(table, "Qté", headerColor, headerFont);
        addTableHeader(table, "Prix Unit.", headerColor, headerFont);
        if (template.getShowDiscountColumn()) {
            addTableHeader(table, "Remise", headerColor, headerFont);
        }
        addTableHeader(table, "TVA", headerColor, headerFont);
        addTableHeader(table, "Total", headerColor, headerFont);

        // Data rows
        NumberFormat currencyFormat = getCurrencyFormat(companySettings);
        boolean alternate = false;

        for (InvoiceItem item : invoice.getItems()) {
            Color bgColor = alternate ? new Color(250, 250, 250) : Color.WHITE;
            alternate = !alternate;

            addTableCell(table, item.getPart().getName(), cellFont, Element.ALIGN_LEFT, bgColor);
            addTableCell(table, String.valueOf(item.getQuantity()), cellFont, Element.ALIGN_CENTER, bgColor);
            addTableCell(table, currencyFormat.format(item.getUnitPrice()), cellFont, Element.ALIGN_RIGHT, bgColor);

            if (template.getShowDiscountColumn()) {
                String discount = item.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0 ?
                        item.getDiscountPercent() + "%" : "-";
                addTableCell(table, discount, cellFont, Element.ALIGN_CENTER, bgColor);
            }

            BigDecimal taxAmount = calculateItemTax(item, companySettings.getDefaultTaxRate());
            addTableCell(table, currencyFormat.format(taxAmount), cellFont, Element.ALIGN_RIGHT, bgColor);
            addTableCell(table, currencyFormat.format(item.getTotalPrice()), cellFont, Element.ALIGN_RIGHT, bgColor);
        }

        document.add(table);
    }

    private void addClassicTotals(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                  InvoiceTemplate template) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(50);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setWidths(new float[]{2f, 1f});
        totalsTable.setSpacingAfter(15f);

        Font labelFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
        Font valueFont = new Font(Font.HELVETICA, 9, Font.BOLD);
        Font totalFont = new Font(Font.HELVETICA, 11, Font.BOLD, parseColor(template.getAccentColor()));

        NumberFormat currencyFormat = getCurrencyFormat(companySettings);

        addTotalRow(totalsTable, "Sous-total:", currencyFormat.format(invoice.getSubtotal()), labelFont, valueFont, false);

        if (invoice.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            addTotalRow(totalsTable, "Remise:", "- " + currencyFormat.format(invoice.getDiscountAmount()),
                    labelFont, valueFont, false);
        }

        addTotalRow(totalsTable, "TVA:", currencyFormat.format(invoice.getTaxAmount()), labelFont, valueFont, false);

        if (invoice.getDepositDeduction().compareTo(BigDecimal.ZERO) > 0) {
            addTotalRow(totalsTable, "Acompte déduit:", "- " + currencyFormat.format(invoice.getDepositDeduction()),
                    labelFont, valueFont, false);
        }

        addTotalRow(totalsTable, "TOTAL:", currencyFormat.format(invoice.getTotalAmount()), totalFont, totalFont, true);

        document.add(totalsTable);

        // Add amount in words
        String amountInWords = NumberToWordsConverter.convertToWordsWithSymbol(
                invoice.getTotalAmount(),
                companySettings.getCurrencySymbol(),
                companySettings.getCurrencyDecimals()
        );

        Font amountWordsFont = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.DARK_GRAY);
        Paragraph amountWordsParagraph = new Paragraph("Arrêté à: " + amountInWords, amountWordsFont);
        amountWordsParagraph.setAlignment(Element.ALIGN_RIGHT);
        amountWordsParagraph.setSpacingBefore(5f);
        amountWordsParagraph.setSpacingAfter(15f);
        document.add(amountWordsParagraph);
    }

    // ==================== MODERN DESIGN ====================
    private void renderModernDesign(Document document, PdfWriter writer, Invoice invoice,
                                    CompanySettingsResponse companySettings, InvoiceTemplate template)
            throws DocumentException, IOException {
        // Modern: Clean lines, bold typography, minimalist
        addModernHeader(document, invoice, companySettings, template);
        addModernInvoiceInfo(document, invoice, companySettings, template);
        addModernItemsTable(document, invoice, companySettings, template);
        addModernTotals(document, invoice, companySettings, template);
        addNotes(document, invoice, template);
        addFooter(document, invoice, template);
    }

    private void addModernHeader(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                 InvoiceTemplate template) throws DocumentException {
        // Full-width accent color bar
        PdfPTable colorBar = new PdfPTable(1);
        colorBar.setWidthPercentage(100);
        colorBar.setSpacingAfter(0);

        PdfPCell barCell = new PdfPCell();
        barCell.setBackgroundColor(parseColor(template.getAccentColor()));
        barCell.setFixedHeight(10f);
        barCell.setBorder(Rectangle.NO_BORDER);
        colorBar.addCell(barCell);
        document.add(colorBar);

        // Company name and invoice type on same line
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 1});
        headerTable.setSpacingBefore(15f);
        headerTable.setSpacingAfter(10f);

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        // Add logo if available
        Image logo = loadLogo(invoice, template);
        if (logo != null) {
            Paragraph logoParagraph = new Paragraph();
            logoParagraph.add(new Chunk(logo, 0, 0));
            logoParagraph.setSpacingAfter(5f);
            leftCell.addElement(logoParagraph);
        }

        Font companyFont = new Font(Font.HELVETICA, 14, Font.BOLD, parseColor(template.getPrimaryColor()));
        Paragraph company = new Paragraph(companySettings.getCompanyName() != null ?
                companySettings.getCompanyName() : "Company Name", companyFont);
        leftCell.addElement(company);
        headerTable.addCell(leftCell);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        if (invoice.getInvoiceType() != InvoiceType.STANDARD) {
            Font typeFont = new Font(Font.HELVETICA, 28, Font.BOLD, parseColor(template.getAccentColor()));
            Paragraph invoiceType = new Paragraph(getInvoiceTypeLabel(invoice.getInvoiceType()), typeFont);
            invoiceType.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(invoiceType);
        }
        headerTable.addCell(rightCell);

        document.add(headerTable);

        // Single line separator
        addLineSeparator(document, parseColor(template.getAccentColor()), 1f);
    }

    private void addModernInvoiceInfo(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                      InvoiceTemplate template) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(3);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1.5f, 1.5f, 1f});
        infoTable.setSpacingBefore(15f);
        infoTable.setSpacingAfter(20f);

        Font labelFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY);
        Font valueFont = new Font(Font.HELVETICA, 10, Font.BOLD);

        // Company details
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(Rectangle.NO_BORDER);
        companyCell.setPadding(8f);
        Paragraph companyDetails = new Paragraph();
        companyDetails.add(new Chunk("DE\n", labelFont));
        Font normalFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
        addCompanyAddress(companyDetails, companySettings, normalFont);
        companyCell.addElement(companyDetails);
        infoTable.addCell(companyCell);

        // Customer details
        PdfPCell customerCell = new PdfPCell();
        customerCell.setBorder(Rectangle.NO_BORDER);
        customerCell.setPadding(8f);
        Paragraph customerDetails = new Paragraph();
        customerDetails.add(new Chunk("POUR\n", labelFont));
        addCustomerInfo(customerDetails, invoice, template, normalFont);
        customerCell.addElement(customerDetails);
        infoTable.addCell(customerCell);

        // Invoice details
        PdfPCell detailsCell = new PdfPCell();
        detailsCell.setBorder(Rectangle.NO_BORDER);
        detailsCell.setPadding(8f);
        Paragraph invoiceDetails = new Paragraph();
        invoiceDetails.add(new Chunk("DÉTAILS\n", labelFont));
        invoiceDetails.add(new Chunk(invoice.getInvoiceNumber() + "\n", valueFont));
        invoiceDetails.add(new Chunk(invoice.getIssuedDate().format(DATE_FORMATTER) + "\n", normalFont));
        if (invoice.getDueDate() != null && template.getShowPaymentTerms()) {
            invoiceDetails.add(new Chunk("Échéance: " + invoice.getDueDate().format(DATE_FORMATTER), normalFont));
        }
        detailsCell.addElement(invoiceDetails);
        infoTable.addCell(detailsCell);

        document.add(infoTable);
    }

    private void addModernItemsTable(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                     InvoiceTemplate template) throws DocumentException {
        int columnCount = template.getShowDiscountColumn() ? 5 : 4;
        float[] widths = template.getShowDiscountColumn() ?
                new float[]{3f, 1f, 1.5f, 1f, 1.5f} :
                new float[]{3f, 1f, 1.5f, 1.5f};

        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.setSpacingAfter(15f);

        Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD, parseColor(template.getPrimaryColor()));
        Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        // Header with bottom border only
        addModernTableHeader(table, "Article", headerFont);
        addModernTableHeader(table, "Qté", headerFont);
        addModernTableHeader(table, "Prix", headerFont);
        if (template.getShowDiscountColumn()) {
            addModernTableHeader(table, "Remise", headerFont);
        }
        addModernTableHeader(table, "Total", headerFont);

        NumberFormat currencyFormat = getCurrencyFormat(companySettings);

        for (InvoiceItem item : invoice.getItems()) {
            addModernTableCell(table, item.getPart().getName(), cellFont, Element.ALIGN_LEFT);
            addModernTableCell(table, String.valueOf(item.getQuantity()), cellFont, Element.ALIGN_CENTER);
            addModernTableCell(table, currencyFormat.format(item.getUnitPrice()), cellFont, Element.ALIGN_RIGHT);

            if (template.getShowDiscountColumn()) {
                String discount = item.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0 ?
                        item.getDiscountPercent() + "%" : "-";
                addModernTableCell(table, discount, cellFont, Element.ALIGN_CENTER);
            }

            addModernTableCell(table, currencyFormat.format(item.getTotalPrice()), cellFont, Element.ALIGN_RIGHT);
        }

        document.add(table);
    }

    private void addModernTotals(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                 InvoiceTemplate template) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(40);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setWidths(new float[]{1.5f, 1f});
        totalsTable.setSpacingAfter(15f);

        Font labelFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
        Font totalFont = new Font(Font.HELVETICA, 16, Font.BOLD, parseColor(template.getAccentColor()));

        NumberFormat currencyFormat = getCurrencyFormat(companySettings);

        // Only show total in modern design
        PdfPCell labelCell = new PdfPCell(new Phrase("TOTAL", labelFont));
        labelCell.setBorder(Rectangle.TOP);
        labelCell.setBorderWidthTop(2f);
        labelCell.setBorderColorTop(parseColor(template.getAccentColor()));
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(10f);
        totalsTable.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(currencyFormat.format(invoice.getTotalAmount()), totalFont));
        valueCell.setBorder(Rectangle.TOP);
        valueCell.setBorderWidthTop(2f);
        valueCell.setBorderColorTop(parseColor(template.getAccentColor()));
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(10f);
        totalsTable.addCell(valueCell);

        document.add(totalsTable);

        // Add amount in words
        String amountInWords = NumberToWordsConverter.convertToWordsWithSymbol(
                invoice.getTotalAmount(),
                companySettings.getCurrencySymbol(),
                companySettings.getCurrencyDecimals()
        );

        Font amountWordsFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);
        Paragraph amountWordsParagraph = new Paragraph("Arrêté à: " + amountInWords, amountWordsFont);
        amountWordsParagraph.setAlignment(Element.ALIGN_RIGHT);
        amountWordsParagraph.setSpacingBefore(8f);
        amountWordsParagraph.setSpacingAfter(15f);
        document.add(amountWordsParagraph);
    }

    // ==================== ELEGANT DESIGN ====================
    private void renderElegantDesign(Document document, PdfWriter writer, Invoice invoice,
                                     CompanySettingsResponse companySettings, InvoiceTemplate template)
            throws DocumentException, IOException {
        // Elegant: Decorative borders, refined spacing, sophisticated
        addElegantBorder(document);
        addElegantHeader(document, invoice, companySettings, template);
        addElegantDetails(document, invoice, companySettings, template);
        addElegantItemsTable(document, invoice, companySettings, template);
        addElegantTotals(document, invoice, companySettings, template);
        addNotes(document, invoice, template);
        addElegantFooter(document, invoice, template);
    }

    private void addElegantBorder(Document document) throws DocumentException {
        // Decorative top border
        PdfPTable borderTable = new PdfPTable(1);
        borderTable.setWidthPercentage(100);
        borderTable.setSpacingAfter(10f);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderWidthBottom(2f);
        cell.setBorderColor(new Color(180, 180, 180));
        cell.setFixedHeight(5f);
        borderTable.addCell(cell);

        document.add(borderTable);
    }

    private void addElegantHeader(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                  InvoiceTemplate template) throws DocumentException {
        // Centered header with decorative elements
        // Add logo if available
        Image logo = loadLogo(invoice, template);
        if (logo != null) {
            Paragraph logoParagraph = new Paragraph();
            logoParagraph.setAlignment(Element.ALIGN_CENTER);
            logoParagraph.add(new Chunk(logo, 0, 0));
            logoParagraph.setSpacingAfter(10f);
            document.add(logoParagraph);
        }

        Font companyFont = new Font(Font.TIMES_ROMAN, 22, Font.BOLD, parseColor(template.getPrimaryColor()));
        Paragraph companyName = new Paragraph(companySettings.getCompanyName() != null ?
                companySettings.getCompanyName() : "Company Name", companyFont);
        companyName.setAlignment(Element.ALIGN_CENTER);
        companyName.setSpacingAfter(5f);
        document.add(companyName);

        Font detailsFont = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL, Color.DARK_GRAY);
        Paragraph companyDetails = new Paragraph();
        companyDetails.setAlignment(Element.ALIGN_CENTER);
        companyDetails.setFont(detailsFont);

        if (companySettings.getStreet() != null) {
            companyDetails.add(companySettings.getStreet() + " • ");
        }
        if (companySettings.getCity() != null) {
            companyDetails.add(companySettings.getCity() + " • ");
        }
        if (companySettings.getPhone() != null) {
            companyDetails.add(companySettings.getPhone());
        }

        companyDetails.setSpacingAfter(15f);
        document.add(companyDetails);

        // Invoice type with decorative underline (only if not STANDARD)
        if (invoice.getInvoiceType() != InvoiceType.STANDARD) {
            Font typeFont = new Font(Font.TIMES_ROMAN, 20, Font.BOLD, parseColor(template.getAccentColor()));
            Paragraph invoiceType = new Paragraph(getInvoiceTypeLabel(invoice.getInvoiceType()), typeFont);
            invoiceType.setAlignment(Element.ALIGN_CENTER);
            invoiceType.setSpacingAfter(3f);
            document.add(invoiceType);

            addLineSeparator(document, parseColor(template.getAccentColor()), 1.5f);
        }
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
    }

    private void addElegantDetails(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                   InvoiceTemplate template) throws DocumentException {
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setWidths(new float[]{1, 1});
        detailsTable.setSpacingAfter(20f);

        Font labelFont = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
        Font valueFont = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);

        // Left - Customer
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.BOX);
        leftCell.setPadding(12f);
        leftCell.setBorderColor(new Color(200, 200, 200));

        Paragraph customerInfo = new Paragraph();
        customerInfo.add(new Chunk("Facturé à\n", new Font(Font.TIMES_ROMAN, 11, Font.BOLD, parseColor(template.getAccentColor()))));
        customerInfo.add(new Chunk("\n", valueFont));
        customerInfo.add(new Chunk(invoice.getCustomer().getName() + "\n", new Font(Font.TIMES_ROMAN, 10, Font.BOLD)));
        addCustomerInfo(customerInfo, invoice, template, valueFont);
        leftCell.addElement(customerInfo);
        detailsTable.addCell(leftCell);

        // Right - Invoice details
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.BOX);
        rightCell.setPadding(12f);
        rightCell.setBorderColor(new Color(200, 200, 200));

        Paragraph invoiceInfo = new Paragraph();
        invoiceInfo.add(new Chunk("Détails de la facture\n", new Font(Font.TIMES_ROMAN, 11, Font.BOLD, parseColor(template.getAccentColor()))));
        invoiceInfo.add(new Chunk("\n", valueFont));
        invoiceInfo.add(new Chunk("Numéro: ", labelFont));
        invoiceInfo.add(new Chunk(invoice.getInvoiceNumber() + "\n", valueFont));
        invoiceInfo.add(new Chunk("Date: ", labelFont));
        invoiceInfo.add(new Chunk(invoice.getIssuedDate().format(DATE_FORMATTER) + "\n", valueFont));

        if (invoice.getDueDate() != null && template.getShowPaymentTerms()) {
            invoiceInfo.add(new Chunk("Échéance: ", labelFont));
            invoiceInfo.add(new Chunk(invoice.getDueDate().format(DATE_FORMATTER) + "\n", valueFont));
        }

        invoiceInfo.add(new Chunk("Statut: ", labelFont));
        invoiceInfo.add(new Chunk(getStatusLabel(invoice.getStatus()), valueFont));

        rightCell.addElement(invoiceInfo);
        detailsTable.addCell(rightCell);

        document.add(detailsTable);
    }

    private void addElegantItemsTable(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                      InvoiceTemplate template) throws DocumentException {
        int columnCount = template.getShowDiscountColumn() ? 5 : 4;
        float[] widths = template.getShowDiscountColumn() ?
                new float[]{3f, 1f, 1.5f, 1f, 1.5f} :
                new float[]{3f, 1f, 1.5f, 1.5f};

        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.setSpacingAfter(15f);

        Color headerBg = new Color(240, 240, 245);
        Font headerFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, parseColor(template.getPrimaryColor()));
        Font cellFont = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);

        // Header
        addElegantTableHeader(table, "Description", headerFont, headerBg);
        addElegantTableHeader(table, "Quantité", headerFont, headerBg);
        addElegantTableHeader(table, "Prix unitaire", headerFont, headerBg);
        if (template.getShowDiscountColumn()) {
            addElegantTableHeader(table, "Remise", headerFont, headerBg);
        }
        addElegantTableHeader(table, "Montant", headerFont, headerBg);

        NumberFormat currencyFormat = getCurrencyFormat(companySettings);

        for (InvoiceItem item : invoice.getItems()) {
            addElegantTableCell(table, item.getPart().getName(), cellFont, Element.ALIGN_LEFT);
            addElegantTableCell(table, String.valueOf(item.getQuantity()), cellFont, Element.ALIGN_CENTER);
            addElegantTableCell(table, currencyFormat.format(item.getUnitPrice()), cellFont, Element.ALIGN_RIGHT);

            if (template.getShowDiscountColumn()) {
                String discount = item.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0 ?
                        item.getDiscountPercent() + "%" : "-";
                addElegantTableCell(table, discount, cellFont, Element.ALIGN_CENTER);
            }

            addElegantTableCell(table, currencyFormat.format(item.getTotalPrice()), cellFont, Element.ALIGN_RIGHT);
        }

        document.add(table);
    }

    private void addElegantTotals(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                  InvoiceTemplate template) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(45);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setWidths(new float[]{2f, 1f});
        totalsTable.setSpacingAfter(15f);

        Font labelFont = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
        Font valueFont = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
        Font totalFont = new Font(Font.TIMES_ROMAN, 12, Font.BOLD, parseColor(template.getAccentColor()));

        NumberFormat currencyFormat = getCurrencyFormat(companySettings);

        addElegantTotalRow(totalsTable, "Sous-total", currencyFormat.format(invoice.getSubtotal()), labelFont, valueFont, false);

        if (invoice.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            addElegantTotalRow(totalsTable, "Remise", "- " + currencyFormat.format(invoice.getDiscountAmount()),
                    labelFont, valueFont, false);
        }

        addElegantTotalRow(totalsTable, "TVA", currencyFormat.format(invoice.getTaxAmount()), labelFont, valueFont, false);

        if (invoice.getDepositDeduction().compareTo(BigDecimal.ZERO) > 0) {
            addElegantTotalRow(totalsTable, "Acompte déduit", "- " + currencyFormat.format(invoice.getDepositDeduction()),
                    labelFont, valueFont, false);
        }

        addElegantTotalRow(totalsTable, "MONTANT TOTAL", currencyFormat.format(invoice.getTotalAmount()),
                totalFont, totalFont, true);

        document.add(totalsTable);

        // Add amount in words
        String amountInWords = NumberToWordsConverter.convertToWordsWithSymbol(
                invoice.getTotalAmount(),
                companySettings.getCurrencySymbol(),
                companySettings.getCurrencyDecimals()
        );

        Font amountWordsFont = new Font(Font.TIMES_ROMAN, 9, Font.ITALIC, new Color(80, 80, 80));
        Paragraph amountWordsParagraph = new Paragraph("Arrêté à: " + amountInWords, amountWordsFont);
        amountWordsParagraph.setAlignment(Element.ALIGN_RIGHT);
        amountWordsParagraph.setSpacingBefore(8f);
        amountWordsParagraph.setSpacingAfter(15f);
        document.add(amountWordsParagraph);
    }

    private void addElegantFooter(Document document, Invoice invoice, InvoiceTemplate template) throws DocumentException {
        // Add stamp if available
        com.lowagie.text.Image stamp = loadStamp(invoice, template);
        if (stamp != null) {
            Paragraph stampParagraph = new Paragraph();
            stampParagraph.setAlignment(Element.ALIGN_RIGHT);
            stampParagraph.add(new Chunk(stamp, 0, 0));
            stampParagraph.setSpacingBefore(20f);
            stampParagraph.setSpacingAfter(10f);
            document.add(stampParagraph);
        }

        if (template.getDefaultNotes() != null && !template.getDefaultNotes().isEmpty()) {
            addLineSeparator(document, new Color(200, 200, 200), 1f);

            Font footerFont = new Font(Font.TIMES_ROMAN, 8, Font.ITALIC, Color.DARK_GRAY);
            Paragraph footer = new Paragraph(template.getDefaultNotes(), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(10f);
            document.add(footer);
        }
    }

    // ==================== COMPACT DESIGN ====================
    private void renderCompactDesign(Document document, PdfWriter writer, Invoice invoice,
                                     CompanySettingsResponse companySettings, InvoiceTemplate template)
            throws DocumentException, IOException {
        // Compact: Space-efficient, smaller fonts, tighter spacing
        addCompactHeader(document, invoice, companySettings, template);
        addCompactItemsTable(document, invoice, companySettings, template);
        addCompactTotals(document, invoice, companySettings, template);
        if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) {
            addCompactNotes(document, invoice);
        }
        addCompactFooter(document, invoice, template);
    }

    private void addCompactHeader(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                  InvoiceTemplate template) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1.5f, 2f, 1.5f});
        headerTable.setSpacingAfter(10f);

        Font smallFont = new Font(Font.HELVETICA, 7, Font.NORMAL);
        Font smallBoldFont = new Font(Font.HELVETICA, 7, Font.BOLD);
        Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD, parseColor(template.getAccentColor()));

        // Company info
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(Rectangle.NO_BORDER);
        companyCell.setPadding(2f);

        Paragraph company = new Paragraph();

        // Add logo if available (smaller for compact design)
        Image logo = loadLogo(invoice, template);
        if (logo != null) {
            logo.scaleToFit(80, 40); // Smaller logo for compact design
            company.add(new Chunk(logo, 0, 0));
            company.add(Chunk.NEWLINE);
        }

        company.add(new Chunk(companySettings.getCompanyName() != null ?
                companySettings.getCompanyName() : "Company", new Font(Font.HELVETICA, 9, Font.BOLD)));
        company.add(Chunk.NEWLINE);

        if (companySettings.getPhone() != null) {
            company.add(new Chunk(companySettings.getPhone() + "\n", smallFont));
        }
        if (companySettings.getEmail() != null) {
            company.add(new Chunk(companySettings.getEmail(), smallFont));
        }

        companyCell.addElement(company);
        headerTable.addCell(companyCell);

        // Customer info
        PdfPCell customerCell = new PdfPCell();
        customerCell.setBorder(Rectangle.NO_BORDER);
        customerCell.setPadding(2f);

        Paragraph customer = new Paragraph();
        customer.add(new Chunk("FACTURER À: ", smallBoldFont));
        customer.add(new Chunk(invoice.getCustomer().getName() + "\n", smallFont));

        if (invoice.getCustomer().getCity() != null) {
            customer.add(new Chunk(invoice.getCustomer().getCity(), smallFont));
        }

        customerCell.addElement(customer);
        headerTable.addCell(customerCell);

        // Invoice details
        PdfPCell detailsCell = new PdfPCell();
        detailsCell.setBorder(Rectangle.NO_BORDER);
        detailsCell.setPadding(2f);
        detailsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph details = new Paragraph();
        details.setAlignment(Element.ALIGN_RIGHT);
        if (invoice.getInvoiceType() != InvoiceType.STANDARD) {
            details.add(new Chunk(getInvoiceTypeLabel(invoice.getInvoiceType()) + "\n", titleFont));
        }
        details.add(new Chunk(invoice.getInvoiceNumber() + "\n", smallBoldFont));
        details.add(new Chunk(invoice.getIssuedDate().format(DATE_FORMATTER), smallFont));

        detailsCell.addElement(details);
        headerTable.addCell(detailsCell);

        document.add(headerTable);
        addLineSeparator(document, Color.LIGHT_GRAY, 0.5f);
    }

    private void addCompactItemsTable(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                      InvoiceTemplate template) throws DocumentException {
        int columnCount = 4; // Compact: no discount column shown separately
        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 0.8f, 1.2f, 1.2f});
        table.setSpacingBefore(8f);
        table.setSpacingAfter(8f);

        Font headerFont = new Font(Font.HELVETICA, 7, Font.BOLD);
        Font cellFont = new Font(Font.HELVETICA, 7, Font.NORMAL);

        // Header
        addCompactTableHeader(table, "Article", headerFont);
        addCompactTableHeader(table, "Qté", headerFont);
        addCompactTableHeader(table, "Prix", headerFont);
        addCompactTableHeader(table, "Total", headerFont);

        NumberFormat currencyFormat = getCurrencyFormat(companySettings);

        for (InvoiceItem item : invoice.getItems()) {
            addCompactTableCell(table, item.getPart().getName(), cellFont, Element.ALIGN_LEFT);
            addCompactTableCell(table, String.valueOf(item.getQuantity()), cellFont, Element.ALIGN_CENTER);
            addCompactTableCell(table, currencyFormat.format(item.getUnitPrice()), cellFont, Element.ALIGN_RIGHT);
            addCompactTableCell(table, currencyFormat.format(item.getTotalPrice()), cellFont, Element.ALIGN_RIGHT);
        }

        document.add(table);
        addLineSeparator(document, Color.LIGHT_GRAY, 0.5f);
    }

    private void addCompactTotals(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                  InvoiceTemplate template) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(40);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setWidths(new float[]{1.5f, 1f});
        totalsTable.setSpacingBefore(8f);

        Font labelFont = new Font(Font.HELVETICA, 7, Font.NORMAL);
        Font valueFont = new Font(Font.HELVETICA, 7, Font.BOLD);
        Font totalFont = new Font(Font.HELVETICA, 10, Font.BOLD, parseColor(template.getAccentColor()));

        NumberFormat currencyFormat = getCurrencyFormat(companySettings);

        addCompactTotalRow(totalsTable, "Sous-total:", currencyFormat.format(invoice.getSubtotal()), labelFont, valueFont);
        addCompactTotalRow(totalsTable, "TVA:", currencyFormat.format(invoice.getTaxAmount()), labelFont, valueFont);

        PdfPCell labelCell = new PdfPCell(new Phrase("TOTAL:", totalFont));
        labelCell.setBorder(Rectangle.TOP);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(4f);
        totalsTable.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(currencyFormat.format(invoice.getTotalAmount()), totalFont));
        valueCell.setBorder(Rectangle.TOP);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(4f);
        totalsTable.addCell(valueCell);

        document.add(totalsTable);

        // Add amount in words
        String amountInWords = NumberToWordsConverter.convertToWordsWithSymbol(
                invoice.getTotalAmount(),
                companySettings.getCurrencySymbol(),
                companySettings.getCurrencyDecimals()
        );

        Font amountWordsFont = new Font(Font.HELVETICA, 7, Font.ITALIC, Color.DARK_GRAY);
        Paragraph amountWordsParagraph = new Paragraph("Arrêté à: " + amountInWords, amountWordsFont);
        amountWordsParagraph.setAlignment(Element.ALIGN_RIGHT);
        amountWordsParagraph.setSpacingBefore(5f);
        amountWordsParagraph.setSpacingAfter(10f);
        document.add(amountWordsParagraph);
    }

    private void addCompactNotes(Document document, Invoice invoice) throws DocumentException {
        Font notesFont = new Font(Font.HELVETICA, 7, Font.ITALIC, Color.DARK_GRAY);
        Paragraph notes = new Paragraph(invoice.getNotes(), notesFont);
        notes.setSpacingBefore(10f);
        document.add(notes);
    }

    private void addCompactFooter(Document document, Invoice invoice, InvoiceTemplate template) throws DocumentException {
        // Add stamp if available
        com.lowagie.text.Image stamp = loadStamp(invoice, template);
        if (stamp != null) {
            stamp.scaleToFit(80, 80); // Smaller stamp for compact design
            Paragraph stampParagraph = new Paragraph();
            stampParagraph.setAlignment(Element.ALIGN_RIGHT);
            stampParagraph.add(new Chunk(stamp, 0, 0));
            stampParagraph.setSpacingBefore(10f);
            stampParagraph.setSpacingAfter(5f);
            document.add(stampParagraph);
        }

        if (template.getDefaultNotes() != null && !template.getDefaultNotes().isEmpty()) {
            Font footerFont = new Font(Font.HELVETICA, 6, Font.ITALIC, Color.GRAY);
            Paragraph footer = new Paragraph(template.getDefaultNotes(), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(5f);
            document.add(footer);
        }
    }

    // ==================== PROFESSIONAL DESIGN ====================
    private void renderProfessionalDesign(Document document, PdfWriter writer, Invoice invoice,
                                          CompanySettingsResponse companySettings, InvoiceTemplate template)
            throws DocumentException, IOException {
        // Professional: Corporate structure, clear sections, formal
        addProfessionalHeader(document, invoice, companySettings, template);
        addProfessionalSummary(document, invoice, companySettings, template);
        addProfessionalItemsTable(document, invoice, companySettings, template);
        addProfessionalTotalsAndPayment(document, invoice, companySettings, template);
        addProfessionalFooter(document, invoice, template);
    }

    private void addProfessionalHeader(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                       InvoiceTemplate template) throws DocumentException {
        // Header with structured layout
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 1});
        headerTable.setSpacingAfter(15f);

        Font companyFont = new Font(Font.HELVETICA, 16, Font.BOLD, parseColor(template.getPrimaryColor()));
        Font normalFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

        // Company details
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(Rectangle.NO_BORDER);

        Paragraph companyInfo = new Paragraph();

        // Add logo if available
        Image logo = loadLogo(invoice, template);
        if (logo != null) {
            companyInfo.add(new Chunk(logo, 0, 0));
            companyInfo.add(Chunk.NEWLINE);
            companyInfo.add(Chunk.NEWLINE);
        }

        companyInfo.add(new Chunk(companySettings.getCompanyName() != null ?
                companySettings.getCompanyName() : "Company Name", companyFont));
        companyInfo.add(Chunk.NEWLINE);
        companyInfo.add(Chunk.NEWLINE);
        addCompanyAddress(companyInfo, companySettings, normalFont);
        addCompanyRegistration(companyInfo, invoice, companySettings, template, normalFont);

        companyCell.addElement(companyInfo);
        headerTable.addCell(companyCell);

        // Invoice type box
        PdfPCell typeCell = new PdfPCell();
        typeCell.setBorder(Rectangle.BOX);
        typeCell.setBorderWidth(2f);
        typeCell.setBorderColor(parseColor(template.getAccentColor()));
        typeCell.setPadding(15f);
        typeCell.setBackgroundColor(new Color(248, 249, 250));

        if (invoice.getInvoiceType() != InvoiceType.STANDARD) {
            Font typeFont = new Font(Font.HELVETICA, 22, Font.BOLD, parseColor(template.getAccentColor()));
            Paragraph invoiceType = new Paragraph(getInvoiceTypeLabel(invoice.getInvoiceType()), typeFont);
            invoiceType.setAlignment(Element.ALIGN_CENTER);
            typeCell.addElement(invoiceType);
        }

        Paragraph invoiceNumber = new Paragraph(invoice.getInvoiceNumber(),
                new Font(Font.HELVETICA, 11, Font.BOLD));
        invoiceNumber.setAlignment(Element.ALIGN_CENTER);
        invoiceNumber.setSpacingBefore(invoice.getInvoiceType() != InvoiceType.STANDARD ? 5f : 0f);
        typeCell.addElement(invoiceNumber);

        headerTable.addCell(typeCell);

        document.add(headerTable);
    }

    private void addProfessionalSummary(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                        InvoiceTemplate template) throws DocumentException {
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setWidths(new float[]{1, 1});
        summaryTable.setSpacingAfter(20f);

        Font sectionFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        Font labelFont = new Font(Font.HELVETICA, 8, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

        // Billing Information
        PdfPCell billToCell = new PdfPCell();
        billToCell.setBorder(Rectangle.BOX);
        billToCell.setPadding(0);

        PdfPCell billToHeader = new PdfPCell(new Phrase("INFORMATIONS CLIENT", sectionFont));
        billToHeader.setBackgroundColor(parseColor(template.getAccentColor()));
        billToHeader.setPadding(6f);
        billToHeader.setBorder(Rectangle.NO_BORDER);

        PdfPTable billToContent = new PdfPTable(1);
        billToContent.setWidthPercentage(100);
        billToContent.addCell(billToHeader);

        PdfPCell billToData = new PdfPCell();
        billToData.setBorder(Rectangle.NO_BORDER);
        billToData.setPadding(10f);

        Paragraph customerInfo = new Paragraph();
        customerInfo.add(new Chunk(invoice.getCustomer().getName() + "\n", new Font(Font.HELVETICA, 9, Font.BOLD)));
        addCustomerInfo(customerInfo, invoice, template, valueFont);

        billToData.addElement(customerInfo);
        billToContent.addCell(billToData);
        billToCell.addElement(billToContent);
        summaryTable.addCell(billToCell);

        // Invoice Details
        PdfPCell detailsCell = new PdfPCell();
        detailsCell.setBorder(Rectangle.BOX);
        detailsCell.setPadding(0);

        PdfPCell detailsHeader = new PdfPCell(new Phrase("DÉTAILS DE LA FACTURE", sectionFont));
        detailsHeader.setBackgroundColor(parseColor(template.getAccentColor()));
        detailsHeader.setPadding(6f);
        detailsHeader.setBorder(Rectangle.NO_BORDER);

        PdfPTable detailsContent = new PdfPTable(1);
        detailsContent.setWidthPercentage(100);
        detailsContent.addCell(detailsHeader);

        PdfPCell detailsData = new PdfPCell();
        detailsData.setBorder(Rectangle.NO_BORDER);
        detailsData.setPadding(10f);

        Paragraph invoiceInfo = new Paragraph();
        invoiceInfo.add(new Chunk("Date d'émission: ", labelFont));
        invoiceInfo.add(new Chunk(invoice.getIssuedDate().format(DATE_FORMATTER) + "\n", valueFont));

        if (invoice.getDueDate() != null && template.getShowPaymentTerms()) {
            invoiceInfo.add(new Chunk("Date d'échéance: ", labelFont));
            invoiceInfo.add(new Chunk(invoice.getDueDate().format(DATE_FORMATTER) + "\n", valueFont));
        }

        invoiceInfo.add(new Chunk("Statut: ", labelFont));
        invoiceInfo.add(new Chunk(getStatusLabel(invoice.getStatus()) + "\n", valueFont));

        if (invoice.getSourceWarehouse() != null && template.getShowWarehouseAddress()) {
            invoiceInfo.add(new Chunk("Entrepôt: ", labelFont));
            invoiceInfo.add(new Chunk(invoice.getSourceWarehouse().getName(), valueFont));
        }

        detailsData.addElement(invoiceInfo);
        detailsContent.addCell(detailsData);
        detailsCell.addElement(detailsContent);
        summaryTable.addCell(detailsCell);

        document.add(summaryTable);
    }

    private void addProfessionalItemsTable(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                           InvoiceTemplate template) throws DocumentException {
        int columnCount = template.getShowDiscountColumn() ? 6 : 5;
        float[] widths = template.getShowDiscountColumn() ?
                new float[]{3f, 1f, 1.5f, 1f, 1.5f, 1.5f} :
                new float[]{3f, 1f, 1.5f, 1.5f, 1.5f};

        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.setSpacingAfter(15f);

        Color headerBg = parseColor(template.getPrimaryColor());
        Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
        Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        // Header
        addTableHeader(table, "Description", headerBg, headerFont);
        addTableHeader(table, "Qté", headerBg, headerFont);
        addTableHeader(table, "Prix Unit.", headerBg, headerFont);
        if (template.getShowDiscountColumn()) {
            addTableHeader(table, "Remise", headerBg, headerFont);
        }
        addTableHeader(table, "TVA", headerBg, headerFont);
        addTableHeader(table, "Total HT", headerBg, headerFont);

        NumberFormat currencyFormat = getCurrencyFormat(companySettings);
        boolean alternate = false;

        for (InvoiceItem item : invoice.getItems()) {
            Color bgColor = alternate ? new Color(248, 249, 250) : Color.WHITE;
            alternate = !alternate;

            addTableCell(table, item.getPart().getName(), cellFont, Element.ALIGN_LEFT, bgColor);
            addTableCell(table, String.valueOf(item.getQuantity()), cellFont, Element.ALIGN_CENTER, bgColor);
            addTableCell(table, currencyFormat.format(item.getUnitPrice()), cellFont, Element.ALIGN_RIGHT, bgColor);

            if (template.getShowDiscountColumn()) {
                String discount = item.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0 ?
                        item.getDiscountPercent() + "%" : "-";
                addTableCell(table, discount, cellFont, Element.ALIGN_CENTER, bgColor);
            }

            BigDecimal taxAmount = calculateItemTax(item, companySettings.getDefaultTaxRate());
            addTableCell(table, currencyFormat.format(taxAmount), cellFont, Element.ALIGN_RIGHT, bgColor);
            addTableCell(table, currencyFormat.format(item.getTotalPrice()), cellFont, Element.ALIGN_RIGHT, bgColor);
        }

        document.add(table);
    }

    private void addProfessionalTotalsAndPayment(Document document, Invoice invoice, CompanySettingsResponse companySettings,
                                                 InvoiceTemplate template) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[]{1, 1});
        mainTable.setSpacingAfter(15f);

        // Left - Payment info (if needed)
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPadding(0);

        if (template.getShowPaymentTerms() && invoice.getDueDate() != null) {
            Font infoFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);
            Paragraph paymentInfo = new Paragraph();
            paymentInfo.add(new Chunk("Conditions de paiement\n", new Font(Font.HELVETICA, 9, Font.BOLD)));
            paymentInfo.add(new Chunk("Paiement dû avant le " + invoice.getDueDate().format(DATE_FORMATTER), infoFont));
            leftCell.addElement(paymentInfo);
        }

        mainTable.addCell(leftCell);

        // Right - Totals
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPadding(0);

        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(100);
        totalsTable.setWidths(new float[]{1.5f, 1f});

        Font labelFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
        Font valueFont = new Font(Font.HELVETICA, 9, Font.BOLD);
        Font totalFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);

        NumberFormat currencyFormat = getCurrencyFormat(companySettings);

        addProfessionalTotalRow(totalsTable, "Sous-total HT", currencyFormat.format(invoice.getSubtotal()),
                labelFont, valueFont, false, Color.WHITE);

        if (invoice.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            addProfessionalTotalRow(totalsTable, "Remise", "- " + currencyFormat.format(invoice.getDiscountAmount()),
                    labelFont, valueFont, false, Color.WHITE);
        }

        addProfessionalTotalRow(totalsTable, "TVA", currencyFormat.format(invoice.getTaxAmount()),
                labelFont, valueFont, false, Color.WHITE);

        if (invoice.getDepositDeduction().compareTo(BigDecimal.ZERO) > 0) {
            addProfessionalTotalRow(totalsTable, "Acompte déduit", "- " + currencyFormat.format(invoice.getDepositDeduction()),
                    labelFont, valueFont, false, Color.WHITE);
        }

        addProfessionalTotalRow(totalsTable, "TOTAL TTC", currencyFormat.format(invoice.getTotalAmount()),
                totalFont, totalFont, true, parseColor(template.getAccentColor()));

        rightCell.addElement(totalsTable);
        mainTable.addCell(rightCell);

        document.add(mainTable);

        // Add amount in words
        String amountInWords = NumberToWordsConverter.convertToWordsWithSymbol(
                invoice.getTotalAmount(),
                companySettings.getCurrencySymbol(),
                companySettings.getCurrencyDecimals()
        );

        Font amountWordsFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.DARK_GRAY);
        Paragraph amountWordsParagraph = new Paragraph("Arrêté à: " + amountInWords, amountWordsFont);
        amountWordsParagraph.setAlignment(Element.ALIGN_RIGHT);
        amountWordsParagraph.setSpacingBefore(8f);
        amountWordsParagraph.setSpacingAfter(15f);
        document.add(amountWordsParagraph);
    }

    private void addProfessionalFooter(Document document, Invoice invoice, InvoiceTemplate template)
            throws DocumentException {
        addLineSeparator(document, Color.LIGHT_GRAY, 1f);

        // Add stamp if available
        com.lowagie.text.Image stamp = loadStamp(invoice, template);
        if (stamp != null) {
            Paragraph stampParagraph = new Paragraph();
            stampParagraph.setAlignment(Element.ALIGN_RIGHT);
            stampParagraph.add(new Chunk(stamp, 0, 0));
            stampParagraph.setSpacingBefore(15f);
            stampParagraph.setSpacingAfter(10f);
            document.add(stampParagraph);
        }

        if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) {
            Font notesFont = new Font(Font.HELVETICA, 8, Font.NORMAL);
            Paragraph notes = new Paragraph();
            notes.add(new Chunk("Notes: ", new Font(Font.HELVETICA, 9, Font.BOLD)));
            notes.add(new Chunk(invoice.getNotes(), notesFont));
            notes.setSpacingBefore(10f);
            notes.setSpacingAfter(10f);
            document.add(notes);
        }

        if (template.getDefaultNotes() != null && !template.getDefaultNotes().isEmpty()) {
            Font footerFont = new Font(Font.HELVETICA, 7, Font.ITALIC, Color.GRAY);
            Paragraph footer = new Paragraph(template.getDefaultNotes(), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(5f);
            document.add(footer);
        }
    }

    // ==================== HELPER METHODS ====================

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

    private void addCompanyAddress(Paragraph paragraph, CompanySettingsResponse companySettings, Font font) {
        if (companySettings.getStreet() != null) {
            paragraph.add(new Chunk(companySettings.getStreet() + "\n", font));
        }
        if (companySettings.getCity() != null || companySettings.getPostalCode() != null) {
            String cityLine = "";
            if (companySettings.getPostalCode() != null) cityLine += companySettings.getPostalCode() + " ";
            if (companySettings.getCity() != null) cityLine += companySettings.getCity();
            paragraph.add(new Chunk(cityLine + "\n", font));
        }
        if (companySettings.getCountry() != null) {
            paragraph.add(new Chunk(companySettings.getCountry() + "\n", font));
        }
        if (companySettings.getPhone() != null) {
            paragraph.add(new Chunk("Tél: " + companySettings.getPhone() + "\n", font));
        }
        if (companySettings.getEmail() != null) {
            paragraph.add(new Chunk("Email: " + companySettings.getEmail() + "\n", font));
        }
    }

    private void addCompanyRegistration(Paragraph paragraph, Invoice invoice,
                                        CompanySettingsResponse companySettings,
                                        InvoiceTemplate template, Font font) {
        // Get store info from invoice's source warehouse, fallback to company settings
        String taxId = null;
        String ninea = null;
        String rccm = null;

        if (invoice.getSourceWarehouse() != null && invoice.getSourceWarehouse().getStore() != null) {
            Store store = invoice.getSourceWarehouse().getStore();
            taxId = store.getTaxId();
            ninea = store.getNinea();
            rccm = store.getRccm();
        } else {
            // Fallback to company settings
            taxId = companySettings.getTaxId();
            ninea = companySettings.getNinea();
            rccm = companySettings.getRccm();
        }

        if (template.getShowTaxId() && taxId != null) {
            paragraph.add(new Chunk("TVA: " + taxId + "\n", font));
        }
        if (template.getShowNinea() && ninea != null) {
            paragraph.add(new Chunk("NINEA: " + ninea + "\n", font));
        }
        if (template.getShowRccm() && rccm != null) {
            paragraph.add(new Chunk("RCCM: " + rccm + "\n", font));
        }
    }

    private void addCustomerInfo(Paragraph paragraph, Invoice invoice, InvoiceTemplate template, Font font) {
        paragraph.add(new Chunk(invoice.getCustomer().getName() + "\n", font));
        if (invoice.getCustomer().getCompany() != null) {
            paragraph.add(new Chunk(invoice.getCustomer().getCompany() + "\n", font));
        }
        if (invoice.getCustomer().getStreet() != null) {
            paragraph.add(new Chunk(invoice.getCustomer().getStreet() + "\n", font));
        }
        if (invoice.getCustomer().getCity() != null || invoice.getCustomer().getPostalCode() != null) {
            String cityLine = "";
            if (invoice.getCustomer().getPostalCode() != null) cityLine += invoice.getCustomer().getPostalCode() + " ";
            if (invoice.getCustomer().getCity() != null) cityLine += invoice.getCustomer().getCity();
            paragraph.add(new Chunk(cityLine + "\n", font));
        }
        if (invoice.getCustomer().getCountry() != null) {
            paragraph.add(new Chunk(invoice.getCustomer().getCountry() + "\n", font));
        }
        if (template.getShowCustomerTaxId() && invoice.getCustomer().getTaxId() != null) {
            paragraph.add(new Chunk("TVA: " + invoice.getCustomer().getTaxId() + "\n", font));
        }
    }

    private void addNotes(Document document, Invoice invoice, InvoiceTemplate template) throws DocumentException {
        if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) {
            Font titleFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font notesFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

            Paragraph notesTitle = new Paragraph("Notes:", titleFont);
            notesTitle.setSpacingBefore(10f);
            document.add(notesTitle);

            Paragraph notes = new Paragraph(invoice.getNotes(), notesFont);
            notes.setSpacingAfter(20f);
            document.add(notes);
        }
    }

    private void addFooter(Document document, Invoice invoice, InvoiceTemplate template) throws DocumentException {
        // Add stamp if available
        com.lowagie.text.Image stamp = loadStamp(invoice, template);
        if (stamp != null) {
            Paragraph stampParagraph = new Paragraph();
            stampParagraph.setAlignment(Element.ALIGN_RIGHT);
            stampParagraph.add(new Chunk(stamp, 0, 0));
            stampParagraph.setSpacingBefore(20f);
            stampParagraph.setSpacingAfter(10f);
            document.add(stampParagraph);
        }

        if (template.getDefaultNotes() != null && !template.getDefaultNotes().isEmpty()) {
            Font footerFont = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY);
            Paragraph footer = new Paragraph(template.getDefaultNotes(), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
        }
    }

    private void addLineSeparator(Document document, Color color, float width) throws DocumentException {
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        line.setSpacingAfter(10f);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColorBottom(color);
        cell.setBorderWidthBottom(width);
        cell.setFixedHeight(1f);
        line.addCell(cell);

        document.add(line);
    }

    // Table cell helpers for different designs

    private void addTableHeader(PdfPTable table, String text, Color bgColor, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8f);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font, int align, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6f);
        cell.setBackgroundColor(bgColor);
        table.addCell(cell);
    }

    private void addModernTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderWidthBottom(2f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6f);
        table.addCell(cell);
    }

    private void addModernTableCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(align);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    private void addElegantTableHeader(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8f);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(new Color(200, 200, 200));
        table.addCell(cell);
    }

    private void addElegantTableCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setPadding(6f);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(new Color(220, 220, 220));
        table.addCell(cell);
    }

    private void addCompactTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(3f);
        table.addCell(cell);
    }

    private void addCompactTableCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(align);
        cell.setPadding(2f);
        table.addCell(cell);
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font labelFont,
                             Font valueFont, boolean bold) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(bold ? Rectangle.TOP : Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(5f);
        if (bold) labelCell.setPaddingTop(10f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(bold ? Rectangle.TOP : Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(5f);
        if (bold) valueCell.setPaddingTop(10f);
        table.addCell(valueCell);
    }

    private void addElegantTotalRow(PdfPTable table, String label, String value, Font labelFont,
                                    Font valueFont, boolean isTotal) {
        Color borderColor = isTotal ? parseColor("#4F46E5") : new Color(220, 220, 220);
        int borderType = isTotal ? Rectangle.TOP : Rectangle.NO_BORDER;

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(borderType);
        if (isTotal) {
            labelCell.setBorderWidthTop(2f);
            labelCell.setBorderColor(borderColor);
        }
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(6f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(borderType);
        if (isTotal) {
            valueCell.setBorderWidthTop(2f);
            valueCell.setBorderColor(borderColor);
        }
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(6f);
        table.addCell(valueCell);
    }

    private void addCompactTotalRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(2f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(2f);
        table.addCell(valueCell);
    }

    private void addProfessionalTotalRow(PdfPTable table, String label, String value, Font labelFont,
                                         Font valueFont, boolean isTotal, Color bgColor) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(isTotal ? bgColor : Color.WHITE);
        labelCell.setBorder(isTotal ? Rectangle.NO_BORDER : Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(8f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBackgroundColor(isTotal ? bgColor : Color.WHITE);
        valueCell.setBorder(isTotal ? Rectangle.NO_BORDER : Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(8f);
        table.addCell(valueCell);
    }

    private Color parseColor(String hexColor) {
        try {
            if (hexColor.startsWith("#")) {
                hexColor = hexColor.substring(1);
            }
            int r = Integer.parseInt(hexColor.substring(0, 2), 16);
            int g = Integer.parseInt(hexColor.substring(2, 4), 16);
            int b = Integer.parseInt(hexColor.substring(4, 6), 16);
            return new Color(r, g, b);
        } catch (Exception e) {
            return Color.BLACK;
        }
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
            case PAID -> "Payée";
            case PARTIALLY_PAID -> "Partiellement payée";
            case OVERDUE -> "En retard";
        };
    }

    private BigDecimal calculateItemTax(InvoiceItem item, BigDecimal taxRate) {
        if (taxRate == null) return BigDecimal.ZERO;
        return item.getTotalPrice().multiply(taxRate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    private NumberFormat getCurrencyFormat(CompanySettingsResponse companySettings) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        format.setCurrency(java.util.Currency.getInstance("XOF"));
        format.setMinimumFractionDigits(companySettings.getCurrencyDecimals() != null ?
                companySettings.getCurrencyDecimals() : 0);
        format.setMaximumFractionDigits(companySettings.getCurrencyDecimals() != null ?
                companySettings.getCurrencyDecimals() : 0);
        return format;
    }

    private Image loadLogo(Invoice invoice, InvoiceTemplate template) {
        try {
            String logoUrl = null;

            // Priority 1: Template logo
            if (template != null && template.getLogoUrl() != null && !template.getLogoUrl().isEmpty()) {
                logoUrl = template.getLogoUrl();
            }
            // Priority 2: Store logo (fallback)
            else if (invoice.getSourceWarehouse() != null &&
                    invoice.getSourceWarehouse().getStore() != null &&
                    invoice.getSourceWarehouse().getStore().getLogoUrl() != null) {
                logoUrl = invoice.getSourceWarehouse().getStore().getLogoUrl();
            }

            if (logoUrl != null) {
                byte[] logoBytes = fileStorageService.getFileBytes(extractObjectName(logoUrl));
                Image logo = Image.getInstance(logoBytes);
                logo.scaleToFit(120, 60); // Max width: 120, max height: 60
                return logo;
            }
        } catch (Exception e) {
            log.warn("Could not load logo image: {}", e.getMessage());
        }
        return null;
    }

    private Image loadStamp(Invoice invoice, InvoiceTemplate template) {
        try {
            String stampUrl = null;

            // Priority 1: Template stamp
            if (template != null && template.getStampImageUrl() != null && !template.getStampImageUrl().isEmpty()) {
                stampUrl = template.getStampImageUrl();
            }
            // Priority 2: Store stamp (fallback)
            else if (invoice.getSourceWarehouse() != null &&
                    invoice.getSourceWarehouse().getStore() != null &&
                    invoice.getSourceWarehouse().getStore().getStampImageUrl() != null) {
                stampUrl = invoice.getSourceWarehouse().getStore().getStampImageUrl();
            }

            if (stampUrl != null) {
                byte[] stampBytes = fileStorageService.getFileBytes(extractObjectName(stampUrl));
                Image stamp = Image.getInstance(stampBytes);
                stamp.scaleToFit(100, 100); // Max width/height: 100
                return stamp;
            }
        } catch (Exception e) {
            log.warn("Could not load stamp image: {}", e.getMessage());
        }
        return null;
    }

    private String extractObjectName(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        // Extract object name from URL
        // URL format: http://localhost:9000/spareparts/folder/filename.ext
        int lastSlashIndex = fileUrl.lastIndexOf("/spareparts/");
        if (lastSlashIndex != -1) {
            return fileUrl.substring(lastSlashIndex + "/spareparts/".length());
        }
        return null;
    }
}
