package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateUserRequest;
import sn.symmetry.spareparts.dto.request.UpdateUserRequest;
import sn.symmetry.spareparts.dto.request.UserWarehouseAssignmentRequest;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.dto.response.UserResponse;
import sn.symmetry.spareparts.enums.UserRole;

import java.util.List;

public interface UserService {

    PagedResponse<UserResponse> getAllUsers(UserRole role, Boolean isActive, Pageable pageable);

    UserResponse getUserById(Long id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    UserResponse updateUserWarehouses(Long id, List<UserWarehouseAssignmentRequest> assignments);
}
