package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for the /me endpoint.
 * Contains all necessary information about the current user for the frontend,
 * including accessible resources and permissions.
 * Implements Serializable for Redis caching.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 4L;

    // Basic user information
    private UUID id;
    private String name;
    private String email;
    private String roleCode;
    private String roleDisplayName;
    private Boolean superAdmin;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Role-level permission codes (from the user's assigned role)
    private List<String> permissions;

    // Accessible resources
    private List<StoreInfo> accessibleStores;
    private List<WarehouseInfo> accessibleWarehouses;

    // Warehouse assignments with permissions (for WAREHOUSE_OPERATOR)
    private List<UserWarehouseAssignmentResponse> warehouseAssignments;

    /**
     * Simplified store information for the /me response.
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreInfo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
        private UUID id;
        private String code;
        private String name;
        private String street;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String phone;
        private String email;
        private Boolean isActive;
    }

    /**
     * Simplified warehouse information for the /me response.
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseInfo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
        private UUID id;
        private String code;
        private String name;
        private String location;
        private UUID storeId;
        private String storeName;
        private Boolean isActive;
    }
}
