package spring.apo.demotest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import spring.apo.demotest.entity.AppUser;

@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {
    boolean existsByUsername(String username);

    Optional<AppUser> findByUsername(String username);
    @Query("SELECT DISTINCT u FROM AppUser u LEFT JOIN FETCH u.usageHistories")
    List<AppUser> findAllWithUsageHistories();

    List<AppUser> findByDeletedFalse();
    List<AppUser> findByDeletedTrue(); 

    
}
