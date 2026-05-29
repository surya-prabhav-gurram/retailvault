package com.retailvault.repository.warehouse;
import com.retailvault.entity.warehouse.DimProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface DimProductRepository extends JpaRepository<DimProduct, Integer> {
    Optional<DimProduct> findByProductIdAndIsCurrent(Integer productId, Boolean isCurrent);
}
