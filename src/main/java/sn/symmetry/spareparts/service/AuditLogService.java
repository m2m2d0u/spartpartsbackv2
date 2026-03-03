package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.response.AuditLogResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

public interface AuditLogService {

    PagedResponse<AuditLogResponse> getAllAuditLogs(String entityType, Long entityId, Long userId, String action, Pageable pageable);

    AuditLogResponse getAuditLogById(Long id);
}
