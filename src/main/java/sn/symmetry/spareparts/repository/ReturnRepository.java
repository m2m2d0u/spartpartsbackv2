package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Return;
import sn.symmetry.spareparts.enums.ReturnStatus;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {

    boolean existsByReturnNumber(String returnNumber);

    Page<Return> findByCustomerId(Long customerId, Pageable pageable);

    Page<Return> findByStatus(ReturnStatus status, Pageable pageable);
}
