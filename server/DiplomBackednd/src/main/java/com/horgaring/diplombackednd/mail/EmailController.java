package com.horgaring.diplombackednd.mail;

import com.horgaring.diplombackednd.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("activate/{code}")
    public User activateCode(@PathVariable UUID code) {
        return emailService.activateAccount(code);
    }
}
