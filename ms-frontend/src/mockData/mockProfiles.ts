import { Profile } from '../types/profile';

export const mockProfiles: Profile[] = [
  {
    userId: 1,
    username: 'Alice',
    password: 'test1234',
    profileInfo: {
      profilePicture: 'https://via.placeholder.com/101',
      location: 'New York',
      education: 'Master of Arts',
      workExperience: 'Marketing Specialist',
    },
  },
  {
    userId: 2,
    username: 'Bob',
    password: 'test1234',
    profileInfo: {
      profilePicture: 'https://via.placeholder.com/102',
      location: 'San Francisco',
      education: 'PhD in Physics',
      workExperience: 'Research Scientist',
    },
  },
  {
    userId: 3,
    username: 'Charlie',
    password: 'test1234',
    profileInfo: {
      profilePicture: 'https://via.placeholder.com/103',
      location: 'Berlin',
      education: 'Bachelor of Engineering',
      workExperience: 'Mechanical Engineer',
    },
  },
  {
    userId: 4,
    username: 'Dana',
    password: 'test1234',
    profileInfo: {
      profilePicture: 'https://via.placeholder.com/104',
      location: 'Tokyo',
      education: 'Diploma in Graphic Design',
      workExperience: 'UX/UI Designer',
    },
  },
];
