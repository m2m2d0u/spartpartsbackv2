package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStockResponse {

    private UUID id;
    private UUID warehouseId;
    private String warehouseName;
    private String warehouseCode;
    private UUID partId;
    private String partName;
    private String partNumber;
    private Integer quantity;
    private Integer minStockLevel;
}
