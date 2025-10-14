package spring.apo.demotest.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.AuthenticationRequest;
import spring.apo.demotest.dto.request.IntrospectRequest;
import spring.apo.demotest.dto.request.LogoutRequest;
import spring.apo.demotest.dto.request.RefreshRequest;
import spring.apo.demotest.dto.response.ApiResponse;
import spring.apo.demotest.dto.response.AuthenticationResponse;
import spring.apo.demotest.dto.response.IntrospectResponse;
import spring.apo.demotest.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                    .data(result)
                    .build();
    }
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authticated(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().data(result).build();
    }
    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authticated(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder().data(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logOut(request);
        return ApiResponse.<Void>builder().message("Đăng xuất thành công").build();
    }
    @PostMapping("{userId}/verify")
    ApiResponse<Void> verifyCode(@PathVariable(value = "userId") String userId, @RequestParam String code) {
        String result = authenticationService.verifyCode(userId, code);
        return ApiResponse.<Void>builder().code(1000).message(result).build();
    }
    @PostMapping("{userId}/resend")
    ApiResponse<Void> resendCode(@PathVariable(value = "userId") String userId) {
        String result = authenticationService.restVerificationCode(userId);
        return ApiResponse.<Void>builder().code(1000).message(result).build();
    }

}   
