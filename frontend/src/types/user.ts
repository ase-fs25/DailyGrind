export interface User {
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
  birthday: string;
  location: string;
  jobs: UserJob[];
  education: UserEducation[];
}

export interface UserJob {
  jobId: string;
  startDate: string;
  endDate?: string;
  jobTitle: string;
  companyName: string;
  location: string;
  description: string;
}

export interface UserEducation {
  educationId: string;
  degree: string;
  institution: string;
  educationStartDate: string;
  educationEndDate?: string;
  fieldOfStudy: string;
  educationLocation: string;
  educationDescription: string;
}
