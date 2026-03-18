package com.horgaring.diplombackednd.mail;

import com.horgaring.diplombackednd.user.User;
import com.horgaring.diplombackednd.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class EmailController {
    private  final EmailService emailService;

    @PostMapping("activate/{code}")
    public User activateCode(@PathVariable UUID code) {
        return emailService.activateAccount(code);
    }
}
