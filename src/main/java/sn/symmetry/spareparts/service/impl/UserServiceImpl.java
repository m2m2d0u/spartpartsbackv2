package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateUserRequest;
import sn.symmetry.spareparts.dto.request.UpdateUserRequest;
import sn.symmetry.spareparts.dto.request.UserWarehouseAssignmentRequest;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.dto.response.UserResponse;
import sn.symmetry.spareparts.dto.response.UserWarehouseAssignmentResponse;
import sn.symmetry.spareparts.entity.User;
import sn.symmetry.spareparts.entity.UserWarehouse;
import sn.symmetry.spareparts.entity.UserWarehousePermission;
import sn.symmetry.spareparts.entity.Warehouse;
import sn.symmetry.spareparts.enums.UserRole;
import sn.symmetry.spareparts.enums.WarehousePermission;
import sn.symmetry.spareparts.exception.DuplicateResourceException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.UserMapper;
import sn.symmetry.spareparts.repository.UserRepository;
import sn.symmetry.spareparts.repository.UserWarehouseRepository;
import sn.symmetry.spareparts.repository.WarehouseRepository;
import sn.symmetry.spareparts.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserWarehouseRepository userWarehouseRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserMapper userMapper;

    @Override
    public PagedResponse<UserResponse> getAllUsers(UserRole role, Boolean isActive, Pageable pageable) {
        Page<User> page;
        if (role != null && isActive != null) {
            page = userRepository.findByRoleAndIsActive(role, isActive, pageable);
        } else if (role != null) {
            page = userRepository.findByRole(role, pageable);
        } else if (isActive != null) {
            page = userRepository.findByIsActive(isActive, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }
        return PagedResponse.of(page.map(userMapper::toResponse));
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        UserResponse response = userMapper.toResponse(user);
        response.setWarehouseAssignments(buildWarehouseAssignments(id));
        return response;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(request.getPassword());
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        userMapper.updateEntity(request, user);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserWarehouses(UUID id, List<UserWarehouseAssignmentRequest> assignments) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Validate all warehouse IDs exist
        for (UserWarehouseAssignmentRequest assignment : assignments) {
            if (!warehouseRepository.existsById(assignment.getWarehouseId())) {
                throw new ResourceNotFoundException("Warehouse", "id", assignment.getWarehouseId());
            }
        }

        // Delete existing assignments (cascade deletes permissions)
        userWarehouseRepository.deleteByUserId(id);
        userWarehouseRepository.flush();

        // Create new assignments
        List<UserWarehouse> newAssignments = new ArrayList<>();
        for (UserWarehouseAssignmentRequest assignment : assignments) {
            Warehouse warehouse = warehouseRepository.getReferenceById(assignment.getWarehouseId());

            UserWarehouse uw = new UserWarehouse();
            uw.setUser(user);
            uw.setWarehouse(warehouse);
            uw.setPermissions(new ArrayList<>());

            for (WarehousePermission perm : assignment.getPermissions()) {
                UserWarehousePermission uwp = new UserWarehousePermission();
                uwp.setUserWarehouse(uw);
                uwp.setPermission(perm);
                uw.getPermissions().add(uwp);
            }

            newAssignments.add(uw);
        }
        userWarehouseRepository.saveAll(newAssignments);

        UserResponse response = userMapper.toResponse(user);
        response.setWarehouseAssignments(buildWarehouseAssignments(id));
        return response;
    }

    private List<UserWarehouseAssignmentResponse> buildWarehouseAssignments(UUID userId) {
        List<UserWarehouse> assignments = userWarehouseRepository.findByUserId(userId);
        return assignments.stream()
                .map(uw -> UserWarehouseAssignmentResponse.builder()
                        .warehouseId(uw.getWarehouse().getId())
                        .warehouseName(uw.getWarehouse().getName())
                        .warehouseCode(uw.getWarehouse().getCode())
                        .permissions(uw.getPermissions().stream()
                                .map(UserWarehousePermission::getPermission)
                                .toList())
                        .build())
                .toList();
    }
}
