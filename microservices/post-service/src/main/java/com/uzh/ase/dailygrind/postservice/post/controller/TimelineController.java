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
 * Controller responsible for handling timeline-related operations.
 * <p>
 * This controller allows users to retrieve their timeline posts.
 * It interacts with the {@link TimelineService} to fetch the timeline entries for the user.
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    /**
     * Retrieves the timeline posts for the currently authenticated user.
     *
     * @param principal the current authenticated user
     * @return a list of timeline entries for the authenticated user
     */
    @Operation(summary = "Get timeline posts for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved timeline")
    @GetMapping("/users/me/timeline")
    public ResponseEntity<List<TimelineEntryDto>> getMyTimeline(Principal principal) {
        return ResponseEntity.ok(timelineService.getTimelineEntries(principal.getName()));
    }

}
