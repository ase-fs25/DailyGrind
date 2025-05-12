import { getAuthToken } from './authHelper';
import { Post, PostComments } from '../types/post';
import moment from 'moment';
import { POSTING_TIME } from '../constants/postTime';
import { getApiUrl } from './apiHelper';

export async function getUserPosts(): Promise<Post[]> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(getApiUrl(`users/me/posts`), {
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

    const response = await fetch(getApiUrl(`posts`), {
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
    return createdPost;
  } catch (error) {
    console.error('Error creating post:', error);
    throw error;
  }
}

export async function deletePost(postId: string): Promise<void> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(getApiUrl(`posts/${postId}`), {
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

export async function userHasPostedAlready(): Promise<boolean> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(getApiUrl(`users/me/daily-post`), {
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (response.ok) {
      try {
        const data = await response.json();
        return !!data && !!data.postId;
      } catch {
        console.log('User has not posted yet!');
      }
    }

    return false;
  } catch (error) {
    console.error('Error checking if user has posted already:', error);
    return false;
  }
}

export function validPostingTime(currentTime: moment.Moment): boolean {
  const { startTime, endTime } = getPostingTimeRange(currentTime);
  return currentTime.isBetween(startTime, endTime, null, '[]');
}

export async function getPinnedPostsByUserId(userId: string): Promise<Post[]> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(getApiUrl(`users/${userId}/pinned-posts`), {
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to fetch posts for user ${userId}: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error(`Error fetching posts for user ${userId}:`, error);
    return [];
  }
}

export async function getUserPinnedPosts(): Promise<Post[]> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(getApiUrl(`users/me/pinned-posts`), {
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to fetch posts: ${response.status}`);
    }

    const pinnedPosts = await response.json();
    return pinnedPosts;
  } catch (error) {
    console.error('Error fetching users pinned posts:', error);
    throw error;
  }
}

export async function pinPost(postId: string): Promise<void> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(getApiUrl(`users/me/pinned-posts/${postId}`), {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to pin post: ${response.status}`);
    }
  } catch (error) {
    console.error('Error pinning post:', error);
    throw error;
  }
}

export async function unpinPost(postId: string): Promise<void> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(getApiUrl(`users/me/pinned-posts/${postId}`), {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to unpin post: ${response.status}`);
    }
  } catch (error) {
    console.error('Error unpinning post:', error);
    throw error;
  }
}

export async function likePost(postId: string): Promise<void> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL}/posts/${postId}/likes`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to like post: ${response.status}`);
    }
  } catch (error) {
    console.error('Error liking post:', error);
    throw error;
  }
}

export async function unlikePost(postId: string): Promise<void> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL}/posts/${postId}/likes`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to like post: ${response.status}`);
    }
  } catch (error) {
    console.error('Error liking post:', error);
    throw error;
  }
}

export async function getCommentsForPost(postId: string): Promise<PostComments[]> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL}/posts/${postId}/comments`, {
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to fetch comments: ${response.status}`);
    }

    const commentsForPost = await response.json();
    return commentsForPost;
  } catch (error) {
    console.error('Error fetching comments:', error);
    throw error;
  }
}

export async function addCommentForPost(postId: string, newComment: string): Promise<void> {
  try {
    const authToken = await getAuthToken();

    const postRequest = {
      content: newComment,
    };

    const response = await fetch(`${API_URL}/posts/${postId}/comments`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify(postRequest),
    });

    if (!response.ok) {
      throw new Error(`Failed to add comment: ${response.status}`);
    }
  } catch (error) {
    console.error('Error adding comment:', error);
    throw error;
  }
}

export async function deleteCommentForPost(postId: string, commentId: string): Promise<void> {
  try {
    const authToken = await getAuthToken();

    const response = await fetch(`${API_URL}/posts/${postId}/comments/${commentId}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to delete comment: ${response.status}`);
    }
  } catch (error) {
    console.error('Error deleting comment:', error);
    throw error;
  }
}

export const formatDate = (timestamp: string) => {
  const ts = Number(timestamp);
  const ms = timestamp.length === 10 ? ts * 1000 : ts;

  const date = new Date(ms);
  if (isNaN(date.getTime())) {
    return 'Invalid date';
  }
  return `${date.getDate().toString().padStart(2, '0')}-${(date.getMonth() + 1)
    .toString()
    .padStart(2, '0')}-${date.getFullYear()}`;
};
