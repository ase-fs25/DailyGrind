import { mockProfiles } from '../mockData/mockProfiles';
import userStore from '../stores/userStore';
import { User } from '../types/user';
import { fetchAuthSession } from 'aws-amplify/auth';

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

//TODO: include job and education history
export async function registerUser(userData: {
  firstName: string;
  lastName: string;
  email: string;
  location: string;
  birthday: string;
}) {
  try {
    const session = await fetchAuthSession();
    const authToken = session.tokens?.accessToken.toString();

    if (!authToken) {
      throw new Error('Authentication failed. Please log in again.');
    }

    const response = await fetch('http://localhost:8080/users', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify({
        ...userData,
        jobs: [],
        education: [],
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
