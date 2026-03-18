package com.horgaring.diplombackednd.mail;

import com.horgaring.diplombackednd.user.City;
import com.horgaring.diplombackednd.user.Gender;
import com.horgaring.diplombackednd.user.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mail_codes")
public class MailCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID code;

    @Column(name = "mail")
    private String mail;

}
