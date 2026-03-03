package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.response.AuditLogResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

import java.util.UUID;

public interface AuditLogService {

    PagedResponse<AuditLogResponse> getAllAuditLogs(String entityType, UUID entityId, UUID userId, String action, Pageable pageable);

    AuditLogResponse getAuditLogById(UUID id);
}
