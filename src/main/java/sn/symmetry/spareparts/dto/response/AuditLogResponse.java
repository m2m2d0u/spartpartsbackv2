package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private UUID id;
    private UUID userId;
    private String userName;
    private UUID customerId;
    private String customerName;
    private String action;
    private String entityType;
    private UUID entityId;
    private Object changes;
    private String ipAddress;
    private LocalDateTime createdAt;
}
