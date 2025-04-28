import { getAuthToken } from './authHelper';

const API_URL = 'http://localhost:8080'; // user-service

export interface UserProfile {
  userId: string;
  firstName: string;
  lastName: string;
  // add other fields if needed (email, location, etc.)
}

export async function searchUsers(name: string): Promise<UserProfile[]> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL}/users/search?name=${encodeURIComponent(name)}`, {
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to search users: ${response.status}`);
    }

    const users = await response.json();
    return users;
  } catch (error) {
    console.error('Error searching users:', error);
    throw error;
  }
}

export async function sendFriendRequest(targetUserId: string): Promise<void> {
  const API_URL_2 = 'http://localhost:8080/users'; // fine

  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL_2}/requests?targetUserId=${encodeURIComponent(targetUserId)}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to send friend request: ${response.status}`);
    }
  } catch (error) {
    console.error('Error sending friend request:', error);
    throw error;
  }
}

export async function fetchIncomingRequests() {
  const API_URL_2 = 'http://localhost:8080/users';
  const authToken = await getAuthToken();
  const response = await fetch(`${API_URL_2}/requests/incoming`, {
    headers: { Authorization: `Bearer ${authToken}` },
  });
  if (!response.ok) {
    throw new Error('Failed to fetch incoming friend requests.');
  }
  return response.json(); // returns a list of users
}

export async function acceptFriendRequest(requestId: string) {
  const API_URL_2 = 'http://localhost:8080/users';
  const authToken = await getAuthToken();
  const response = await fetch(`${API_URL_2}/requests/${requestId}/accept`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${authToken}` },
  });
  if (!response.ok) {
    throw new Error('Failed to accept friend request.');
  }
}

export async function declineFriendRequest(requestId: string) {
  const API_URL_2 = 'http://localhost:8080/users';
  const authToken = await getAuthToken();
  const response = await fetch(`${API_URL_2}/requests/${requestId}/decline`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${authToken}` },
  });
  if (!response.ok) {
    throw new Error('Failed to decline friend request.');
  }
}
