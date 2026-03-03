package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.response.RefundResponse;
import sn.symmetry.spareparts.entity.Refund;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface RefundMapper {

    @Mapping(source = "returnEntity.id", target = "returnId")
    @Mapping(source = "invoice.id", target = "invoiceId")
    RefundResponse toResponse(Refund refund);

    List<RefundResponse> toResponseList(List<Refund> refunds);
}
