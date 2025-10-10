package spring.apo.demotest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.EmailRequest;
import spring.apo.demotest.service.EmailService;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailController {
    EmailService emailService;
    @PostMapping("/send")
    public String sendAdvancedEmail(@RequestBody EmailRequest request) {
        try {
            // emailService.sendSimpleEmail(
            //     request.getTo(), 
            //     request.getSubject(), 
            //     request.getMessage()
            // );
            emailService.sendEmailAsync(request);
            return "Email đã được gửi thành công!";
        } catch (Exception e) {
            return "Lỗi khi gửi email: " + e.getMessage();
        }
    }
}
