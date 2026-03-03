package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.UserWarehouse;

import java.util.List;

@Repository
public interface UserWarehouseRepository extends JpaRepository<UserWarehouse, Long> {

    List<UserWarehouse> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
