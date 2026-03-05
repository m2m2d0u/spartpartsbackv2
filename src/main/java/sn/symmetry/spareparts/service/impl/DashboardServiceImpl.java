package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.response.DashboardResponse;
import sn.symmetry.spareparts.entity.ClientOrder;
import sn.symmetry.spareparts.entity.StockMovement;
import sn.symmetry.spareparts.entity.WarehouseStock;
import sn.symmetry.spareparts.enums.InvoiceStatus;
import sn.symmetry.spareparts.enums.OrderStatus;
import sn.symmetry.spareparts.enums.RoleLevel;
import sn.symmetry.spareparts.enums.StockTransferStatus;
import sn.symmetry.spareparts.repository.ClientOrderRepository;
import sn.symmetry.spareparts.repository.InvoiceRepository;
import sn.symmetry.spareparts.repository.StockMovementRepository;
import sn.symmetry.spareparts.repository.StockTransferRepository;
import sn.symmetry.spareparts.repository.WarehouseRepository;
import sn.symmetry.spareparts.repository.WarehouseStockRepository;
import sn.symmetry.spareparts.service.AuthorizationService;
import sn.symmetry.spareparts.service.DashboardService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final AuthorizationService authorizationService;
    private final WarehouseStockRepository warehouseStockRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final InvoiceRepository invoiceRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockTransferRepository stockTransferRepository;
    private final WarehouseRepository warehouseRepository;

    private static final List<OrderStatus> REVENUE_STATUSES = List.of(
            OrderStatus.CONFIRMED, OrderStatus.PROCESSING,
            OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.COMPLETED
    );

    private static final List<OrderStatus> PENDING_STATUSES = List.of(
            OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.PROCESSING
    );

    private static final List<StockTransferStatus> PENDING_TRANSFER_STATUSES = List.of(
            StockTransferStatus.PENDING, StockTransferStatus.IN_TRANSIT
    );

    private static final PageRequest TOP_10 = PageRequest.of(0, 10);
    private static final PageRequest TOP_5 = PageRequest.of(0, 5);

    @Override
    public DashboardResponse getDashboard() {
        // null = admin (all), non-null = scoped list
        List<UUID> warehouseIds = authorizationService.getAccessibleWarehouseIds();
        boolean isAll = (warehouseIds == null);
        RoleLevel roleLevel = resolveRoleLevel();

        DashboardResponse.DashboardResponseBuilder builder = DashboardResponse.builder()
                .roleLevel(roleLevel);

        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        // KPIs shared across all roles
        builder.totalParts(isAll
                ? warehouseStockRepository.countDistinctPartsInStockAll()
                : warehouseStockRepository.countDistinctPartsInStockByWarehouses(warehouseIds));
        builder.totalStockValue(isAll
                ? warehouseStockRepository.calculateTotalStockValueAll()
                : warehouseStockRepository.calculateTotalStockValueByWarehouses(warehouseIds));
        builder.lowStockCount(isAll
                ? warehouseStockRepository.countLowStockItemsAll()
                : warehouseStockRepository.countLowStockItemsByWarehouses(warehouseIds));

        // Low stock table — all roles
        List<WarehouseStock> lowStockItems = isAll
                ? warehouseStockRepository.findLowStockItemsAll(TOP_10)
                : warehouseStockRepository.findLowStockItemsByWarehouses(warehouseIds, TOP_10);
        builder.lowStockItems(lowStockItems.stream().map(ws -> DashboardResponse.LowStockRow.builder()
                .partId(ws.getPart().getId().toString())
                .partNumber(ws.getPart().getPartNumber())
                .partName(ws.getPart().getName())
                .warehouseName(ws.getWarehouse().getName())
                .currentStock(ws.getQuantity())
                .minStockLevel(ws.getMinStockLevel())
                .build()).toList());

        switch (roleLevel) {
            case SYSTEM -> buildSystemDashboard(builder, warehouseIds, isAll, monthStart, thirtyDaysAgo);
            case STORE -> buildStoreDashboard(builder, warehouseIds, isAll, monthStart, thirtyDaysAgo);
            case WAREHOUSE -> buildWarehouseDashboard(builder, warehouseIds, isAll, thirtyDaysAgo, todayStart);
        }

        return builder.build();
    }

    private void buildSystemDashboard(DashboardResponse.DashboardResponseBuilder builder,
                                       List<UUID> warehouseIds, boolean isAll,
                                       LocalDateTime monthStart, LocalDateTime thirtyDaysAgo) {
        builder.pendingOrdersCount(isAll
                ? clientOrderRepository.countByStatusInAll(PENDING_STATUSES)
                : clientOrderRepository.countByStatusInByWarehouses(PENDING_STATUSES, warehouseIds));
        builder.monthlyRevenue(isAll
                ? clientOrderRepository.sumRevenueSinceAll(REVENUE_STATUSES, monthStart)
                : clientOrderRepository.sumRevenueSinceByWarehouses(REVENUE_STATUSES, monthStart, warehouseIds));
        builder.overdueInvoicesCount(isAll
                ? invoiceRepository.countByInvoiceStatusAll(InvoiceStatus.OVERDUE)
                : invoiceRepository.countByInvoiceStatusByWarehouses(InvoiceStatus.OVERDUE, warehouseIds));
        builder.unpaidInvoicesCount(isAll
                ? invoiceRepository.countByInvoiceStatusAll(InvoiceStatus.PARTIALLY_PAID)
                : invoiceRepository.countByInvoiceStatusByWarehouses(InvoiceStatus.PARTIALLY_PAID, warehouseIds));
        builder.warehouseCount(warehouseRepository.count());

        builder.revenueChart(buildRevenueChart(thirtyDaysAgo, warehouseIds, isAll));
        builder.ordersByStatus(buildOrdersByStatus(warehouseIds, isAll));
        builder.topSellingParts(buildTopSellingParts(thirtyDaysAgo, warehouseIds, isAll));
        builder.recentOrders(buildRecentOrders(warehouseIds, isAll));
    }

    private void buildStoreDashboard(DashboardResponse.DashboardResponseBuilder builder,
                                      List<UUID> warehouseIds, boolean isAll,
                                      LocalDateTime monthStart, LocalDateTime thirtyDaysAgo) {
        builder.pendingOrdersCount(isAll
                ? clientOrderRepository.countByStatusInAll(PENDING_STATUSES)
                : clientOrderRepository.countByStatusInByWarehouses(PENDING_STATUSES, warehouseIds));
        builder.monthlyRevenue(isAll
                ? clientOrderRepository.sumRevenueSinceAll(REVENUE_STATUSES, monthStart)
                : clientOrderRepository.sumRevenueSinceByWarehouses(REVENUE_STATUSES, monthStart, warehouseIds));
        builder.overdueInvoicesCount(isAll
                ? invoiceRepository.countByInvoiceStatusAll(InvoiceStatus.OVERDUE)
                : invoiceRepository.countByInvoiceStatusByWarehouses(InvoiceStatus.OVERDUE, warehouseIds));
        builder.warehouseCount(warehouseIds != null ? (long) warehouseIds.size() : warehouseRepository.count());

        builder.revenueChart(buildRevenueChart(thirtyDaysAgo, warehouseIds, isAll));
        builder.stockByWarehouse(buildStockByWarehouse(warehouseIds, isAll));
        builder.ordersByStatus(buildOrdersByStatus(warehouseIds, isAll));
        builder.topSellingParts(buildTopSellingParts(thirtyDaysAgo, warehouseIds, isAll));
        builder.recentOrders(buildRecentOrders(warehouseIds, isAll));
    }

    private void buildWarehouseDashboard(DashboardResponse.DashboardResponseBuilder builder,
                                          List<UUID> warehouseIds, boolean isAll,
                                          LocalDateTime thirtyDaysAgo, LocalDateTime todayStart) {
        builder.todayMovementsCount(isAll
                ? stockMovementRepository.countMovementsSinceAll(todayStart)
                : stockMovementRepository.countMovementsSinceByWarehouses(todayStart, warehouseIds));
        builder.pendingTransfersCount(isAll
                ? stockTransferRepository.countByStatusInAll(PENDING_TRANSFER_STATUSES)
                : stockTransferRepository.countByStatusInByWarehouses(PENDING_TRANSFER_STATUSES, warehouseIds));

        builder.movementsChart(buildMovementsChart(thirtyDaysAgo, warehouseIds, isAll));

        List<StockMovement> movements = isAll
                ? stockMovementRepository.findRecentMovementsAll(TOP_10)
                : stockMovementRepository.findRecentMovementsByWarehouses(warehouseIds, TOP_10);
        builder.recentMovements(movements.stream().map(sm -> DashboardResponse.RecentMovementRow.builder()
                .movementId(sm.getId().toString())
                .partName(sm.getPart().getName())
                .warehouseName(sm.getWarehouse().getName())
                .type(sm.getType().name())
                .quantityChange(sm.getQuantityChange())
                .createdAt(sm.getCreatedAt().toString())
                .build()).toList());
    }

    // ── Chart builders ────────────────────────────────────────

    private List<DashboardResponse.TimeSeriesPoint> buildRevenueChart(LocalDateTime since, List<UUID> warehouseIds, boolean isAll) {
        List<Object[]> rows = isAll
                ? clientOrderRepository.dailyRevenueSinceAll(REVENUE_STATUSES, since)
                : clientOrderRepository.dailyRevenueSinceByWarehouses(REVENUE_STATUSES, since, warehouseIds);
        return rows.stream().map(r -> DashboardResponse.TimeSeriesPoint.builder()
                .date((LocalDate) r[0])
                .value((BigDecimal) r[1])
                .build()).toList();
    }

    private List<DashboardResponse.TimeSeriesPoint> buildMovementsChart(LocalDateTime since, List<UUID> warehouseIds, boolean isAll) {
        List<Object[]> rows = isAll
                ? stockMovementRepository.dailyMovementsSinceAll(since)
                : stockMovementRepository.dailyMovementsSinceByWarehouses(since, warehouseIds);
        List<DashboardResponse.TimeSeriesPoint> result = new ArrayList<>();
        for (Object[] r : rows) {
            LocalDate date = (LocalDate) r[0];
            Long stockIn = (Long) r[1];
            Long stockOut = (Long) r[2];
            result.add(DashboardResponse.TimeSeriesPoint.builder()
                    .date(date)
                    .value(BigDecimal.valueOf(stockIn - stockOut))
                    .build());
        }
        return result;
    }

    private Map<String, Long> buildOrdersByStatus(List<UUID> warehouseIds, boolean isAll) {
        List<Object[]> rows = isAll
                ? clientOrderRepository.countGroupedByStatusAll()
                : clientOrderRepository.countGroupedByStatusByWarehouses(warehouseIds);
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            map.put(((OrderStatus) r[0]).name(), (Long) r[1]);
        }
        return map;
    }

    private List<DashboardResponse.NamedValue> buildStockByWarehouse(List<UUID> warehouseIds, boolean isAll) {
        List<Object[]> rows = isAll
                ? warehouseStockRepository.getStockValueByWarehouseAll()
                : warehouseStockRepository.getStockValueByWarehouseFiltered(warehouseIds);
        return rows.stream().map(r -> DashboardResponse.NamedValue.builder()
                .name((String) r[0])
                .value((BigDecimal) r[1])
                .build()).toList();
    }

    private List<DashboardResponse.TopPartRow> buildTopSellingParts(LocalDateTime since, List<UUID> warehouseIds, boolean isAll) {
        List<Object[]> rows = isAll
                ? clientOrderRepository.findTopSellingPartsAll(REVENUE_STATUSES, since, TOP_10)
                : clientOrderRepository.findTopSellingPartsByWarehouses(REVENUE_STATUSES, since, warehouseIds, TOP_10);
        return rows.stream().map(r -> DashboardResponse.TopPartRow.builder()
                .partId(r[0].toString())
                .partNumber((String) r[1])
                .partName((String) r[2])
                .quantitySold((Long) r[3])
                .revenue((BigDecimal) r[4])
                .build()).toList();
    }

    private List<DashboardResponse.RecentOrderRow> buildRecentOrders(List<UUID> warehouseIds, boolean isAll) {
        List<ClientOrder> orders = isAll
                ? clientOrderRepository.findRecentOrdersAll(TOP_5)
                : clientOrderRepository.findRecentOrdersByWarehouses(warehouseIds, TOP_5);
        return orders.stream().map(o -> DashboardResponse.RecentOrderRow.builder()
                .orderId(o.getId().toString())
                .orderNumber(o.getOrderNumber())
                .customerName(o.getCustomer().getName())
                .status(o.getStatus().name())
                .totalAmount(o.getTotalAmount())
                .orderDate(o.getOrderDate().toLocalDate().toString())
                .build()).toList();
    }

    private RoleLevel resolveRoleLevel() {
        if (authorizationService.isAdmin()) return RoleLevel.SYSTEM;
        if (authorizationService.isStoreManager()) return RoleLevel.STORE;
        return RoleLevel.WAREHOUSE;
    }
}
