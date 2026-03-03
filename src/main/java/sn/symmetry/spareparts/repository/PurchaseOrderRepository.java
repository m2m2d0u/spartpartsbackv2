package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.PurchaseOrder;
import sn.symmetry.spareparts.enums.PurchaseOrderStatus;

import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    boolean existsByPoNumber(String poNumber);

    Page<PurchaseOrder> findBySupplierId(UUID supplierId, Pageable pageable);

    Page<PurchaseOrder> findByStatus(PurchaseOrderStatus status, Pageable pageable);

    Page<PurchaseOrder> findBySupplierIdAndStatus(UUID supplierId, PurchaseOrderStatus status, Pageable pageable);
}
