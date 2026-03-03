package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Return;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {
}
