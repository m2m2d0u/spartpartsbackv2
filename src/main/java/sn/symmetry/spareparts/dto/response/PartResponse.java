package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartResponse {

    private Long id;
    private String partNumber;
    private String name;
    private String description;
    private String shortDescription;
    private Long categoryId;
    private String categoryName;
    private BigDecimal sellingPrice;
    private BigDecimal purchasePrice;
    private Integer minStockLevel;
    private Boolean published;
    private String notes;
    private List<PartImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
