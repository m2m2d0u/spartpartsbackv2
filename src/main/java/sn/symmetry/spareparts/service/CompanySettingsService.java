package sn.symmetry.spareparts.service;

import sn.symmetry.spareparts.dto.request.UpdateCompanySettingsRequest;
import sn.symmetry.spareparts.dto.response.CompanySettingsResponse;

public interface CompanySettingsService {

    CompanySettingsResponse getSettings();

    CompanySettingsResponse updateSettings(UpdateCompanySettingsRequest request);
}
