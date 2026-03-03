package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateTaxRateRequest;
import sn.symmetry.spareparts.dto.request.UpdateTaxRateRequest;
import sn.symmetry.spareparts.dto.response.TaxRateResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

import java.util.UUID;

public interface TaxRateService {

    PagedResponse<TaxRateResponse> getAllTaxRates(Pageable pageable);

    TaxRateResponse getTaxRateById(UUID id);

    TaxRateResponse createTaxRate(CreateTaxRateRequest request);

    TaxRateResponse updateTaxRate(UUID id, UpdateTaxRateRequest request);

    void deleteTaxRate(UUID id);
}
