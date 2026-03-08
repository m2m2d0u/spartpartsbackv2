package sn.symmetry.spareparts.dto.request.portal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortalCreateOrderRequest {

    @Valid
    @NotNull
    private PortalCustomerInfo customer;

    @Valid
    @NotEmpty
    private List<PortalOrderItemRequest> items;

    private String notes;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortalCustomerInfo {

        @NotBlank
        private String name;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String phone;

        private String street;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortalOrderItemRequest {

        @NotNull
        private UUID partId;

        @NotNull
        @Min(1)
        private Integer quantity;
    }
}
