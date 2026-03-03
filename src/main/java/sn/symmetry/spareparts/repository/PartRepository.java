package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Part;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {

    boolean existsByPartNumber(String partNumber);

    boolean existsByPartNumberAndIdNot(String partNumber, Long id);

    Page<Part> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Part> findByPublished(Boolean published, Pageable pageable);

    Page<Part> findByCategoryIdAndPublished(Long categoryId, Boolean published, Pageable pageable);
}
