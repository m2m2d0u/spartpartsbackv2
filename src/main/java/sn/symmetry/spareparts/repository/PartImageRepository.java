package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.PartImage;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartImageRepository extends JpaRepository<PartImage, UUID> {

    List<PartImage> findByPartIdOrderBySortOrder(UUID partId);
}
