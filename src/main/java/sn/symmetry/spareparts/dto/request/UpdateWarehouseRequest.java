package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWarehouseRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "Code is required")
    @Size(max = 20, message = "Code must not exceed 20 characters")
    private String code;

    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @Size(max = 300, message = "Street must not exceed 300 characters")
    private String street;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Size(max = 200, message = "Contact person must not exceed 200 characters")
    private String contactPerson;

    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;

    @Size(max = 50, message = "NINEA must not exceed 50 characters")
    private String ninea;

    @Size(max = 50, message = "RCCM must not exceed 50 characters")
    private String rccm;

    @Size(max = 50, message = "Tax ID must not exceed 50 characters")
    private String taxId;

    private String notes;
}
