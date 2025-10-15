package spring.apo.demotest.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.apo.demotest.entity.AppUser;
import spring.apo.demotest.entity.UsageHistory;

@Repository
public interface UsageHistroryRepository extends JpaRepository<UsageHistory, Long> {
    List<UsageHistory> findAllByUserId(String userId);

    boolean existsByUserAndUsageDateBetween(AppUser user, LocalDate start, LocalDate end);

    List<UsageHistory> findByUserAndUsageDateBetween(AppUser user, LocalDate start, LocalDate end);
}
