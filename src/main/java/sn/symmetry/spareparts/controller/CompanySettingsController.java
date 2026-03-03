package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.request.UpdateCompanySettingsRequest;
import sn.symmetry.spareparts.dto.response.CompanySettingsResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.service.CompanySettingsService;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class CompanySettingsController {

    private final CompanySettingsService companySettingsService;

    @GetMapping
    public ResponseEntity<ApiResponse<CompanySettingsResponse>> getSettings() {
        return ResponseEntity.ok(ApiResponse.success(companySettingsService.getSettings()));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CompanySettingsResponse>> updateSettings(
            @Valid @RequestBody UpdateCompanySettingsRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Settings updated successfully",
                companySettingsService.updateSettings(request)));
    }
}
