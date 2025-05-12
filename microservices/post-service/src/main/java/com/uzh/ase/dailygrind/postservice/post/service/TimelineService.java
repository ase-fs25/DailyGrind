package com.uzh.ase.dailygrind.postservice.post.service;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.TimelineEntryDto;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final UserService userService;
    private final PostService postService;

    public List<TimelineEntryDto> getTimelineEntries(String userId) {
        List<UserDto> friends = userService.getFriends(userId);

        return friends.stream()
            .map(friend -> {
                PostDto post = postService.getDailyPostForUser(friend.userId(), userId);
                return post != null ? new TimelineEntryDto(post, friend) : null;
            })
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing((TimelineEntryDto entry) -> entry.post().timestamp(), Comparator.reverseOrder()))
            .toList();
    }

}
