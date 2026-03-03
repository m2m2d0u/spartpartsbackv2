package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for updating a role.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleRequest {

    @NotBlank(message = "Display name is required")
    @Size(max = 255, message = "Display name must not exceed 255 characters")
    private String displayName;

    private String description;

    private Boolean isActive;
}
