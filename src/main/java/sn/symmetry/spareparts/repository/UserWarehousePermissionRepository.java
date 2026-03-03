package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.UserWarehousePermission;

import java.util.UUID;

@Repository
public interface UserWarehousePermissionRepository extends JpaRepository<UserWarehousePermission, UUID> {
}
