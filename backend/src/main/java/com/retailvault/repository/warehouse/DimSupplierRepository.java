package com.retailvault.repository.warehouse;
import com.retailvault.entity.warehouse.DimSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface DimSupplierRepository extends JpaRepository<DimSupplier, Integer> {
    Optional<DimSupplier> findBySupplierIdAndIsCurrent(Integer supplierId, Boolean isCurrent);
}
