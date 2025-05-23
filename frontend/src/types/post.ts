import { User } from './user';

export interface Post {
  postId: string;
  title: string;
  content: string;
  timestamp: string;
  commentCount: number;
  isLiked: boolean;
  isPinned: boolean;
  likeCount: number;
}

export interface Comment {
  commentId: string;
  userId: string;
  content: string;
  timestamp: string;
}

export interface CommentForBackend {
  content: string;
}

export interface FeedPost {
  post: Post;
  user: User;
}

export interface PostComments {
  comment: Comment;
  user: User;
}
