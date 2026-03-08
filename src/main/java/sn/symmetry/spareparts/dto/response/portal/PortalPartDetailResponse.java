package sn.symmetry.spareparts.dto.response.portal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalPartDetailResponse {

    private String id;
    private String partNumber;
    private String name;
    private String shortDescription;
    private String description;
    private BigDecimal sellingPrice;
    private String categoryName;
    private String carBrandName;
    private String carModelName;
    private List<PortalImageResponse> images;
    private List<PortalTagResponse> tags;
    private int availableStock;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortalImageResponse {
        private String id;
        private String url;
        private Integer sortOrder;
        private Boolean isMain;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortalTagResponse {
        private String id;
        private String name;
    }
}
