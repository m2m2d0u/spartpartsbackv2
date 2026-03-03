package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCreditNoteRequest {

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be at least 0.01")
    private BigDecimal totalAmount;

    @NotNull(message = "Issued date is required")
    private LocalDate issuedDate;
}
