package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.WarehouseStock;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Long> {
}
