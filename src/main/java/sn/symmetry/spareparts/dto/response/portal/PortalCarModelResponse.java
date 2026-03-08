package sn.symmetry.spareparts.dto.response.portal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalCarModelResponse {

    private String id;
    private String name;
    private String brandId;
}
