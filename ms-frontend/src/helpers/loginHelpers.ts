import { mockProfiles } from '../mockData/mockProfiles';
import profileStore from '../stores/profileStore';
import { Profile } from '../types/profile';

// Login function
export function checkLogin(username: string, password: string): boolean {
  // TODO In this function we would need to call the API to check the user's profile
  const user = mockProfiles.find((profile: Profile) => profile.username === username && profile.password === password);
  if (user) {
    profileStore.setProfile(user);
    return true;
  }
  return false;
}

// Registration function
export function registerUser(username: string, password: string): boolean {
  // TODO In this function we would also need to call the API to add the user's profile
  if (mockProfiles.some((profile) => profile.username === username)) {
    return false; // Username already exists
  }

  const newUser: Profile = {
    userId: mockProfiles.length + 1,
    username,
    password,
    profileInfo: {
      profilePicture: 'https://via.placeholder.com/100',
      location: '',
      education: '',
      workExperience: '',
    },
  };

  mockProfiles.push(newUser);
  profileStore.setProfile(newUser);
  return true;
}
