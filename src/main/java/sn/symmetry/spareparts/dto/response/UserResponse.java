package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.RoleLevel;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for User entity.
 * Implements Serializable for Redis caching.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    private UUID id;
    private String name;
    private String email;
    private String roleCode;
    private String roleDisplayName;
    private RoleLevel roleLevel;
    private Boolean isSuperAdmin;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<UserStoreResponse> stores;
    private List<UserWarehouseAssignmentResponse> warehouseAssignments;
}
