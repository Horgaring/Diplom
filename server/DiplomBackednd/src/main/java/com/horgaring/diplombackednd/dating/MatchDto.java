package com.horgaring.diplombackednd.dating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDto {

    private UUID matchId;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String bio;
    private String avatarUrl;
    private Instant matchedAt;
}
