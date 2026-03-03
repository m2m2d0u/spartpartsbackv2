package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.InvoiceTemplate;

import java.util.UUID;

@Repository
public interface InvoiceTemplateRepository extends JpaRepository<InvoiceTemplate, UUID> {
}
