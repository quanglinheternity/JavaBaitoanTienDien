package spring.apo.demotest.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import spring.apo.demotest.entity.AppUser;
import spring.apo.demotest.entity.VerificationCode;
import spring.apo.demotest.repository.VerificationCodeRepository;

@Service
@RequiredArgsConstructor
public class VerificationService {
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;

    public void createAndSendVerificationCode(AppUser savedUser) {
        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        // ⏰ Hạn 10 phút
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        VerificationCode codeEntity = new VerificationCode();
        codeEntity.setUserId(savedUser.getId());
        codeEntity.setCode(verificationCode);
        codeEntity.setCreatedAt(LocalDateTime.now());
        codeEntity.setExpiresAt(expiresAt);
        verificationCodeRepository.save(codeEntity);
        emailService.sendSimpleEmail(savedUser.getUsername(), savedUser.getLastName(), verificationCode);
    }
}
