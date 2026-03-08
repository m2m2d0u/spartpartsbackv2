package sn.symmetry.spareparts.dto.response.portal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalStoreConfigResponse {

    private String storeName;
    private String logoUrl;
    private String currencySymbol;
    private String currencyPosition;
    private int currencyDecimals;
    private String thousandsSeparator;
}
