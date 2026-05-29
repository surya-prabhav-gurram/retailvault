package com.retailvault.repository.oltp;

import com.retailvault.entity.oltp.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreOltpRepository extends JpaRepository<Store, Integer> {}
