package spring.apo.demotest.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.EmailRequest;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {
    JavaMailSender mailSender;
    SpringTemplateEngine templateEngine;

    @Async
    public void sendSimpleEmail(String to, String name, String code) {
        try {
            // Tạo context Thymeleaf
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("code", code);

            // Load template
            String htmlContent = templateEngine.process("mail/verify-email", context);

            // Tạo MimeMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom("ITS <quanglinhlazy@gmail.com>");
            helper.setTo(to);
            helper.setSubject("Xác minh tài khoản ITS");
            helper.setText(htmlContent, true); // true = nội dung HTML

            // Gửi mail
            mailSender.send(mimeMessage);
            log.info(" Email template sent to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw new RuntimeException("Gửi email thất bại");
        }
    }
    @Async
    public void sendEmailAsync(EmailRequest request) {
        sendSimpleEmail(request.getTo(), request.getName(), request.getCode());
    }
}
