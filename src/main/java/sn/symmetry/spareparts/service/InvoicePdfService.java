package sn.symmetry.spareparts.service;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public interface InvoicePdfService {

    /**
     * Generate PDF for an invoice
     * @param invoiceId the invoice ID
     * @return PDF content as byte array
     */
    ByteArrayOutputStream generateInvoicePdf(UUID invoiceId);
}
