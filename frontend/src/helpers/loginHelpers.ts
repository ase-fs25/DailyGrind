import userStore from '../stores/userStore';
import { UserEducation, UserJob } from '../types/user';
import { getAuthToken } from './authHelper';

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

    // Create user Info
    const createUserInfo = await fetch('http://localhost:8080/users/me', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify({
        ...userData,
      }),
    });

    if (!createUserInfo.ok) {
      throw new Error(`Registration failed: ${createUserInfo.status}`);
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

export async function loginUser(userInfoRaw: string, authToken: string) {
  const userData = JSON.parse(userInfoRaw);

  userStore.setUser({
    userId: userData.userId,
    email: userData.email,
    firstName: userData.firstName,
    lastName: userData.lastName,
    birthday: userData.birthday,
    location: userData.location,
    jobs: userData.jobs || [],
    education: userData.education || [],
  });

  const userJobInfo = await fetch('http://localhost:8080/users/me/jobs', {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (userJobInfo.ok) {
    const userJobInfoRaw = await userJobInfo.text();
    if (userJobInfoRaw) {
      const userJobData = JSON.parse(userJobInfoRaw);
      userStore.setJobs(userJobData);
    }
  }
  const userEducationInfo = await fetch('http://localhost:8080/users/me/education', {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${authToken}`,
    },
  });
  if (userEducationInfo.ok) {
    const userEducationInfoRaw = await userEducationInfo.text();
    if (userEducationInfoRaw) {
      const userEducationData = JSON.parse(userEducationInfoRaw);
      userStore.setEducation(userEducationData);
    }
  }
}
