package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for assigning roles to a user-warehouse combination.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignRolesToUserWarehouseRequest {

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    @NotNull(message = "Role IDs are required")
    private List<UUID> roleIds;
}
