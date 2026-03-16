package sn.symmetry.spareparts.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.request.CreateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.request.UpdateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.response.InvoiceTemplateResponse;
import sn.symmetry.spareparts.entity.InvoiceTemplate;
import sn.symmetry.spareparts.service.FileStorageService;

@Mapper(config = MapStructConfig.class)
public abstract class InvoiceTemplateMapper {

    @Autowired
    protected FileStorageService fileStorageService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "taxRate", ignore = true)
    public abstract InvoiceTemplate toEntity(CreateInvoiceTemplateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "taxRate", ignore = true)
    public abstract void updateEntity(UpdateInvoiceTemplateRequest request, @MappingTarget InvoiceTemplate invoiceTemplate);

    @Mapping(source = "taxRate.id", target = "taxRateId")
    @Mapping(source = "taxRate.label", target = "taxRateLabel")
    public abstract InvoiceTemplateResponse toResponse(InvoiceTemplate invoiceTemplate);

    @AfterMapping
    protected void resolveUrls(@MappingTarget InvoiceTemplateResponse response) {
        response.setLogoUrl(toPublicUrl(response.getLogoUrl()));
        response.setHeaderImageUrl(toPublicUrl(response.getHeaderImageUrl()));
        response.setFooterImageUrl(toPublicUrl(response.getFooterImageUrl()));
        response.setStampImageUrl(toPublicUrl(response.getStampImageUrl()));
        response.setSignatureImageUrl(toPublicUrl(response.getSignatureImageUrl()));
        response.setWatermarkImageUrl(toPublicUrl(response.getWatermarkImageUrl()));
    }

    private String toPublicUrl(String reference) {
        if (reference == null || reference.isEmpty() || reference.startsWith("http")) {
            return reference;
        }
        return fileStorageService.getPublicUrl(reference);
    }
}
