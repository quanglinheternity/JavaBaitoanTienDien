package spring.apo.demotest.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    public void sendSimpleEmail(String to, String subject, String text) {
         try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ITS <quanglinhlazy@gmail.com>"); // bạn nên để email thật, tránh "ITS" ko hợp lệ
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);

            log.info("Email sent to: {} | Subject: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to: {} | Error: {}", to, e.getMessage(), e);
        }
    }
    @Async
    public void sendEmailAsync(EmailRequest request) {
        sendSimpleEmail(request.getTo(), request.getSubject(), request.getMessage());
    }
}
