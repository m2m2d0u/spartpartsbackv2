package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.InvoiceStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvoiceStatusRequest {

    @NotNull(message = "Status is required")
    private InvoiceStatus status;
}
