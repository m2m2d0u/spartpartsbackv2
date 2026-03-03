package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.StockMovement;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
}
