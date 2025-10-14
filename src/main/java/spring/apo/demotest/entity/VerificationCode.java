package spring.apo.demotest.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "verification_codes")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    String userId;

    @Column(nullable = false)
    String code;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    LocalDateTime expiresAt;
}
