package com.retailvault.repository.oltp;

import com.retailvault.entity.oltp.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Integer> {
    @Query("""
        SELECT il FROM InventoryLog il
        JOIN FETCH il.product p
        JOIN FETCH p.supplier
        JOIN FETCH il.store
        WHERE il.movementDate >= :since
    """)
    List<InventoryLog> findAllWithDetailsSince(LocalDateTime since);
}
