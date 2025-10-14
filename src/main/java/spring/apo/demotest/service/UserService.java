package spring.apo.demotest.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.UserCreateRequest;
import spring.apo.demotest.dto.request.UserUpdateRequest;
import spring.apo.demotest.dto.response.UserResponse;
import spring.apo.demotest.dto.response.UserResponseHaspassword;
import spring.apo.demotest.entity.AppUser;
import spring.apo.demotest.enums.Role;
import spring.apo.demotest.exception.AppException;
import spring.apo.demotest.exception.ErrorCode;
import spring.apo.demotest.mapper.UserMapper;
import spring.apo.demotest.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class UserService {

    @Value("${image.upload-dir}")
    String imageUploadDir; 

    final UserRepository userRepository; 
    final PasswordEncoder passwordEncoder;
    final UserMapper userMapper;
    final VerificationService verificationService;
    public UserResponseHaspassword createdUser(UserCreateRequest request, MultipartFile avatar) throws IOException {
        log.info("Creating user...");

        if (avatar != null && !avatar.isEmpty()) {
            Path uploadDir = Paths.get(imageUploadDir);
            Files.createDirectories(uploadDir);

            String fileName = System.currentTimeMillis() + "_" + avatar.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);

            avatar.transferTo(filePath.toFile());

            request.setProfileImage(fileName);
        }

        AppUser user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        
        user.setRole(Role.USER.name());

        try {
            AppUser savedUser = userRepository.save(user);
            verificationService.createAndSendVerificationCode(savedUser);
            UserResponseHaspassword response = userMapper.toUserResponseHaspassword(savedUser);
            response.setPassword(request.getPassword()); 
            return response;

        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }
    
    

    public UserResponse getUser( String id) {
        AppUser user = userRepository.findById(id)
                            .orElseThrow(() -> 
                            new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponseHaspassword updateUser(String id, UserUpdateRequest request, MultipartFile avatar) throws IOException {
        log.info("Updating user with ID: {}", id);

        AppUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (avatar != null && !avatar.isEmpty()) {
            Path uploadDir = Paths.get(imageUploadDir);
            Files.createDirectories(uploadDir);

            if (existingUser.getProfileImage() != null && !existingUser.getProfileImage().isEmpty()) {
                Path oldImagePath = uploadDir.resolve(existingUser.getProfileImage());
                Files.deleteIfExists(oldImagePath);
                log.info("Deleted old avatar: {}", oldImagePath);
            }

            String fileName = System.currentTimeMillis() + "_" + avatar.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            avatar.transferTo(filePath.toFile());
            log.info("Uploaded new avatar: {}", filePath);

            request.setProfileImage(fileName);
        }else {
            request.setProfileImage(existingUser.getProfileImage());
        }

        userMapper.updateAppUserFromRequest(request, existingUser);

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        AppUser savedUser = userRepository.save(existingUser);

        UserResponseHaspassword response = userMapper.toUserResponseHaspassword(savedUser);
        response.setPassword(request.getPassword()); 
        return response;
    }

    public UserResponse getMyInfo() {
        log.debug("Đang gọi API...");   // chỉ hiện khi level <= DEBUG
            log.info("App khởi động xong"); // hiện khi level <= INFO
            log.warn("Dữ liệu thiếu field"); // hiện khi level <= WARN
            log.error("Không kết nối được DB"); // luôn hiện khi level <= ERROR
        var conText = SecurityContextHolder.getContext();
        String name = conText.getAuthentication().getName();

        AppUser user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }
    //  public List<AppUser> getAllUsers() {
    //     return userRepository.findAllWithHistories();
    // }
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
    List<AppUser> users = userRepository.findByDeletedFalse();
    // users.forEach(user -> {
    //     log.info("User: {}, UsageHistories size: {}", 
    //              user.getUsername(), 
    //              user.getUsageHistories() != null ? user.getUsageHistories().size() : "null");
        
    //     if (user.getUsageHistories() != null) {
    //         user.getUsageHistories().forEach(h -> 
    //             log.info("  - History ID: {}, KWH: {}", h.getId(), h.getKwh())
    //         );
    //     }
    // });
    return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsersByDeleted() {
    List<AppUser> users = userRepository.findByDeletedTrue();
    // users.forEach(user -> {
    //     log.info("User: {}, UsageHistories size: {}", 
    //              user.getUsername(), 
    //              user.getUsageHistories() != null ? user.getUsageHistories().size() : "null");
        
    //     if (user.getUsageHistories() != null) {
    //         user.getUsageHistories().forEach(h -> 
    //             log.info("  - History ID: {}, KWH: {}", h.getId(), h.getKwh())
    //         );
    //     }
    // });
    return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public void delete(String id) {
        AppUser user = userRepository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.isDeleted()) {
            throw new AppException(ErrorCode.USER_ALREADY_DELETED);
        }
        userRepository.deleteById(id);
    }
    @Transactional
    public void restoreUser(String userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!user.isDeleted()) {
            throw new AppException(ErrorCode.USER_NOT_DELETED); 
        }

        user.setDeleted(false);
        userRepository.save(user); // update lại deleted = false
    }
}
