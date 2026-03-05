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
 * Response DTO for the /me endpoint.
 * Contains user identity and role info.
 * Permissions are carried in the JWT token.
 * Accessible stores/warehouses are fetched via dedicated endpoints.
 * Implements Serializable for Redis caching.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 7L;

    // Basic user information
    private UUID id;
    private String name;
    private String email;
    private String roleCode;
    private String roleDisplayName;
    private RoleLevel roleLevel;
    private Boolean superAdmin;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Warehouse assignments with permissions (for WAREHOUSE_OPERATOR)
    private List<UserWarehouseAssignmentResponse> warehouseAssignments;
}
