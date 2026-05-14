package com.horgaring.diplombackednd.config;

import com.horgaring.diplombackednd.user.Gender;
import com.horgaring.diplombackednd.user.Role;
import com.horgaring.diplombackednd.user.User;
import com.horgaring.diplombackednd.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.util.List;


@Configuration
@Profile("dev")
public class DevWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/test-avatars/");

    }

    @Bean
    public CommandLineRunner loadData(UserRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            var pw = passwordEncoder.encode("secretpassword1");
            var adminPw = passwordEncoder.encode("admin123");
            repository.saveAll(List.of(
                    User.builder()
                            .email("Anna1@mail.ru")
                            .firstName("Anna")
                            .lastName("Armatova")
                            .gender(Gender.Female)
                            .password(pw)
                            .avatarUrl("/Untitled1.jpg")
                            .birthDate(LocalDate.of(2008, 3, 28))
                            .build(),
                    User.builder()
                            .email("Sveta@mail.ru")
                            .firstName("Sveta")
                            .lastName("Limonova")
                            .gender(Gender.Female)
                            .password(pw)
                            .avatarUrl("/Untitled2.jpg")
                            .birthDate(LocalDate.of(2006, 2, 20))
                            .build(),
                    User.builder()
                            .email("Olga@mail.ru")
                            .firstName("Olga")
                            .lastName("Varikoznaya")
                            .gender(Gender.Female)
                            .password(pw)
                            .birthDate(LocalDate.of(2006, 8, 12))
                            .avatarUrl("/Untitled3.jpg")
                            .build(),
                    User.builder()
                            .email("Gulnara@mail.ru")
                            .firstName("Gulnara")
                            .lastName("Frogova")
                            .gender(Gender.Female)
                            .password(pw)
                            .avatarUrl("/Untitled4.jpg")
                            .birthDate(LocalDate.of(2008, 3, 12))
                            .build(),
                    User.builder()
                            .email("Anna2@mail.ru")
                            .firstName("Tamila")
                            .lastName("Sokolova")
                            .gender(Gender.Female)
                            .password(pw)
                            .avatarUrl("/Untitled5.jpg")
                            .birthDate(LocalDate.of(2007, 1, 16))
                            .build(),
                    User.builder()
                            .email("Dima@mail.ru")
                            .firstName("Dima")
                            .lastName("Buka")
                            .gender(Gender.Male)
                            .password(pw)
                            .avatarUrl("/Untitled5.jpg")
                            .birthDate(LocalDate.of(2006, 4, 28))
                            .build(),
                    User.builder()
                            .email("admin@admin.com")
                            .firstName("Admin")
                            .lastName("Adminov")
                            .role(Role.ADMIN)
                            .password(adminPw)
                            .verified(true)
                            .active(true)
                            .build()
            ));
        };
    }
}
