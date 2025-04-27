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
  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL}/followers/request`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify({
        followingId: targetUserId, // 👈 sending friend request to them
      }),
    });

    if (!response.ok) {
      throw new Error(`Failed to send friend request: ${response.status}`);
    }
  } catch (error) {
    console.error('Error sending friend request:', error);
    throw error;
  }
}
