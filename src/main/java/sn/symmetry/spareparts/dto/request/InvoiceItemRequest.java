package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemRequest {

    @NotNull(message = "Part ID is required")
    private Long partId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0", message = "Unit price must be zero or positive")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0", message = "Discount percent must be zero or positive")
    @DecimalMax(value = "100", message = "Discount percent must not exceed 100")
    private BigDecimal discountPercent;
}
