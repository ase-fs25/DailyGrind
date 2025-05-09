import { getAuthToken } from './authHelper';

const API_URL = 'http://localhost:8080/users'; // base path for user-service

export interface UserProfile {
  userId: string;
  firstName: string;
  lastName: string;
  email?: string;
  location?: string;
  requestId?: string; // used for accepting/declining
  hasPendingRequest?: boolean;
  isAlreadyFriend?: boolean;
}

export interface FriendRequest {
  requestId: string; // SK from DynamoDB
  senderId: string;
  firstName: string;
  lastName: string;
}

// --- Search Users ---
export async function searchUsers(name: string): Promise<UserProfile[]> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL}/search?name=${encodeURIComponent(name)}`, {
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to search users: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error searching users:', error);
    throw error;
  }
}

// --- Send Friend Request ---
export async function sendFriendRequest(targetUserId: string): Promise<void> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL}/requests?targetUserId=${encodeURIComponent(targetUserId)}`, {
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

// --- Fetch Friend List ---
export async function fetchFriends(): Promise<UserProfile[]> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/friends`, {
    headers: { Authorization: `Bearer ${authToken}` },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch friends.');
  }

  return response.json();
}

// --- Fetch Incoming Friend Requests ---
export async function fetchIncomingRequests(): Promise<FriendRequest[]> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/requests/incoming`, {
    headers: { Authorization: `Bearer ${authToken}` },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch incoming friend requests.');
  }

  return response.json();
}
export async function fetchOutgoingRequests(): Promise<UserProfile[]> {
  const authToken = await getAuthToken();

  const response = await fetch(`http://localhost:8080/users/requests/outgoing`, {
    headers: {
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch outgoing friend requests.');
  }

  return response.json();
}

// --- Accept Friend Request ---
export async function acceptFriendRequest(requestId: string): Promise<void> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/requests/${requestId}/accept`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${authToken}` },
  });

  if (!response.ok) {
    throw new Error('Failed to accept friend request.');
  }
}

// --- Decline Friend Request ---
export async function declineFriendRequest(requestId: string): Promise<void> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/requests/${requestId}/decline`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${authToken}` },
  });

  if (!response.ok) {
    throw new Error('Failed to decline friend request.');
  }
}

// --- Check if Friend Request Already Exists ---
export async function checkExistingFriendRequest(targetUserId: string): Promise<boolean> {
  try {
    const friends = await fetchFriends();
    return friends.some((friend) => friend.userId === targetUserId);
  } catch (err) {
    console.error('Failed to check existing friend request:', err);
    return false;
  }
}
export async function removeFriend(friendId: string): Promise<void> {
  const authToken = await getAuthToken();

  const response = await fetch(`http://localhost:8080/users/friends/${friendId}/remove`, {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!response.ok) {
    throw new Error('Failed to remove friend.');
  }
}
