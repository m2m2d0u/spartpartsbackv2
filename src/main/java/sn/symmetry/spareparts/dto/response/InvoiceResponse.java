package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.InvoiceStatus;
import sn.symmetry.spareparts.enums.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private InvoiceType invoiceType;
    private Long customerId;
    private String customerName;
    private Long orderId;
    private Long proformaId;
    private Long depositId;
    private Long templateId;
    private InvoiceStatus status;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal depositDeduction;
    private BigDecimal totalAmount;
    private LocalDate issuedDate;
    private LocalDate dueDate;
    private LocalDate validityDate;
    private LocalDate paidDate;
    private Long sourceWarehouseId;
    private String sourceWarehouseName;
    private String issuerName;
    private String issuerNinea;
    private String issuerRccm;
    private String issuerTaxId;
    private String issuerAddress;
    private String notes;
    private String internalNotes;
    private List<InvoiceItemResponse> items;
    private List<PaymentResponse> payments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
