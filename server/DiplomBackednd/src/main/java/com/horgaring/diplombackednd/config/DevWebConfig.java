package com.horgaring.diplombackednd.config;

import com.horgaring.diplombackednd.user.City;
import com.horgaring.diplombackednd.user.CityRepository;
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
    public CommandLineRunner loadData(UserRepository userRepository, CityRepository cityRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) return;

            City moscow = new City();
            moscow.setName("Москва");
            City spb = new City();
            spb.setName("Санкт-Петербург");
            City kazan = new City();
            kazan.setName("Казань");
            cityRepository.saveAll(List.of(moscow, spb, kazan));

            var pw = passwordEncoder.encode("secretpassword1");
            var testPw = passwordEncoder.encode("test123");
            var adminPw = passwordEncoder.encode("admin123");
            userRepository.saveAll(List.of(
                    User.builder()
                            .email("test@test.com")
                            .firstName("Тест")
                            .lastName("Тестовый")
                            .gender(Gender.Male)
                            .password(testPw)
                            .birthDate(LocalDate.of(2000, 6, 15))
                            .bio("Люблю путешествовать и пробовать новую еду. Ищу собеседника для прогулок по городу.")
                            .homeTown(moscow)
                            .active(true)
                            .verified(true)
                            .build(),
                    User.builder()
                            .email("Anna1@mail.ru")
                            .firstName("Анна")
                            .lastName("Арматова")
                            .gender(Gender.Female)
                            .password(pw)
                            .avatarUrl("/Untitled1.jpg")
                            .birthDate(LocalDate.of(2008, 3, 28))
                            .bio("Творческая натура. Рисую, пою, танцую. Мечтаю посетить Японию.")
                            .homeTown(moscow)
                            .active(true)
                            .verified(true)
                            .build(),
                    User.builder()
                            .email("Sveta@mail.ru")
                            .firstName("Света")
                            .lastName("Лимонова")
                            .gender(Gender.Female)
                            .password(pw)
                            .avatarUrl("/Untitled2.jpg")
                            .birthDate(LocalDate.of(2006, 2, 20))
                            .bio("Кофеман и книголюб. Обожаю долгие прогулки и разговоры по душам.")
                            .homeTown(spb)
                            .active(true)
                            .verified(true)
                            .build(),
                    User.builder()
                            .email("Olga@mail.ru")
                            .firstName("Ольга")
                            .lastName("Козенная")
                            .gender(Gender.Female)
                            .password(pw)
                            .birthDate(LocalDate.of(2006, 8, 12))
                            .avatarUrl("/Untitled3.jpg")
                            .bio("Спортсменка и оптимистка. Бегаю по утрам, люблю йогу и здоровое питание.")
                            .homeTown(kazan)
                            .active(true)
                            .verified(true)
                            .build(),
                    User.builder()
                            .email("Gulnara@mail.ru")
                            .firstName("Гульнара")
                            .lastName("Фрогова")
                            .gender(Gender.Female)
                            .password(pw)
                            .avatarUrl("/Untitled4.jpg")
                            .birthDate(LocalDate.of(2008, 3, 12))
                            .bio("Фотограф-любитель. Лучшие кадры получаются на закате. Ищу единомышленника.")
                            .homeTown(moscow)
                            .active(true)
                            .verified(true)
                            .build(),
                    User.builder()
                            .email("Anna2@mail.ru")
                            .firstName("Тамила")
                            .lastName("Соколова")
                            .gender(Gender.Female)
                            .password(pw)
                            .avatarUrl("/Untitled5.jpg")
                            .birthDate(LocalDate.of(2007, 1, 16))
                            .bio("Учусь на дизайнера. Обожаю моду и красивые места для фото.")
                            .homeTown(spb)
                            .active(true)
                            .verified(true)
                            .build(),
                    User.builder()
                            .email("Dima@mail.ru")
                            .firstName("Дима")
                            .lastName("Стульчиков")
                            .gender(Gender.Male)
                            .password(pw)
                            .avatarUrl("/Untitled5.jpg")
                            .birthDate(LocalDate.of(2006, 4, 28))
                            .bio("Геймер и программист. Ищу девушку, которая разделит мои увлечения.")
                            .homeTown(kazan)
                            .active(true)
                            .verified(true)
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
