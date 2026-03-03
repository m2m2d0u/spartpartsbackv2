package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating a new role.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {

    @NotBlank(message = "Role code is required")
    @Size(max = 100, message = "Role code must not exceed 100 characters")
    private String code;

    @NotBlank(message = "Display name is required")
    @Size(max = 255, message = "Display name must not exceed 255 characters")
    private String displayName;

    private String description;

    private List<UUID> permissionIds;
}
