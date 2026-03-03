package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.WarehousePermission;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWarehouseAssignmentRequest {

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    @NotEmpty(message = "At least one permission is required")
    private List<WarehousePermission> permissions;
}
