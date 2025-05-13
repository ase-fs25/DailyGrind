package com.uzh.ase.dailygrind.postservice.post.service;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.TimelineEntryDto;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Service layer responsible for handling timeline-related business logic.
 * It retrieves timeline entries for a user, which include the posts of their friends.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TimelineService {

    private final UserService userService;
    private final PostService postService;

    /**
     * Retrieves the timeline entries for a specific user.
     * A timeline entry consists of a friend's post (if they have a daily post) along with their details.
     * The timeline is sorted by the timestamp of the posts in descending order.
     *
     * @param userId  The ID of the user for whom the timeline is being retrieved.
     * @return        A list of TimelineEntryDto objects representing the user's timeline.
     */
    public List<TimelineEntryDto> getTimelineEntries(String userId) {
        log.info("Retrieving timeline entries for user: {}", userId);

        // Retrieve the user's friends from the user service
        List<UserDto> friends = userService.getFriends(userId);
        log.debug("Found {} friends for user {}", friends.size(), userId);

        // Process the timeline entries
        List<TimelineEntryDto> timelineEntries = friends.stream()
            // For each friend, retrieve their daily post
            .map(friend -> {
                log.debug("Retrieving daily post for friend: {}", friend.userId());
                PostDto post = postService.getDailyPostForUser(friend.userId(), userId);
                // Create a TimelineEntryDto only if the friend has a daily post
                return post != null ? new TimelineEntryDto(post, friend) : null;
            })
            // Filter out null entries (friends who don't have a daily post)
            .filter(Objects::nonNull)
            // Sort the timeline entries by post timestamp in descending order
            .sorted(Comparator.comparing((TimelineEntryDto entry) -> entry.post().timestamp(), Comparator.reverseOrder()))
            .toList();

        log.info("Retrieved {} timeline entries for user: {}", timelineEntries.size(), userId);
        return timelineEntries;
    }
}
