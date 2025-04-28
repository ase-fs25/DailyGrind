import { fetchAuthSession } from 'aws-amplify/auth';

export async function getAuthToken(): Promise<string> {
  const session = await fetchAuthSession();
  const authToken = session.tokens?.accessToken.toString();

  if (!authToken) {
    throw new Error('Authentication failed. Please log in again.');
  }

  return authToken;
}
