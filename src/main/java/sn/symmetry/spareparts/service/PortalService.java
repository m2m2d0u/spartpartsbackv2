package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.portal.PortalCreateOrderRequest;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalCarBrandResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalCarModelResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalCategoryResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalOrderConfirmationResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalPartDetailResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalPartResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalStoreConfigResponse;

import java.util.List;
import java.util.UUID;

public interface PortalService {

    PagedResponse<PortalPartResponse> searchParts(String name, UUID categoryId, UUID carBrandId, UUID carModelId, Pageable pageable);

    PortalPartDetailResponse getPartById(UUID id);

    List<PortalCategoryResponse> getCategories();

    List<PortalCarBrandResponse> getCarBrands();

    List<PortalCarModelResponse> getCarModels(UUID brandId);

    PortalStoreConfigResponse getStoreConfig();

    PortalOrderConfirmationResponse createOrder(PortalCreateOrderRequest request);

    PortalOrderConfirmationResponse getOrderByNumber(String orderNumber);
}
