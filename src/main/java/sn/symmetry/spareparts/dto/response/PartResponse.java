package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartResponse {

    private UUID id;
    private String partNumber;
    private String name;
    private String description;
    private String shortDescription;
    private UUID categoryId;
    private String categoryName;
    private UUID carBrandId;
    private String carBrandName;
    private UUID carModelId;
    private String carModelName;
    private BigDecimal sellingPrice;
    private BigDecimal purchasePrice;
    private Integer minStockLevel;
    private Boolean published;
    private String notes;
    private List<PartImageResponse> images;
    private List<TagResponse> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
