package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Response containing all warehouse permissions grouped by category.
 * Provides structured permission data for frontend permission management UI.
 * Implements Serializable for Redis caching.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionsResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<PermissionCategoryGroup> categories;
    private List<PermissionInfo> allPermissions;
    private int totalPermissions;
    private int activePermissions;
    private int legacyPermissions;

    /**
     * Group of permissions within a category.
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionCategoryGroup implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
        private String code;
        private String displayName;
        private String description;
        private List<PermissionInfo> permissions;
        private int count;
    }
}
