package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.response.AuditLogResponse;
import sn.symmetry.spareparts.entity.AuditLog;

@Mapper(config = MapStructConfig.class)
public interface AuditLogMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.name", target = "customerName")
    AuditLogResponse toResponse(AuditLog auditLog);
}
