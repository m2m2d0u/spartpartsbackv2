package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.ClientOrder;
import sn.symmetry.spareparts.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientOrderRepository extends JpaRepository<ClientOrder, UUID> {

    boolean existsByOrderNumber(String orderNumber);

    Optional<ClientOrder> findByOrderNumber(String orderNumber);

    Page<ClientOrder> findByCustomerId(UUID customerId, Pageable pageable);

    Page<ClientOrder> findByStatus(OrderStatus status, Pageable pageable);

    Page<ClientOrder> findByCustomerIdAndStatus(UUID customerId, OrderStatus status, Pageable pageable);

    // Dashboard — unfiltered (admin)

    @Query("SELECT COUNT(o) FROM ClientOrder o WHERE o.status IN :statuses")
    long countByStatusInAll(@Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM ClientOrder o WHERE o.status IN :statuses AND o.orderDate >= :since")
    BigDecimal sumRevenueSinceAll(@Param("statuses") List<OrderStatus> statuses, @Param("since") LocalDateTime since);

    @Query("SELECT o.status, COUNT(o) FROM ClientOrder o GROUP BY o.status")
    List<Object[]> countGroupedByStatusAll();

    @Query("SELECT CAST(o.orderDate AS LocalDate), COALESCE(SUM(o.totalAmount), 0) FROM ClientOrder o WHERE o.status IN :statuses AND o.orderDate >= :since GROUP BY CAST(o.orderDate AS LocalDate) ORDER BY CAST(o.orderDate AS LocalDate)")
    List<Object[]> dailyRevenueSinceAll(@Param("statuses") List<OrderStatus> statuses, @Param("since") LocalDateTime since);

    @Query("SELECT o FROM ClientOrder o JOIN FETCH o.customer ORDER BY o.createdAt DESC")
    List<ClientOrder> findRecentOrdersAll(Pageable pageable);

    @Query("SELECT oi.part.id, oi.part.partNumber, oi.part.name, SUM(oi.quantity), SUM(oi.quantity * oi.unitPrice) FROM ClientOrder o JOIN o.items oi WHERE o.status IN :statuses AND o.orderDate >= :since GROUP BY oi.part.id, oi.part.partNumber, oi.part.name ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingPartsAll(@Param("statuses") List<OrderStatus> statuses, @Param("since") LocalDateTime since, Pageable pageable);

    // Dashboard — filtered by warehouse IDs

    @Query("SELECT COUNT(o) FROM ClientOrder o WHERE o.status IN :statuses AND o.warehouse.id IN :warehouseIds")
    long countByStatusInByWarehouses(@Param("statuses") List<OrderStatus> statuses, @Param("warehouseIds") List<UUID> warehouseIds);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM ClientOrder o WHERE o.status IN :statuses AND o.orderDate >= :since AND o.warehouse.id IN :warehouseIds")
    BigDecimal sumRevenueSinceByWarehouses(@Param("statuses") List<OrderStatus> statuses, @Param("since") LocalDateTime since, @Param("warehouseIds") List<UUID> warehouseIds);

    @Query("SELECT o.status, COUNT(o) FROM ClientOrder o WHERE o.warehouse.id IN :warehouseIds GROUP BY o.status")
    List<Object[]> countGroupedByStatusByWarehouses(@Param("warehouseIds") List<UUID> warehouseIds);

    @Query("SELECT CAST(o.orderDate AS LocalDate), COALESCE(SUM(o.totalAmount), 0) FROM ClientOrder o WHERE o.status IN :statuses AND o.orderDate >= :since AND o.warehouse.id IN :warehouseIds GROUP BY CAST(o.orderDate AS LocalDate) ORDER BY CAST(o.orderDate AS LocalDate)")
    List<Object[]> dailyRevenueSinceByWarehouses(@Param("statuses") List<OrderStatus> statuses, @Param("since") LocalDateTime since, @Param("warehouseIds") List<UUID> warehouseIds);

    @Query("SELECT o FROM ClientOrder o JOIN FETCH o.customer WHERE o.warehouse.id IN :warehouseIds ORDER BY o.createdAt DESC")
    List<ClientOrder> findRecentOrdersByWarehouses(@Param("warehouseIds") List<UUID> warehouseIds, Pageable pageable);

    @Query("SELECT oi.part.id, oi.part.partNumber, oi.part.name, SUM(oi.quantity), SUM(oi.quantity * oi.unitPrice) FROM ClientOrder o JOIN o.items oi WHERE o.status IN :statuses AND o.orderDate >= :since AND o.warehouse.id IN :warehouseIds GROUP BY oi.part.id, oi.part.partNumber, oi.part.name ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingPartsByWarehouses(@Param("statuses") List<OrderStatus> statuses, @Param("since") LocalDateTime since, @Param("warehouseIds") List<UUID> warehouseIds, Pageable pageable);
}
