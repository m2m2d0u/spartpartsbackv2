package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemResponse {

    private Long id;
    private Long partId;
    private String partName;
    private String partNumber;
    private Integer quantity;
    private BigDecimal unitPrice;
    private Integer receivedQuantity;
}
