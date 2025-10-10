package spring.apo.demotest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.apo.demotest.dto.response.TierConfigResponse;
import spring.apo.demotest.entity.TierConfig;

@Repository
public interface TierConfigRepository extends JpaRepository<TierConfig, Integer> {
    List<TierConfig> findAllByOrderByMinValueAsc();
    List<TierConfigResponse> findByTierName(String getMessage);
}
