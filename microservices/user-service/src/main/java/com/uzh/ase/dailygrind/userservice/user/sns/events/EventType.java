package com.uzh.ase.dailygrind.userservice.user.sns.events;

public enum EventType {
    USER_CREATED("USER_CREATED"),
    USER_UPDATED("USER_UPDATED"),
    USER_DELETED("USER_DELETED"),
    FRIENDSHIP_CREATED("FRIENDSHIP_CREATED"),
    FRIENDSHIP_DELETED("FRIENDSHIP_DELETED"),;

    private final String eventType;

    EventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }
}
