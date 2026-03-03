package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.response.InvoiceItemResponse;
import sn.symmetry.spareparts.dto.response.InvoiceResponse;
import sn.symmetry.spareparts.dto.response.PaymentResponse;
import sn.symmetry.spareparts.entity.Invoice;
import sn.symmetry.spareparts.entity.InvoiceItem;
import sn.symmetry.spareparts.entity.Payment;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface InvoiceMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "proforma.id", target = "proformaId")
    @Mapping(source = "deposit.id", target = "depositId")
    @Mapping(source = "template.id", target = "templateId")
    @Mapping(source = "sourceWarehouse.id", target = "sourceWarehouseId")
    @Mapping(source = "sourceWarehouse.name", target = "sourceWarehouseName")
    @Mapping(source = "items", target = "items")
    @Mapping(source = "payments", target = "payments")
    InvoiceResponse toResponse(Invoice invoice);

    @Mapping(source = "part.id", target = "partId")
    @Mapping(source = "part.name", target = "partName")
    @Mapping(source = "part.partNumber", target = "partNumber")
    InvoiceItemResponse toItemResponse(InvoiceItem item);

    List<InvoiceItemResponse> toItemResponseList(List<InvoiceItem> items);

    @Mapping(source = "invoice.id", target = "invoiceId")
    PaymentResponse toPaymentResponse(Payment payment);

    List<PaymentResponse> toPaymentResponseList(List<Payment> payments);
}
