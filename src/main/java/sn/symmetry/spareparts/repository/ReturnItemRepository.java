package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.ReturnItem;

@Repository
public interface ReturnItemRepository extends JpaRepository<ReturnItem, Long> {
}
