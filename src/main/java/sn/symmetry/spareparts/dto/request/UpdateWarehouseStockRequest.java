package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWarehouseStockRequest {

    @NotNull(message = "Minimum stock level is required")
    @Min(value = 0, message = "Minimum stock level must be at least 0")
    private Integer minStockLevel;
}
