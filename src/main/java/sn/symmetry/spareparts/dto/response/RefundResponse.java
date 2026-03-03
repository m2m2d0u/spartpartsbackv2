package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.RefundMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponse {

    private UUID id;
    private UUID returnId;
    private UUID invoiceId;
    private BigDecimal amount;
    private RefundMethod refundMethod;
    private LocalDate refundDate;
    private String reference;
    private String notes;
    private LocalDateTime createdAt;
}
