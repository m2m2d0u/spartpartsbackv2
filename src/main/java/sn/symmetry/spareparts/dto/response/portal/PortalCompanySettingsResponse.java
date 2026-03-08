package sn.symmetry.spareparts.dto.response.portal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalCompanySettingsResponse {

    private String companyName;
    private String logoUrl;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String email;
    private String currencySymbol;
    private String currencyPosition;
    private Integer currencyDecimals;
    private String thousandsSeparator;
    private BigDecimal defaultTaxRate;
    private Boolean portalEnabled;
    private BigDecimal portalMinOrderAmount;
    private BigDecimal portalShippingFlatRate;
    private BigDecimal portalFreeShippingAbove;
    private String portalTermsText;
}
