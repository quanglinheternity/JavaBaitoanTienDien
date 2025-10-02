package spring.apo.demotest.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository  userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseHaspassword createdUser(UserCreateRequest request) {
        AppUser user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER.name());
         try {
            AppUser saveUser = userRepository.save(user);
            UserResponseHaspassword response = userMapper.toUserResponseHaspassword(saveUser);
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
    public UserResponseHaspassword updateUser(String id, UserUpdateRequest request) {
        AppUser existingUser = userRepository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    // MapStruct sẽ chỉ map các field trong request sang existingUser
        userMapper.updateAppUserFromRequest(request, existingUser);
        
        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        AppUser saveUser = userRepository.save(existingUser);
        UserResponseHaspassword response = userMapper.toUserResponseHaspassword(saveUser);
        response.setPassword(request.getPassword());
        return response;
    }
    public UserResponse getMyInfo() {

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
            // log.info("User {} is already deleted", id);
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
