package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.response.CreditNoteResponse;
import sn.symmetry.spareparts.entity.CreditNote;

@Mapper(config = MapStructConfig.class)
public interface CreditNoteMapper {

    @Mapping(source = "returnEntity.id", target = "returnId")
    CreditNoteResponse toResponse(CreditNote creditNote);
}
