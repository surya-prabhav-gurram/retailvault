package com.retailvault.repository.warehouse;

import com.retailvault.entity.warehouse.DimDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DimDateRepository extends JpaRepository<DimDate, Integer> {
    Optional<DimDate> findByDateKey(Integer dateKey);
}
