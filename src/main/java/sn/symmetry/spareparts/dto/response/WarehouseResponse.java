package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponse {

    private Long id;
    private String name;
    private String code;
    private String location;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String contactPerson;
    private String phone;
    private String ninea;
    private String rccm;
    private String taxId;
    private String notes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
