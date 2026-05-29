package com.retailvault.repository.oltp;
import com.retailvault.entity.oltp.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CustomerOltpRepository extends JpaRepository<Customer, Integer> {}
