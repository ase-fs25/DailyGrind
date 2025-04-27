import { getAuthToken } from './authHelper';
import { Post } from '../types/post';
import moment from 'moment';
import { POSTING_TIME } from '../constants/postTime';

const API_URL = 'http://localhost:8081';

export async function getUserPosts(): Promise<Post[]> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL}/me/posts`, {
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to fetch posts: ${response.status}`);
    }

    const posts = await response.json();
    return posts;
  } catch (error) {
    console.error('Error fetching user posts:', error);
    throw error;
  }
}

export async function createPost(title: string, content: string): Promise<Post> {
  try {
    const authToken = await getAuthToken();

    const postRequest = {
      title: title,
      content: content,
    };

    console.log('Sending post request:', postRequest);

    const response = await fetch(`${API_URL}/posts`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify(postRequest),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('Server error response:', errorText);
      throw new Error(`Failed to create post: ${response.status}`);
    }

    const createdPost = await response.json();
    console.log('Created post response:', createdPost);
    return createdPost;
  } catch (error) {
    console.error('Error creating post:', error);
    throw error;
  }
}

export async function deletePost(postId: string): Promise<void> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL}/posts/${postId}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to delete post: ${response.status}`);
    }
  } catch (error) {
    console.error('Error deleting post:', error);
    throw error;
  }
}

function getPostingTimeRange(currentTime: moment.Moment) {
  const startTime = moment(currentTime).set({
    hour: POSTING_TIME.POST_TIME_START_HOUR,
    minute: POSTING_TIME.POST_TIME_START_MINUTES,
    second: 0,
    millisecond: 0,
  });

  const endTime = moment(currentTime).set({
    hour: POSTING_TIME.POST_TIME_END_HOUR,
    minute: POSTING_TIME.POST_TIME_END_MINUTES,
    second: 0,
    millisecond: 0,
  });

  return { startTime, endTime };
}

export function userHasPostedAlready(posts: Post[]): boolean {
  const today = moment().startOf('day');

  const todayStartTime = moment(today).add({
    hours: POSTING_TIME.POST_TIME_START_HOUR,
    minutes: POSTING_TIME.POST_TIME_START_MINUTES,
  });

  const todayEndTime = moment(today).add({
    hours: POSTING_TIME.POST_TIME_END_HOUR,
    minutes: POSTING_TIME.POST_TIME_END_MINUTES,
  });

  return posts.some((post) => {
    const postTime = moment(post.timestamp);
    return postTime.isBetween(todayStartTime, todayEndTime, null, '[]');
  });
}

export function validPostingTime(currentTime: moment.Moment): boolean {
  const { startTime, endTime } = getPostingTimeRange(currentTime);
  return currentTime.isBetween(startTime, endTime, null, '[]');
}
