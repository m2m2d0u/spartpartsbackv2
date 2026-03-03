package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.RestockAction;
import sn.symmetry.spareparts.enums.ReturnReason;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItemResponse {

    private UUID id;
    private UUID partId;
    private String partName;
    private String partNumber;
    private Integer quantity;
    private ReturnReason reason;
    private RestockAction restockAction;
    private UUID warehouseId;
    private String warehouseName;
}
