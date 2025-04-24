import { User, UserEducation, UserJob } from '../types/user';

class UserStore {
  private user: User = {
    userId: '',
    email: '',
    firstName: '',
    lastName: '',
    birthday: '',
    location: '',
    jobs: [],
    education: [],
  };

  /** Entire User **/
  setUser(user: User) {
    this.user = user;
  }

  getUser(): User {
    return this.user;
  }

  deleteUser(): void {
    this.user = {
      userId: '',
      email: '',
      firstName: '',
      lastName: '',
      birthday: '',
      location: '',
      jobs: [],
      education: [],
    };
  }

  /** Getters for Individual User Fields **/
  get userId(): string {
    return this.user.userId;
  }

  get email(): string {
    return this.user.email;
  }

  get firstName(): string {
    return this.user.firstName;
  }

  get lastName(): string {
    return this.user.lastName;
  }

  get birthday(): string {
    return this.user.birthday;
  }

  get location(): string {
    return this.user.location;
  }

  /** Setters for Individual User Fields **/
  setUserId(value: string) {
    this.user.userId = value;
  }

  setEmail(value: string) {
    this.user.email = value;
  }

  setFirstName(value: string) {
    this.user.firstName = value;
  }

  setLastName(value: string) {
    this.user.lastName = value;
  }

  setBirthday(value: string) {
    this.user.birthday = value;
  }

  setLocation(value: string) {
    this.user.location = value;
  }

  /** Job Fields **/
  getJobs(): UserJob[] {
    return this.user.jobs;
  }

  addJob(job: UserJob) {
    this.user.jobs.push(job);
  }

  updateJob(jobId: string, updatedJob: UserJob) {
    const index = this.user.jobs.findIndex((job) => job.jobId === jobId);
    if (index !== -1) {
      this.user.jobs[index] = updatedJob;
    }
  }

  removeJob(jobId: string) {
    this.user.jobs = this.user.jobs.filter((job) => job.jobId !== jobId);
  }

  /** Education Fields **/
  getEducation(): UserEducation[] {
    return this.user.education;
  }

  addEducation(education: UserEducation) {
    this.user.education.push(education);
  }

  updateEducation(educationId: string, updatedEducation: UserEducation) {
    const index = this.user.education.findIndex((edu) => edu.educationId === educationId);
    if (index !== -1) {
      this.user.education[index] = updatedEducation;
    }
  }

  removeEducation(educationId: string) {
    this.user.education = this.user.education.filter((edu) => edu.educationId !== educationId);
  }
}

const userStore = new UserStore();
export default userStore;
