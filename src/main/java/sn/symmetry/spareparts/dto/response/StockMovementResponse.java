package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.StockMovementType;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {

    private Long id;
    private Long partId;
    private String partName;
    private String partNumber;
    private Long warehouseId;
    private String warehouseName;
    private StockMovementType type;
    private Integer quantityChange;
    private Integer balanceAfter;
    private String referenceType;
    private Long referenceId;
    private String notes;
    private LocalDateTime createdAt;
}
