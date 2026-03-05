package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Invoice;
import sn.symmetry.spareparts.enums.InvoiceStatus;
import sn.symmetry.spareparts.enums.InvoiceType;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    boolean existsByInvoiceNumber(String invoiceNumber);

    Page<Invoice> findByCustomerId(UUID customerId, Pageable pageable);

    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);

    Page<Invoice> findByInvoiceType(InvoiceType type, Pageable pageable);

    Page<Invoice> findByCustomerIdAndStatus(UUID customerId, InvoiceStatus status, Pageable pageable);

    // Dashboard

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = :status")
    long countByInvoiceStatusAll(@Param("status") InvoiceStatus status);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = :status AND i.sourceWarehouse.id IN :warehouseIds")
    long countByInvoiceStatusByWarehouses(@Param("status") InvoiceStatus status, @Param("warehouseIds") List<UUID> warehouseIds);
}
