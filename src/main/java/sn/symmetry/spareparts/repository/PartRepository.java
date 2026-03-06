package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT p FROM Part p WHERE " +
           "(COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           " OR LOWER(p.partNumber) LIKE LOWER(CONCAT('%', :name, '%')) " +
           " OR LOWER(COALESCE(CAST(p.reference AS string), '')) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:published IS NULL OR p.published = :published) " +
           "AND (:carBrandId IS NULL OR p.carBrand.id = :carBrandId) " +
           "AND (:carModelId IS NULL OR p.carModel.id = :carModelId)")
    Page<Part> searchParts(@Param("name") String name,
                           @Param("categoryId") UUID categoryId,
                           @Param("published") Boolean published,
                           @Param("carBrandId") UUID carBrandId,
                           @Param("carModelId") UUID carModelId,
                           Pageable pageable);

    @Query("SELECT p FROM Part p WHERE p.id NOT IN " +
           "(SELECT ws.part.id FROM WarehouseStock ws WHERE ws.warehouse.id = :warehouseId) " +
           "AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Part> findPartsNotInWarehouse(@Param("warehouseId") UUID warehouseId,
                                       @Param("name") String name,
                                       Pageable pageable);

    @Query("SELECT p FROM Part p WHERE p.id IN " +
           "(SELECT ws.part.id FROM WarehouseStock ws WHERE ws.warehouse.id = :warehouseId) " +
           "AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(p.partNumber) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Part> findPartsInWarehouse(@Param("warehouseId") UUID warehouseId,
                                    @Param("name") String name,
                                    Pageable pageable);
}
