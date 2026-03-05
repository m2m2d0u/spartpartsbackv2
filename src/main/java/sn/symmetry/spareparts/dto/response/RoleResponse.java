package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.RoleLevel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for Role entity.
 * Implements Serializable for Redis caching.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String code;
    private String displayName;
    private String description;
    private Boolean isSystem;
    private Boolean isSuperAdmin;
    private RoleLevel roleLevel;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PermissionInfo> permissions;
    private Integer permissionCount;
}
