package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateSupplierRequest;
import sn.symmetry.spareparts.dto.request.UpdateSupplierRequest;
import sn.symmetry.spareparts.dto.response.SupplierResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

import java.util.UUID;

public interface SupplierService {

    PagedResponse<SupplierResponse> getAllSuppliers(Pageable pageable);

    SupplierResponse getSupplierById(UUID id);

    SupplierResponse createSupplier(CreateSupplierRequest request);

    SupplierResponse updateSupplier(UUID id, UpdateSupplierRequest request);

    void deleteSupplier(UUID id);
}
