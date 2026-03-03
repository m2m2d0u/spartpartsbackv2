package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditNoteResponse {

    private Long id;
    private String creditNoteNumber;
    private Long returnId;
    private BigDecimal totalAmount;
    private LocalDate issuedDate;
    private LocalDateTime createdAt;
}
