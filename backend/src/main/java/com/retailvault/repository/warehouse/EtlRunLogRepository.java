package com.retailvault.repository.warehouse;
import com.retailvault.entity.warehouse.EtlRunLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface EtlRunLogRepository extends JpaRepository<EtlRunLog, Long> {
    List<EtlRunLog> findTop20ByOrderByStartedAtDesc();
}
