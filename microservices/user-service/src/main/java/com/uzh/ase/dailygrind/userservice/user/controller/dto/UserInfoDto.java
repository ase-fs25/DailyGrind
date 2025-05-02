package com.uzh.ase.dailygrind.userservice.user.controller.dto;

public class UserInfoDto {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String birthday;
    private String location;
    private int numFollowers;
    private int numFollowing;
    private String profilePictureUrl;
    private boolean isFollowed;

   
    private String requestId;

    // Constructor
    public UserInfoDto(String userId, String email, String firstName, String lastName,
                       String birthday, String location, int numFollowers, int numFollowing,
                       String profilePictureUrl, boolean isFollowed) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.location = location;
        this.numFollowers = numFollowers;
        this.numFollowing = numFollowing;
        this.profilePictureUrl = profilePictureUrl;
        this.isFollowed = isFollowed;
    }

    // Getters and setters for all fields
    // (Tip: use your IDE to generate them or Lombok if allowed)

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getNumFollowers() { return numFollowers; }
    public void setNumFollowers(int numFollowers) { this.numFollowers = numFollowers; }

    public int getNumFollowing() { return numFollowing; }
    public void setNumFollowing(int numFollowing) { this.numFollowing = numFollowing; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public boolean isFollowed() { return isFollowed; }
    public void setFollowed(boolean followed) { isFollowed = followed; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
}
