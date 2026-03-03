package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.response.AuditLogResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.AuditLog;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.AuditLogMapper;
import sn.symmetry.spareparts.repository.AuditLogRepository;
import sn.symmetry.spareparts.service.AuditLogService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public PagedResponse<AuditLogResponse> getAllAuditLogs(String entityType, Long entityId, Long userId, String action, Pageable pageable) {
        Page<AuditLog> page;
        if (entityType != null && entityId != null) {
            page = auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable);
        } else if (userId != null) {
            page = auditLogRepository.findByUserId(userId, pageable);
        } else if (entityType != null) {
            page = auditLogRepository.findByEntityType(entityType, pageable);
        } else if (action != null) {
            page = auditLogRepository.findByAction(action, pageable);
        } else {
            page = auditLogRepository.findAll(pageable);
        }
        return PagedResponse.of(page.map(auditLogMapper::toResponse));
    }

    @Override
    public AuditLogResponse getAuditLogById(Long id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog", "id", id));
        return auditLogMapper.toResponse(auditLog);
    }
}
