package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.StockTransfer;
import sn.symmetry.spareparts.enums.StockTransferStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, UUID> {

    boolean existsByTransferNumber(String transferNumber);

    Page<StockTransfer> findByStatus(StockTransferStatus status, Pageable pageable);

    @Query("SELECT COUNT(st) FROM StockTransfer st WHERE st.status IN :statuses")
    long countByStatusInAll(@Param("statuses") List<StockTransferStatus> statuses);

    @Query("SELECT COUNT(st) FROM StockTransfer st WHERE st.status IN :statuses AND (st.sourceWarehouse.id IN :warehouseIds OR st.destinationWarehouse.id IN :warehouseIds)")
    long countByStatusInByWarehouses(@Param("statuses") List<StockTransferStatus> statuses, @Param("warehouseIds") List<UUID> warehouseIds);
}
