import { Post } from '../types/post';

class PostsStore {
  private posts: Post[] = [];

  /** Posts array **/
  setPosts(posts: Post[]) {
    this.posts = posts;
  }

  getPosts(): Post[] {
    return this.posts;
  }

  clearPosts(): void {
    this.posts = [];
  }

  /** Individual Post **/
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
}

const postsStore = new PostsStore();
export default postsStore;
