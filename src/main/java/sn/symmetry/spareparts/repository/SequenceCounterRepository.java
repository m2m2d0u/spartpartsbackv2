package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.SequenceCounter;

import java.util.UUID;

@Repository
public interface SequenceCounterRepository extends JpaRepository<SequenceCounter, UUID> {
}
