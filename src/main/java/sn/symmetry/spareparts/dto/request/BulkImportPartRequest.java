package sn.symmetry.spareparts.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportPartRequest {

    private Integer rowNumber;
    private String partNumber;
    private String name;
    private String description;
    private String shortDescription;
    private String categoryName;
    private String carBrandName;
    private String carModelName;
    private BigDecimal sellingPrice;
    private BigDecimal purchasePrice;
    private Integer minStockLevel;
    private Boolean published;
    private String reference;
    private String notes;
    private String tags; // Comma-separated tag names
}
