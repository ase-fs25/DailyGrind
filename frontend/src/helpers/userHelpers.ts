import { UserEducation, UserJob } from '../types/user';
import userStore from '../stores/userStore';
import { getAuthToken } from './authHelper';

const API_URL = 'http://localhost:8080';

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

    const response = await fetch(`${API_URL}/users/me`, {
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
      throw new Error(`Updating the user failed: ${response.status}`);
    }

    const updatedUser = await response.json();
    if (updatedUser) {
      userStore.setUser(updatedUser);
    }

    return { success: true };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Updating the user failed. Please try again.',
    };
  }
}

export async function addUserJob(job: UserJob) {
  try {
    const authToken = await getAuthToken();
    const response = await fetch(`${API_URL}/users/me/jobs`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify(job),
    });
    if (!response.ok) {
      throw new Error(`Failed to add job: ${response.status}`);
    }
    const newJob = await response.json();
    if (newJob) {
      userStore.setJobs([...userStore.getJobs(), newJob]);
    }
    return { success: true };
  } catch (error) {
    console.error('Error adding job:', error);
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Failed to add job',
    };
  }
}

export async function addUserEducation(education: UserEducation) {
  try {
    const authToken = await getAuthToken();
    const response = await fetch(`${API_URL}/users/me/education`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify(education),
    });

    if (!response.ok) {
      throw new Error(`Failed to add education: ${response.status}`);
    }

    const newEducation = await response.json();
    if (newEducation) {
      userStore.setEducation([...userStore.getEducation(), newEducation]);
    }

    return { success: true };
  } catch (error) {
    console.error('Error adding education:', error);
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Failed to add education',
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
