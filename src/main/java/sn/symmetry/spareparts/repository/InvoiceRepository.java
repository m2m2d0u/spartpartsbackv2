package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Invoice;
import sn.symmetry.spareparts.enums.InvoiceStatus;
import sn.symmetry.spareparts.enums.InvoiceType;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsByInvoiceNumber(String invoiceNumber);

    Page<Invoice> findByCustomerId(Long customerId, Pageable pageable);

    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);

    Page<Invoice> findByInvoiceType(InvoiceType type, Pageable pageable);

    Page<Invoice> findByCustomerIdAndStatus(Long customerId, InvoiceStatus status, Pageable pageable);
}
