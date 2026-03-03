package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.StockMovementType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {

    private UUID id;
    private UUID partId;
    private String partName;
    private String partNumber;
    private UUID warehouseId;
    private String warehouseName;
    private StockMovementType type;
    private Integer quantityChange;
    private Integer balanceAfter;
    private String referenceType;
    private UUID referenceId;
    private String notes;
    private LocalDateTime createdAt;
}
