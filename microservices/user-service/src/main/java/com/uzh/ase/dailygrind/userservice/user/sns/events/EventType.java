package com.uzh.ase.dailygrind.userservice.user.sns.events;

/**
 * Enum representing the different types of events
 * that can occur within the user service. These events
 * are used for publishing user-related changes via SNS.
 */
public enum EventType {

    /** Event indicating a new user was created. */
    USER_CREATED("USER_CREATED"),

    /** Event indicating an existing user was updated. */
    USER_UPDATED("USER_UPDATED"),

    /** Event indicating a user was deleted. */
    USER_DELETED("USER_DELETED"),

    /** Event indicating a new friendship was established. */
    FRIENDSHIP_CREATED("FRIENDSHIP_CREATED"),

    /** Event indicating an existing friendship was removed. */
    FRIENDSHIP_DELETED("FRIENDSHIP_DELETED");

    private final String eventType;

    EventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Returns the string representation of the event type.
     *
     * @return the event type as a string
     */
    public String getEventType() {
        return eventType;
    }
}
