package com.retailvault.repository.warehouse;
import com.retailvault.entity.warehouse.DimCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface DimCustomerRepository extends JpaRepository<DimCustomer, Integer> {
    Optional<DimCustomer> findByCustomerIdAndIsCurrent(Integer customerId, Boolean isCurrent);
}
