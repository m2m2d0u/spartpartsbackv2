package sn.symmetry.spareparts.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sn.symmetry.spareparts.enums.RoleLevel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponse {

    private RoleLevel roleLevel;

    // KPI cards
    private Long totalParts;
    private BigDecimal totalStockValue;
    private Long pendingOrdersCount;
    private BigDecimal monthlyRevenue;
    private Long lowStockCount;
    private Long warehouseCount;
    private Long storeCount;
    private Long todayMovementsCount;
    private Long pendingTransfersCount;
    private Long overdueInvoicesCount;
    private Long unpaidInvoicesCount;

    // Charts
    private List<TimeSeriesPoint> revenueChart;
    private List<TimeSeriesPoint> movementsChart;
    private Map<String, Long> ordersByStatus;
    private List<NamedValue> stockByWarehouse;

    // Tables
    private List<LowStockRow> lowStockItems;
    private List<TopPartRow> topSellingParts;
    private List<RecentOrderRow> recentOrders;
    private List<RecentMovementRow> recentMovements;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSeriesPoint {
        private LocalDate date;
        private BigDecimal value;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NamedValue {
        private String name;
        private BigDecimal value;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowStockRow {
        private String partId;
        private String partNumber;
        private String partName;
        private String warehouseName;
        private Integer currentStock;
        private Integer minStockLevel;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopPartRow {
        private String partId;
        private String partNumber;
        private String partName;
        private Long quantitySold;
        private BigDecimal revenue;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentOrderRow {
        private String orderId;
        private String orderNumber;
        private String customerName;
        private String status;
        private BigDecimal totalAmount;
        private String orderDate;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentMovementRow {
        private String movementId;
        private String partName;
        private String warehouseName;
        private String type;
        private Integer quantityChange;
        private String createdAt;
    }
}
