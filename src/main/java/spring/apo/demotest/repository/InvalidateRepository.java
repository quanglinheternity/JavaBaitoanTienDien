package spring.apo.demotest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.apo.demotest.entity.InvalidatedToken;

@Repository
public interface InvalidateRepository extends JpaRepository<InvalidatedToken, String> {}
