package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Return;
import sn.symmetry.spareparts.enums.ReturnStatus;

import java.util.UUID;

@Repository
public interface ReturnRepository extends JpaRepository<Return, UUID> {

    boolean existsByReturnNumber(String returnNumber);

    Page<Return> findByCustomerId(UUID customerId, Pageable pageable);

    Page<Return> findByStatus(ReturnStatus status, Pageable pageable);
}
