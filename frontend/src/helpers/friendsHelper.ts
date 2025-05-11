import { getAuthToken } from './authHelper';
import { User, UserEducation, UserJob } from '../types/user';

const API_URL = 'http://localhost:8080';

export interface UserProfile {
  userId: string;
  firstName: string;
  lastName: string;
  email?: string;
  location?: string;
  requestId?: string;
  hasPendingRequest?: boolean;
  isAlreadyFriend?: boolean;
  education?: UserEducation[];
  jobs?: UserJob[];
}

export interface FriendRequest {
  requestId: string;
  senderId: string;
  firstName: string;
  lastName: string;
}

// --- Search Users ---
export async function searchUsers(name: string): Promise<UserProfile[]> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/users/search?name=${encodeURIComponent(name)}`, {
    headers: { Authorization: `Bearer ${authToken}` },
  });

  if (!response.ok) throw new Error(`Failed to search users: ${response.status}`);
  return await response.json();
}

// --- Send Friend Request ---
export async function sendFriendRequest(targetUserId: string): Promise<void> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/users/requests?targetUserId=${encodeURIComponent(targetUserId)}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!response.ok) throw new Error(`Failed to send friend request: ${response.status}`);
}

// --- Fetch Friend List ---
export async function fetchFriends(): Promise<UserProfile[]> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/users/me/friends`, {
    headers: { Authorization: `Bearer ${authToken}` },
  });

  if (!response.ok) throw new Error('Failed to fetch friends.');
  return await response.json();
}

// --- Fetch Incoming Friend Requests ---
export async function fetchIncomingRequests(): Promise<User[]> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/users/requests/incoming`, {
    headers: { Authorization: `Bearer ${authToken}` },
  });

  if (!response.ok) throw new Error('Failed to fetch incoming friend requests.');
  return await response.json();
}

// --- Fetch Outgoing Friend Requests ---
export async function fetchOutgoingRequests(): Promise<UserProfile[]> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/users/requests/outgoing`, {
    headers: { Authorization: `Bearer ${authToken}` },
  });

  if (!response.ok) throw new Error('Failed to fetch outgoing friend requests.');
  return await response.json();
}

// --- Accept Friend Request ---
export async function acceptFriendRequest(requestSenderId: string): Promise<void> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/users/requests/${requestSenderId}/accept`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${authToken}` },
  });

  if (!response.ok) throw new Error('Failed to accept friend request.');
}

// --- Decline Friend Request ---
export async function declineFriendRequest(requestSenderId: string): Promise<void> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/users/requests/${requestSenderId}/decline`, {
    method: 'DELETE',
    headers: { Authorization: `Bearer ${authToken}` },
  });

  if (!response.ok) throw new Error('Failed to decline friend request.');
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

// --- Remove Friend ---
export async function removeFriend(friendId: string): Promise<void> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/users/me/friends/${friendId}/remove`, {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!response.ok) throw new Error('Failed to remove friend.');
}

// --- Fetch Jobs by User ID ---
export async function getJobsByUserId(userId: string): Promise<UserJob[]> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/users/${userId}/jobs`, {
    headers: { Authorization: `Bearer ${authToken}` },
  });

  if (!response.ok) throw new Error('Failed to fetch jobs for user.');
  return await response.json();
}

// --- Fetch Education by User ID ---
export async function getEducationByUserId(userId: string): Promise<UserEducation[]> {
  const authToken = await getAuthToken();

  const response = await fetch(`${API_URL}/users/${userId}/education`, {
    headers: { Authorization: `Bearer ${authToken}` },
  });

  if (!response.ok) throw new Error('Failed to fetch education for user.');
  return await response.json();
}
