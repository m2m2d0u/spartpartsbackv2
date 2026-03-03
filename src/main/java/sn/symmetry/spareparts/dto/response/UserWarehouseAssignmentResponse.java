package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.WarehousePermission;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWarehouseAssignmentResponse {

    private Long warehouseId;
    private String warehouseName;
    private String warehouseCode;
    private List<WarehousePermission> permissions;
}
