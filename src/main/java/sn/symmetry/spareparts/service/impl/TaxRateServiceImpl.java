package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateTaxRateRequest;
import sn.symmetry.spareparts.dto.request.UpdateTaxRateRequest;
import sn.symmetry.spareparts.dto.response.TaxRateResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.entity.TaxRate;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.TaxRateMapper;
import sn.symmetry.spareparts.repository.TaxRateRepository;
import sn.symmetry.spareparts.service.TaxRateService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxRateServiceImpl implements TaxRateService {

    private final TaxRateRepository taxRateRepository;
    private final TaxRateMapper taxRateMapper;

    @Override
    public PagedResponse<TaxRateResponse> getAllTaxRates(Pageable pageable) {
        Page<TaxRate> page = taxRateRepository.findAll(pageable);
        return PagedResponse.of(page.map(taxRateMapper::toResponse));
    }

    @Override
    public TaxRateResponse getTaxRateById(Long id) {
        TaxRate taxRate = taxRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaxRate", "id", id));
        return taxRateMapper.toResponse(taxRate);
    }

    @Override
    @Transactional
    public TaxRateResponse createTaxRate(CreateTaxRateRequest request) {
        TaxRate taxRate = taxRateMapper.toEntity(request);
        TaxRate saved = taxRateRepository.save(taxRate);
        return taxRateMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TaxRateResponse updateTaxRate(Long id, UpdateTaxRateRequest request) {
        TaxRate taxRate = taxRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaxRate", "id", id));

        taxRateMapper.updateEntity(request, taxRate);
        TaxRate saved = taxRateRepository.save(taxRate);
        return taxRateMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTaxRate(Long id) {
        TaxRate taxRate = taxRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaxRate", "id", id));
        taxRateRepository.delete(taxRate);
    }
}
