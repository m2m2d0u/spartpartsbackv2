package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for user-store assignments.
 * Represents a store that a STORE_MANAGER user can manage.
 * Implements Serializable for Redis caching.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStoreResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID storeId;
    private String storeName;
    private String storeCode;
    private LocalDateTime createdAt;
}
