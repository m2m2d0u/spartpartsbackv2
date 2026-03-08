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
public class PortalPartResponse {

    private String id;
    private String partNumber;
    private String name;
    private String shortDescription;
    private BigDecimal sellingPrice;
    private String categoryName;
    private String carBrandName;
    private String carModelName;
    private String mainImageUrl;
    private List<PortalImageResponse> images;
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
}
