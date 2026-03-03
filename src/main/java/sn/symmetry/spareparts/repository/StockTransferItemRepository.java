package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.StockTransferItem;

@Repository
public interface StockTransferItemRepository extends JpaRepository<StockTransferItem, Long> {
}
