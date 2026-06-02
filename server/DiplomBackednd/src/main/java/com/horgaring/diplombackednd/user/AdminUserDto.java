package com.horgaring.diplombackednd.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean active;
    private Boolean verified;
    private String gender;
    private LocalDate birthDate;
    private String bio;
    private String avatarUrl;
    private UUID cityId;
    private String cityName;
    private Instant createdAt;
}
