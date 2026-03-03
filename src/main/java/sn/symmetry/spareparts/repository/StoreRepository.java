package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Store;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, UUID id);
    Page<Store> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Find stores by list of IDs (for filtering by accessible stores).
     *
     * @param ids      list of store IDs
     * @param pageable pagination info
     * @return page of stores
     */
    Page<Store> findByIdIn(List<UUID> ids, Pageable pageable);
}
