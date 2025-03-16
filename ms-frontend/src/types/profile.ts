export interface Profile {
  userId: number;
  username: string;
  password: string;
  profileInfo: {
    profilePicture: string;
    location: string;
    education: string;
    workExperience: string;
  }; 
}

export interface ProfileInfo {
  profilePicture: string;
  location: string;
  education: string;
  workExperience: string;
}
