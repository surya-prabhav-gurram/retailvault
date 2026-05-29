package com.retailvault.repository.oltp;
import com.retailvault.entity.oltp.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProductOltpRepository extends JpaRepository<Product, Integer> {}
