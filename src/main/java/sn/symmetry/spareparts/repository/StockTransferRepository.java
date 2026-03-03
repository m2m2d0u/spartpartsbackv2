package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.StockTransfer;
import sn.symmetry.spareparts.enums.StockTransferStatus;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {

    boolean existsByTransferNumber(String transferNumber);

    Page<StockTransfer> findByStatus(StockTransferStatus status, Pageable pageable);
}
