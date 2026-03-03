package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class UpdatePartRequest {

    @NotBlank(message = "Part number is required")
    @Size(max = 50, message = "Part number must not exceed 50 characters")
    private String partNumber;

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    private String description;

    @Size(max = 500, message = "Short description must not exceed 500 characters")
    private String shortDescription;

    private Long categoryId;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0", message = "Selling price must be at least 0")
    private BigDecimal sellingPrice;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0", message = "Purchase price must be at least 0")
    private BigDecimal purchasePrice;

    @Min(value = 0, message = "Minimum stock level must be at least 0")
    private Integer minStockLevel;

    private Boolean published;

    private String notes;
}
