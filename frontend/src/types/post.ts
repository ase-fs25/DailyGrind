export interface Post {
  postId: string;
  title: string;
  content: string;
  timestamp: string;
}

export interface Comment {
  commentId: string;
  content: string;
  timestamp: string;
}
