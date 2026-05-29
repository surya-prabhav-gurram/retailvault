package com.retailvault.repository.warehouse;
import com.retailvault.entity.warehouse.DimStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface DimStoreRepository extends JpaRepository<DimStore, Integer> {
    Optional<DimStore> findByStoreIdAndIsCurrent(Integer storeId, Boolean isCurrent);
}
