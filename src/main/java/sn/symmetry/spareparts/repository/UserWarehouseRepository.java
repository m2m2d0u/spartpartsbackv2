package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.UserWarehouse;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserWarehouseRepository extends JpaRepository<UserWarehouse, UUID> {

    List<UserWarehouse> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
