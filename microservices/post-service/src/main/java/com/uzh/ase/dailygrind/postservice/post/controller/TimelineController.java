package com.uzh.ase.dailygrind.postservice.post.controller;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.TimelineEntryDto;
import com.uzh.ase.dailygrind.postservice.post.service.TimelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * Controller class for managing the user's timeline posts.
 * <p>
 * This class provides an endpoint to retrieve timeline entries for the authenticated user.
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    /**
     * Retrieves timeline posts for the authenticated user.
     * <p>
     * The timeline posts are based on the user's feed and can include posts from people they follow,
     * recent activities, and other relevant posts.
     *
     * @param principal the authenticated user
     * @return a list of timeline entries for the authenticated user
     */
    @Operation(summary = "Get timeline posts for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved timeline")
    @GetMapping("/users/me/timeline")
    public ResponseEntity<List<TimelineEntryDto>> getMyTimeline(Principal principal) {
        return ResponseEntity.ok(timelineService.getTimelineEntries(principal.getName()));
    }
}
