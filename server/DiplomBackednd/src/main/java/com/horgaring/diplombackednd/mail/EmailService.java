package com.horgaring.diplombackednd.mail;

import com.horgaring.diplombackednd.exception.ResourceNotFoundException;
import com.horgaring.diplombackednd.user.User;
import com.horgaring.diplombackednd.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final MailRepository mailRepository;
    private final UserService userService;
    private final AppMailProperties properties;

    private void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

    public void createAndSendMail(String mail) {
        var mailCode = mailRepository.save(MailCode.builder().mail(mail).build());

        sendEmail(mail, properties.getSubject(), String.format(properties.getContentPattern(), mailCode.getCode()));
    }

    public User activateAccount(UUID code) {
        var mailCode = mailRepository.findById(code);

        if (mailCode.isEmpty()) {
            throw new ResourceNotFoundException("mail code", code);
        }

        return userService.activate(mailCode.get().getMail());
    }
}
