package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Refund;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
}
