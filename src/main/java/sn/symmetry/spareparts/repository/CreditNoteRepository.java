package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.CreditNote;

@Repository
public interface CreditNoteRepository extends JpaRepository<CreditNote, Long> {
}
