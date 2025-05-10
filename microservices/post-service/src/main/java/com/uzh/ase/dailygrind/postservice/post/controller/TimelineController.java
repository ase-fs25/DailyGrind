package com.uzh.ase.dailygrind.postservice.post.controller;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
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


@RestController
@RequestMapping
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    @Operation(summary = "Get timeline posts for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved timeline")
    @GetMapping("/users/me/timeline")
    public ResponseEntity<List<TimelineEntryDto>> getMyTimeline(Principal principal) {
        return ResponseEntity.ok(timelineService.getTimelineEntries(principal.getName()));
    }

}
