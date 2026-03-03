package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.ReturnStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnResponse {

    private Long id;
    private String returnNumber;
    private Long invoiceId;
    private Long orderId;
    private Long customerId;
    private String customerName;
    private ReturnStatus status;
    private LocalDate returnDate;
    private String notes;
    private List<ReturnItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
