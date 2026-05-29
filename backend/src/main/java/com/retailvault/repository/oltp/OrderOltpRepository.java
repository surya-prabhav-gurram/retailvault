package com.retailvault.repository.oltp;

import com.retailvault.entity.oltp.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderOltpRepository extends JpaRepository<Order, Integer> {
}
