package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateCustomerRequest;
import sn.symmetry.spareparts.dto.request.UpdateCustomerRequest;
import sn.symmetry.spareparts.dto.response.CustomerResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

public interface CustomerService {

    PagedResponse<CustomerResponse> getAllCustomers(Pageable pageable);

    CustomerResponse getCustomerById(Long id);

    CustomerResponse createCustomer(CreateCustomerRequest request);

    CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request);

    void deleteCustomer(Long id);
}
