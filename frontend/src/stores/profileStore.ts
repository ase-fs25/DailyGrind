import { Profile, ProfileInfo } from '../types/profile';

class ProfileStore {
  private profile: Profile = {
    userId: 0,
    username: '',
    password: '',
    profileInfo: {
      profilePicture: '',
      location: '',
      education: '',
      workExperience: '',
    },
  };

  // Setter / Getter for the entire profile
  setProfile(profile: Profile) {
    this.profile = profile;
  }

  updateProfileInfoField(field: keyof ProfileInfo, value: string) {
    if (this.profile) {
      this.profile = {
        ...this.profile,
        profileInfo: {
          ...this.profile.profileInfo,
          [field]: value,
        },
      };
    }
  }

  getProfile(): Profile {
    return this.profile;
  }

  deleteProfile(): void {
    this.profile = {
      userId: 0,
      username: '',
      password: '',
      profileInfo: {
        profilePicture: '',
        location: '',
        education: '',
        workExperience: '',
      },
    };
  }

  // Getters for individual fields
  get getUserId(): number {
    return this.profile.userId;
  }

  get getUsername(): string {
    return this.profile.username;
  }

  get getPassword(): string {
    return this.profile.password;
  }

  get getProfilePicture(): string {
    return this.profile.profileInfo.profilePicture;
  }

  get getLocation(): string {
    return this.profile.profileInfo.location;
  }

  get getEducation(): string {
    return this.profile.profileInfo.education;
  }

  get getWorkExperience(): string {
    return this.profile.profileInfo.workExperience;
  }

  // Setters for individual fields
  setUserId(value: number) {
    this.profile.userId = value;
  }

  setUsername(value: string) {
    this.profile.username = value;
  }

  setPassword(value: string) {
    this.profile.password = value;
  }

  setProfilePicture(value: string) {
    this.profile.profileInfo.profilePicture = value;
  }

  setLocation(value: string) {
    this.profile.profileInfo.location = value;
  }

  setEducation(value: string) {
    this.profile.profileInfo.education = value;
  }

  setWorkExperience(value: string) {
    this.profile.profileInfo.workExperience = value;
  }
}

const profileStore = new ProfileStore();
export default profileStore;
