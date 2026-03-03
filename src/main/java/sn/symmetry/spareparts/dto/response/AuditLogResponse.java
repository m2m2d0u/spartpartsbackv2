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
public class AuditLogResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Long customerId;
    private String customerName;
    private String action;
    private String entityType;
    private Long entityId;
    private Object changes;
    private String ipAddress;
    private LocalDateTime createdAt;
}
