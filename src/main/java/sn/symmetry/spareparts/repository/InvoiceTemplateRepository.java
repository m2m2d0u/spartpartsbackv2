package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.InvoiceTemplate;

import java.util.UUID;

@Repository
public interface InvoiceTemplateRepository extends JpaRepository<InvoiceTemplate, UUID> {

    @Modifying
    @Query("UPDATE InvoiceTemplate t SET t.isDefault = false WHERE t.id <> :excludeId")
    void clearDefaultExcluding(@Param("excludeId") UUID excludeId);
}
