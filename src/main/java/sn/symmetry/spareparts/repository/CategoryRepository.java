package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    Optional<Category> findByNameIgnoreCase(String name);

    @Query("SELECT c.id, c.name, c.imageUrl, COUNT(p) FROM Category c LEFT JOIN c.parts p ON p.published = true GROUP BY c.id, c.name, c.imageUrl HAVING COUNT(p) > 0 ORDER BY c.name")
    List<Object[]> findCategoriesWithPublishedPartCount();

    @Query("SELECT c.id, c.name, c.imageUrl, COUNT(DISTINCT p.id) " +
           "FROM Category c " +
           "LEFT JOIN c.parts p " +
           "WHERE p.published = true " +
           "AND p.id IN (SELECT ws.part.id FROM WarehouseStock ws WHERE ws.quantity > 0) " +
           "GROUP BY c.id, c.name, c.imageUrl " +
           "HAVING COUNT(DISTINCT p.id) > 0 " +
           "ORDER BY c.name")
    List<Object[]> findCategoriesWithInStockPartCount();
}
