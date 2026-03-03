package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaxRateRequest {

    @NotBlank(message = "Label is required")
    @Size(max = 50, message = "Label must not exceed 50 characters")
    private String label;

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0", message = "Rate must be greater than or equal to 0")
    private BigDecimal rate;

    private Boolean isDefault;
}
