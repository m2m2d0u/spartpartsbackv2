package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateTaxRateRequest;
import sn.symmetry.spareparts.dto.request.UpdateTaxRateRequest;
import sn.symmetry.spareparts.dto.response.TaxRateResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

public interface TaxRateService {

    PagedResponse<TaxRateResponse> getAllTaxRates(Pageable pageable);

    TaxRateResponse getTaxRateById(Long id);

    TaxRateResponse createTaxRate(CreateTaxRateRequest request);

    TaxRateResponse updateTaxRate(Long id, UpdateTaxRateRequest request);

    void deleteTaxRate(Long id);
}
