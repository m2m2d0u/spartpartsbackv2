package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.TaxRate;

@Repository
public interface TaxRateRepository extends JpaRepository<TaxRate, Long> {
}
