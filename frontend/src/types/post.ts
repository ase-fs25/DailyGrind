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
  content: string;
  timestamp: string;
}
