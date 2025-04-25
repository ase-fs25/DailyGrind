import { fetchAuthSession } from 'aws-amplify/auth';
import { UserEducation, UserJob } from '../types/user';
import userStore from '../stores/userStore';

const API_URL = 'http://localhost:8080';

async function getAuthToken(): Promise<string> {
  const session = await fetchAuthSession();
  const authToken = session.tokens?.accessToken.toString();

  if (!authToken) {
    throw new Error('Authentication failed. Please log in again.');
  }

  return authToken;
}

export async function updateUser(userData: {
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

    const response = await fetch(`${API_URL}/users`, {
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

export async function deleteUserJob(jobId: string) {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL}/me/jobs/${jobId}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to delete job: ${response.status}`);
    }

    return { success: true };
  } catch (error) {
    console.error('Error deleting job:', error);
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Failed to delete job',
    };
  }
}

export async function deleteUserEducation(educationId: string) {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL}/me/education/${educationId}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to delete education: ${response.status}`);
    }

    return { success: true };
  } catch (error) {
    console.error('Error deleting education:', error);
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Failed to delete education',
    };
  }
}
