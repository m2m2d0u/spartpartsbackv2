package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateCustomerRequest;
import sn.symmetry.spareparts.dto.request.UpdateCustomerRequest;
import sn.symmetry.spareparts.dto.response.CustomerResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

import java.util.UUID;

public interface CustomerService {

    PagedResponse<CustomerResponse> getAllCustomers(Pageable pageable);

    CustomerResponse getCustomerById(UUID id);

    CustomerResponse createCustomer(CreateCustomerRequest request);

    CustomerResponse updateCustomer(UUID id, UpdateCustomerRequest request);

    void deleteCustomer(UUID id);
}
