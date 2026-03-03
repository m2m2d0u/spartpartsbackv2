package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.StockTransferStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferResponse {

    private Long id;
    private String transferNumber;
    private Long sourceWarehouseId;
    private String sourceWarehouseName;
    private Long destinationWarehouseId;
    private String destinationWarehouseName;
    private StockTransferStatus status;
    private LocalDate transferDate;
    private String notes;
    private List<StockTransferItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
