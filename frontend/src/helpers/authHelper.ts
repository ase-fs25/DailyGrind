import { fetchAuthSession, fetchUserAttributes } from 'aws-amplify/auth';

export async function getAuthToken(): Promise<string> {
  const session = await fetchAuthSession();
  const authToken = session.tokens?.accessToken.toString();

  if (!authToken) {
    throw new Error('Authentication failed. Please log in again.');
  }

  return authToken;
}

export async function getUserEmail(): Promise<string> {
  const session = await fetchUserAttributes();
  const userEmail = session.email;
  if (!userEmail) {
    throw new Error('User email not found. Please log in again.');
  }

  return userEmail;
}
