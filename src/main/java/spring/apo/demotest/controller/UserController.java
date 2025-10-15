package spring.apo.demotest.controller;

import java.io.IOException;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(consumes = "multipart/form-data")
    ApiResponse<UserResponseHaspassword> createUser(
            @ModelAttribute @Valid UserCreateRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar)
            throws IOException {
        log.info("Inside createUser");
        UserResponseHaspassword user = userService.createdUser(request, avatar);
        return ApiResponse.<UserResponseHaspassword>builder().data(user).build();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') || #id == principal.id")
    ApiResponse<UserResponse> getUser(@PathVariable("id") String id) {
        log.info("Inside getUser");
        return ApiResponse.<UserResponse>builder().data(userService.getUser(id)).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponseHaspassword> updateUser(
            @PathVariable("id") String id,
            @ModelAttribute @Valid UserUpdateRequest request,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar)
            throws IOException {

        log.info("Inside updateUser");

        UserResponseHaspassword updatedUser = userService.updateUser(id, request, avatar);

        return ApiResponse.<UserResponseHaspassword>builder().data(updatedUser).build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder().data(userService.getMyInfo()).build();
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
