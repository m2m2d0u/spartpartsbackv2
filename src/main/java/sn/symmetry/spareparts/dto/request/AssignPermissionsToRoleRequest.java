package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for assigning permissions to a role.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignPermissionsToRoleRequest {

    @NotNull(message = "Permission IDs are required")
    private List<UUID> permissionIds;
}
