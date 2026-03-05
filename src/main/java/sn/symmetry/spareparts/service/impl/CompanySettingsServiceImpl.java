package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static sn.symmetry.spareparts.config.CacheConfig.COMPANY_SETTINGS_CACHE;
import sn.symmetry.spareparts.dto.request.UpdateCompanySettingsRequest;
import sn.symmetry.spareparts.dto.response.CompanySettingsResponse;
import sn.symmetry.spareparts.entity.CompanySettings;
import sn.symmetry.spareparts.entity.InvoiceTemplate;
import sn.symmetry.spareparts.entity.Warehouse;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.CompanySettingsMapper;
import sn.symmetry.spareparts.repository.CompanySettingsRepository;
import sn.symmetry.spareparts.repository.InvoiceTemplateRepository;
import sn.symmetry.spareparts.repository.WarehouseRepository;
import sn.symmetry.spareparts.service.CompanySettingsService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanySettingsServiceImpl implements CompanySettingsService {

    private final CompanySettingsRepository companySettingsRepository;
    private final WarehouseRepository warehouseRepository;
    private final InvoiceTemplateRepository invoiceTemplateRepository;
    private final CompanySettingsMapper companySettingsMapper;

    @Override
    @Cacheable(value = COMPANY_SETTINGS_CACHE, key = "'singleton'")
    public CompanySettingsResponse getSettings() {
        CompanySettings settings = companySettingsRepository.findFirstBy()
                .orElseThrow(() -> new ResourceNotFoundException("CompanySettings", "id", "default"));
        return companySettingsMapper.toResponse(settings);
    }

    @Override
    @Transactional
    @CacheEvict(value = COMPANY_SETTINGS_CACHE, allEntries = true)
    public CompanySettingsResponse updateSettings(UpdateCompanySettingsRequest request) {
        CompanySettings settings = companySettingsRepository.findFirstBy()
                .orElseThrow(() -> new ResourceNotFoundException("CompanySettings", "id", "default"));

        companySettingsMapper.updateEntity(request, settings);

        if (request.getDefaultTemplateId() != null) {
            InvoiceTemplate template = invoiceTemplateRepository.findById(request.getDefaultTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("InvoiceTemplate", "id", request.getDefaultTemplateId()));
            settings.setDefaultTemplate(template);
        } else {
            settings.setDefaultTemplate(null);
        }

        if (request.getDefaultWarehouseId() != null) {
            Warehouse defaultWarehouse = warehouseRepository.findById(request.getDefaultWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getDefaultWarehouseId()));
            settings.setDefaultWarehouse(defaultWarehouse);
        } else {
            settings.setDefaultWarehouse(null);
        }

        if (request.getPortalWarehouseId() != null) {
            Warehouse portalWarehouse = warehouseRepository.findById(request.getPortalWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getPortalWarehouseId()));
            settings.setPortalWarehouse(portalWarehouse);
        } else {
            settings.setPortalWarehouse(null);
        }

        CompanySettings saved = companySettingsRepository.save(settings);
        return companySettingsMapper.toResponse(saved);
    }
}
