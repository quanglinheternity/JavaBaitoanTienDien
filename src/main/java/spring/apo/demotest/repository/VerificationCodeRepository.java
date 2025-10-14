package spring.apo.demotest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.apo.demotest.entity.VerificationCode;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByUserIdAndCode(String userId, String code);

    void deleteByUserId(String userId);
}
