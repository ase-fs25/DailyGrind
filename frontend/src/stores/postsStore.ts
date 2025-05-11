import { Post } from '../types/post';

class PostsStore {
  private posts: Post[] = [];
  private pinnedPosts: Post[] = [];
  private feedPosts: Post[] = [];

  /* Getters/Setters for all personal posts */
  setPosts(posts: Post[]) {
    this.posts = posts;
  }

  getPosts(): Post[] {
    return this.posts;
  }

  clearPosts(): void {
    this.posts = [];
  }

  addPost(post: Post) {
    this.posts.push(post);
  }

  updatePost(postId: string, updatedPost: Post) {
    const index = this.posts.findIndex((post) => post.postId === postId);
    if (index !== -1) {
      this.posts[index] = updatedPost;
    }
  }

  removePost(postId: string) {
    this.posts = this.posts.filter((post) => post.postId !== postId);
  }

  /** Getters for Individual Post Fields **/
  getPostById(postId: string): Post | undefined {
    return this.posts.find((post) => post.postId === postId);
  }

  /* Getters/Setters for pinned posts */
  getPinnedPosts(): Post[] {
    return this.pinnedPosts;
  }

  setPinnedPosts(pinnedPosts: Post[]) {
    this.pinnedPosts = pinnedPosts;
  }

  addPinnedPost(newPinnedPost: Post) {
    this.pinnedPosts.push(newPinnedPost);
  }

  removePinnedPost(postId: string) {
    this.pinnedPosts = this.pinnedPosts.filter((pinnedPosts) => pinnedPosts.postId !== postId);
  }

  clearPinnedPosts(): void {
    this.pinnedPosts = [];
  }

  /* Getters/Setters for feed posts */
  getFeedPosts(): Post[] {
    return this.feedPosts;
  }

  setFeedPosts(feedPosts: Post[]) {
    this.feedPosts = feedPosts;
  }

  clearFeedPosts(): void {
    this.feedPosts = [];
  }
}

const postsStore = new PostsStore();
export default postsStore;
