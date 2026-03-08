package sn.symmetry.spareparts.dto.response.portal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalOrderConfirmationResponse {

    private String orderNumber;
    private String status;
    private BigDecimal totalAmount;
    private List<PortalOrderItemResponse> items;
    private String customerName;
    private String customerEmail;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortalOrderItemResponse {
        private String partName;
        private String partNumber;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}
