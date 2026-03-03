package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.ClientOrder;

@Repository
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
}
