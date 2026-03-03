package sn.symmetry.spareparts.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientOrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private Long warehouseId;

    @Size(max = 300, message = "Shipping street must not exceed 300 characters")
    private String shippingStreet;

    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    private String shippingCity;

    @Size(max = 100, message = "Shipping state must not exceed 100 characters")
    private String shippingState;

    @Size(max = 20, message = "Shipping postal must not exceed 20 characters")
    private String shippingPostal;

    @Size(max = 100, message = "Shipping country must not exceed 100 characters")
    private String shippingCountry;

    private String notes;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemRequest> items;
}
