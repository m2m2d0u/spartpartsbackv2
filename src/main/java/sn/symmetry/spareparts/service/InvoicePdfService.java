package sn.symmetry.spareparts.service;

import sn.symmetry.spareparts.dto.request.CreateInvoiceTemplateRequest;
import sn.symmetry.spareparts.enums.InvoiceDesign;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;

public interface InvoicePdfService {

    /**
     * Generate PDF for an invoice
     * @param invoiceId the invoice ID
     * @return PDF content as byte array
     */
    ByteArrayOutputStream generateInvoicePdf(UUID invoiceId);

    /**
     * Generate a sample PDF preview for a given design using the provided template configuration and uploaded images
     * @param design the invoice design to preview
     * @param request the template configuration to apply
     * @param uploadedImages map of image key ("logo", "stamp") to raw file bytes (may be empty)
     * @return PDF content as byte array
     */
    ByteArrayOutputStream generateDesignPreviewPdf(InvoiceDesign design, CreateInvoiceTemplateRequest request, Map<String, byte[]> uploadedImages);
}
