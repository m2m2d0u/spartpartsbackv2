package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for updating a user's store assignments.
 * Used to assign STORE_MANAGER users to stores they can manage.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStoresRequest {

    @NotNull(message = "Store IDs list is required")
    private List<UUID> storeIds;
}
