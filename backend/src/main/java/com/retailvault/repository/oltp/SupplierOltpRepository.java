package com.retailvault.repository.oltp;
import com.retailvault.entity.oltp.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SupplierOltpRepository extends JpaRepository<Supplier, Integer> {}
