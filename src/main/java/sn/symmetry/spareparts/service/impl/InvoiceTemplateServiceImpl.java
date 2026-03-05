package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static sn.symmetry.spareparts.config.CacheConfig.INVOICE_TEMPLATES_CACHE;
import sn.symmetry.spareparts.dto.request.CreateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.request.UpdateInvoiceTemplateRequest;
import sn.symmetry.spareparts.dto.response.InvoiceTemplateResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.InvoiceTemplate;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.InvoiceTemplateMapper;
import sn.symmetry.spareparts.repository.InvoiceTemplateRepository;
import sn.symmetry.spareparts.service.InvoiceTemplateService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceTemplateServiceImpl implements InvoiceTemplateService {

    private final InvoiceTemplateRepository invoiceTemplateRepository;
    private final InvoiceTemplateMapper invoiceTemplateMapper;

    @Override
    public PagedResponse<InvoiceTemplateResponse> getAllInvoiceTemplates(Pageable pageable) {
        Page<InvoiceTemplate> page = invoiceTemplateRepository.findAll(pageable);
        return PagedResponse.of(page.map(invoiceTemplateMapper::toResponse));
    }

    @Override
    @Cacheable(value = INVOICE_TEMPLATES_CACHE, key = "#id")
    public InvoiceTemplateResponse getInvoiceTemplateById(UUID id) {
        InvoiceTemplate invoiceTemplate = invoiceTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InvoiceTemplate", "id", id));
        return invoiceTemplateMapper.toResponse(invoiceTemplate);
    }

    @Override
    @Transactional
    @CacheEvict(value = INVOICE_TEMPLATES_CACHE, allEntries = true)
    public InvoiceTemplateResponse createInvoiceTemplate(CreateInvoiceTemplateRequest request) {
        InvoiceTemplate invoiceTemplate = invoiceTemplateMapper.toEntity(request);
        InvoiceTemplate saved = invoiceTemplateRepository.save(invoiceTemplate);
        return invoiceTemplateMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = INVOICE_TEMPLATES_CACHE, allEntries = true)
    public InvoiceTemplateResponse updateInvoiceTemplate(UUID id, UpdateInvoiceTemplateRequest request) {
        InvoiceTemplate invoiceTemplate = invoiceTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InvoiceTemplate", "id", id));

        invoiceTemplateMapper.updateEntity(request, invoiceTemplate);
        InvoiceTemplate saved = invoiceTemplateRepository.save(invoiceTemplate);
        return invoiceTemplateMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = INVOICE_TEMPLATES_CACHE, allEntries = true)
    public void deleteInvoiceTemplate(UUID id) {
        InvoiceTemplate invoiceTemplate = invoiceTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InvoiceTemplate", "id", id));
        invoiceTemplateRepository.delete(invoiceTemplate);
    }
}
