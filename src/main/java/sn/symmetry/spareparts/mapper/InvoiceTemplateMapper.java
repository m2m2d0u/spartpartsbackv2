package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.request.CreateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.request.UpdateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.response.InvoiceTemplateResponse;
import sn.symmetry.spareparts.entity.InvoiceTemplate;

@Mapper(config = MapStructConfig.class)
public interface InvoiceTemplateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "taxRate", ignore = true)
    InvoiceTemplate toEntity(CreateInvoiceTemplateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "taxRate", ignore = true)
    void updateEntity(UpdateInvoiceTemplateRequest request, @MappingTarget InvoiceTemplate invoiceTemplate);

    @Mapping(source = "taxRate.id", target = "taxRateId")
    @Mapping(source = "taxRate.label", target = "taxRateLabel")
    InvoiceTemplateResponse toResponse(InvoiceTemplate invoiceTemplate);
}
