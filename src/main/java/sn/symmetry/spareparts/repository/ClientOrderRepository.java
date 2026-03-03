package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.ClientOrder;
import sn.symmetry.spareparts.enums.OrderStatus;

@Repository
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {

    boolean existsByOrderNumber(String orderNumber);

    Page<ClientOrder> findByCustomerId(Long customerId, Pageable pageable);

    Page<ClientOrder> findByStatus(OrderStatus status, Pageable pageable);

    Page<ClientOrder> findByCustomerIdAndStatus(Long customerId, OrderStatus status, Pageable pageable);
}
