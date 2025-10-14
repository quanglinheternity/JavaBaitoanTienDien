package spring.apo.demotest.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.AuthenticationRequest;
import spring.apo.demotest.dto.request.IntrospectRequest;
import spring.apo.demotest.dto.request.LogoutRequest;
import spring.apo.demotest.dto.request.RefreshRequest;
import spring.apo.demotest.dto.response.AuthenticationResponse;
import spring.apo.demotest.dto.response.IntrospectResponse;
import spring.apo.demotest.entity.AppUser;
import spring.apo.demotest.entity.InvalidatedToken;
import spring.apo.demotest.entity.VerificationCode;
import spring.apo.demotest.exception.AppException;
import spring.apo.demotest.exception.ErrorCode;
import spring.apo.demotest.repository.InvalidateRepository;
import spring.apo.demotest.repository.UserRepository;
import spring.apo.demotest.repository.VerificationCodeRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;
    InvalidateRepository invalidateRepository;
    VerificationCodeRepository verificationCodeRepository;
    VerificationService verificationService;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.vaild-duration}")
    protected Long VAILD_DURATION;

    @NonFinal
    @Value("${jwt.refresh-duration}")
    protected Long REFRESH_DURATION;


    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean inValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            inValid = false;
        }
        return IntrospectResponse.builder().valid(inValid).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsernameAndDeletedFalse(request.getUsername())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));   
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isMatch) throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        boolean isVerified = user.isVerified();
        if (!isVerified) throw new AppException(ErrorCode.USER_NOT_VERIFIED);
        var token = generateToken(user);
         return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }
    public void logOut(LogoutRequest token) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(token.getToken(), true);
            String jwti = signToken.getJWTClaimsSet().getJWTID();
            Date issueTime = signToken.getJWTClaimsSet().getIssueTime();
            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jwti).expiryTime(issueTime).build();
            invalidateRepository.save(invalidatedToken);
        } catch (AppException e) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        // kiểm tra hiệu lực
        var signedJWT = verifyToken(request.getToken(), true);
        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
        invalidateRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user =
                userRepository.findByUsernameAndDeletedFalse(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }
    @Transactional
    public String verifyCode(String userId, String code) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        VerificationCode verification = verificationCodeRepository
            .findByUserIdAndCode(userId, code)
            .orElseThrow(() -> new AppException(ErrorCode.INVALID_VERIFICATION_CODE));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_CODE_EXPIRED);
        }
        
        user.setVerified(true);  // Thêm cột verified trong user table
        userRepository.save(user);
        verificationCodeRepository.deleteByUserId(userId);
        return "Xác minh tài khoản thành công!";
    }
    @Transactional
    public String restVerificationCode(String userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.isVerified()) {
            throw new AppException(ErrorCode.USER_ALREADY_VERIFIED);
        }
        verificationCodeRepository.deleteByUserId(user.getId());
        verificationService.createAndSendVerificationCode(user);
        return "Bạn hãy kiểm tra mail để lấy mã xác minh";
    }
    
        String generateToken(AppUser user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VAILD_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", user.getRole())
                .build();
        Payload payload = new Payload(claims.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();

        } catch (JOSEException e) {

            log.error("Error while generating token", e);
            throw new RuntimeException(e);
        }
    }
    SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getExpirationTime()
                        .toInstant()
                        .plus(REFRESH_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) {

            throw new AppException((isRefresh) ? ErrorCode.TOKEN_EXPIRED : ErrorCode.AUTHENTICATION_FAILED);
        }
        if (invalidateRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        ;
        return signedJWT;
    }
    
}
