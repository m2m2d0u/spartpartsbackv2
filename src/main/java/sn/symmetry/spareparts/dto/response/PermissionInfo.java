package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.WarehousePermission;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO containing metadata about a warehouse permission.
 * Used to provide frontend with permission details for display and selection.
 * Implements Serializable for Redis caching.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String code;
    private String displayName;
    private String description;
    private String category;
    private String categoryDisplayName;
    private String level;
    private String levelDisplayName;
    private Boolean isActive;
    private boolean isLegacy;
    private boolean isReadOnly;

    /**
     * Convert a WarehousePermission enum to PermissionInfo DTO.
     */
    public static PermissionInfo fromEnum(WarehousePermission permission) {
        return PermissionInfo.builder()
                .code(permission.name())
                .displayName(permission.getDisplayName())
                .description(permission.getDescription())
                .category(permission.getCategory().name())
                .categoryDisplayName(permission.getCategory().getDisplayName())
                .level(permission.getLevel().name())
                .levelDisplayName(permission.getLevel().getDisplayName())
                .isLegacy(permission.isLegacy())
                .isReadOnly(permission.isReadOnly())
                .build();
    }
}
