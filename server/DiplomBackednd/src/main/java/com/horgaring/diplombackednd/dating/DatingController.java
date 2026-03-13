package com.horgaring.diplombackednd.dating;

import com.horgaring.diplombackednd.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dating")
@RequiredArgsConstructor
public class DatingController {

    private final DatingService datingService;

    @GetMapping("/candidates")
    public ResponseEntity<List<UserCardDto>> getCandidates(@AuthenticationPrincipal User user,
                                                           @RequestParam("page_size") Integer pageSize,
                                                           @RequestParam("page_number") Integer pageNumber) {
        return ResponseEntity.ok(datingService.getCandidates(user.getId(), pageSize, pageNumber));
    }

    @PostMapping("/swipe")
    public ResponseEntity<SwipeResponse> swipe(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SwipeRequest request
    ) {
        return ResponseEntity.ok(datingService.swipe(user.getId(), request));
    }

    @GetMapping("/matches")
    public ResponseEntity<List<MatchDto>> getMatches(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(datingService.getMatches(user.getId()));
    }
}
