package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Part;

import java.util.UUID;

@Repository
public interface PartRepository extends JpaRepository<Part, UUID> {

    boolean existsByPartNumber(String partNumber);

    boolean existsByPartNumberAndIdNot(String partNumber, UUID id);

    Page<Part> findByCategoryId(UUID categoryId, Pageable pageable);

    Page<Part> findByPublished(Boolean published, Pageable pageable);

    Page<Part> findByCategoryIdAndPublished(UUID categoryId, Boolean published, Pageable pageable);

    Page<Part> findByCarBrandId(UUID carBrandId, Pageable pageable);

    Page<Part> findByCarModelId(UUID carModelId, Pageable pageable);
}
