package com.horgaring.diplombackednd.dating;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwipeRequest {

    @NotNull
    private UUID targetUserId;

    @NotNull
    private Boolean liked;
}
