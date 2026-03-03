package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStockResponse {

    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private String warehouseCode;
    private Long partId;
    private String partName;
    private String partNumber;
    private Integer quantity;
    private Integer minStockLevel;
}
