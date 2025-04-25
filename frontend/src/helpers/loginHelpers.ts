import { mockProfiles } from '../mockData/mockProfiles';
import userStore from '../stores/userStore';
import { User, UserEducation, UserJob } from '../types/user';
import { getAuthToken } from './authHelper';

// Login function
export function checkLogin(username: string, password: string): boolean {
  // TODO In this function we would need to call the API to check the user's profile
  const user = mockProfiles.find((profile: User) => profile.username === username && profile.password === password);
  if (user) {
    userStore.setUser(user);
    return true;
  }
  return false;
}

export async function registerUser(userData: {
  firstName: string;
  lastName: string;
  email: string;
  location: string;
  birthday: string;
  jobs: UserJob[];
  education: UserEducation[];
}) {
  try {
    const authToken = await getAuthToken();

    const response = await fetch('http://localhost:8080/users', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify({
        ...userData,
      }),
    });

    if (!response.ok) {
      throw new Error(`Registration failed: ${response.status}`);
    }

    const createdUser = await response.json();
    if (createdUser) {
      userStore.setUser(createdUser);
    }

    return { success: true };
  } catch (error) {
    console.error('Registration error:', error);
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Registration failed. Please try again.',
    };
  }
}
