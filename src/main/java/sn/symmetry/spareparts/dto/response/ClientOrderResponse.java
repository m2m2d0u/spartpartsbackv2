package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientOrderResponse {

    private UUID id;
    private String orderNumber;
    private UUID customerId;
    private String customerName;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal shippingAmount;
    private BigDecimal totalAmount;
    private String shippingStreet;
    private String shippingCity;
    private String shippingState;
    private String shippingPostal;
    private String shippingCountry;
    private String notes;
    private String trackingNumber;
    private UUID warehouseId;
    private String warehouseName;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
