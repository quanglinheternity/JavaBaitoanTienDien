package spring.apo.demotest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.UserCreateRequest;
import spring.apo.demotest.dto.request.UserUpdateRequest;
import spring.apo.demotest.dto.response.ApiResponse;
import spring.apo.demotest.dto.response.UserResponse;
import spring.apo.demotest.dto.response.UserResponseHaspassword;
import spring.apo.demotest.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
     UserService userService;

    @PostMapping
    ApiResponse<UserResponseHaspassword> createUser(@RequestBody @Valid UserCreateRequest request) {
        log.info("Inside createUser");
        
        return ApiResponse.<UserResponseHaspassword>builder()
                .data(userService.createdUser(request))
                .build();
    }
    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') || #id == principal.id")
    ApiResponse<UserResponse> getUser(@PathVariable("id") String id) {
        log.info("Inside getUser");
        return ApiResponse.<UserResponse>builder()
                .data(userService.getUser(id))
                .build();
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') ")
    ApiResponse<UserResponseHaspassword> updateUser(@PathVariable("id") String id, @RequestBody @Valid UserUpdateRequest request) {
        log.info("Inside updateUser");
        return ApiResponse.<UserResponseHaspassword>builder()
                .data(userService.updateUser(id ,request))
                .build();
    }
    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getMyInfo())
                .build();
    }
    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        log.info("Inside getAllUsers");
        return ApiResponse.<List<UserResponse>>builder()
                .data(userService.getAllUsers())
                .build();
    }
    @GetMapping("/deleted")
    public ApiResponse<List<UserResponse>> getAllUserByDeleted() {
        return ApiResponse.<List<UserResponse>>builder()
                .data(userService.getAllUsersByDeleted())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable String id) {
        userService.delete(id);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(1000)
                .message("Tài khoản được xóa")
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<String>> restoreUser(@PathVariable String id) {
        userService.restoreUser(id);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(1000)
                .message("Tài khoản đã được khôi phục")
                .build();
        return ResponseEntity.ok(response);
    }

}
