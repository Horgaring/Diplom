package com.horgaring.diplombackednd.dating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwipeResponse {

    private boolean matched;
    private UUID matchId;
    private UUID matchedUserId;
    private String matchedUserName;
}
