package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateUserRequest;
import sn.symmetry.spareparts.dto.request.UpdateUserRequest;
import sn.symmetry.spareparts.dto.request.UserWarehouseAssignmentRequest;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.dto.response.MeResponse;
import sn.symmetry.spareparts.dto.response.UserResponse;
import sn.symmetry.spareparts.enums.UserRole;

import java.util.List;
import java.util.UUID;

public interface UserService {

    PagedResponse<UserResponse> getAllUsers(UserRole role, Boolean isActive, Pageable pageable);

    UserResponse getUserById(UUID id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    void deleteUser(UUID id);

    UserResponse updateUserWarehouses(UUID id, List<UserWarehouseAssignmentRequest> assignments);

    UserResponse updateUserStores(UUID id, List<UUID> storeIds);

    MeResponse getCurrentUserInfo();
}
