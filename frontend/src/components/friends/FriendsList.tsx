import React, { useState, useEffect } from 'react';
import { Box, Card, CardContent, Typography } from '@mui/material';
import { Post } from '../../types/post';
import FriendPopup from '../common/FriendPopup';
import { fetchFriends, getEducationByUserId, getJobsByUserId, UserProfile } from '../../helpers/friendsHelper';
import { getPostsByUserId } from '../../helpers/postHelper';
import { UserEducation, UserJob } from '../../types/user';
import '../../styles/components/friends/friendList.css';

const FriendsList = () => {
  const [friends, setFriends] = useState<UserProfile[]>([]);
  const [selectedProfile, setSelectedProfile] = useState<UserProfile | null>(null);
  const [userPosts, setUserPosts] = useState<Post[]>([]);
  const [userJobs, setUserJobs] = useState<UserJob[]>([]);
  const [userEducation, setUserEducation] = useState<UserEducation[]>([]);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    const loadFriends = async () => {
      try {
        const data = await fetchFriends();
        setFriends(data);
      } catch (error) {
        console.error('Failed to fetch friends:', error);
      }
    };

    loadFriends();
  }, []);

  const handleOpen = async (user: UserProfile) => {
    setSelectedProfile(user);
    setOpen(true);

    try {
      const [posts, jobs, education] = await Promise.all([
        getPostsByUserId(user.userId),
        getJobsByUserId(user.userId),
        getEducationByUserId(user.userId),
      ]);

      setUserPosts(posts);
      setUserJobs(jobs);
      setUserEducation(education);
    } catch (error) {
      console.error(`Failed to load full profile for user ${user.userId}:`, error);
      setUserPosts([]);
      setUserJobs([]);
      setUserEducation([]);
    }
  };

  const handleClose = () => {
    setSelectedProfile(null);
    setUserPosts([]);
    setUserJobs([]);
    setUserEducation([]);
    setOpen(false);
  };

  return (
    <Box className="friends-list-container">
      {friends.map((user) => (
        <Card key={user.userId} className="friend-card" onClick={() => handleOpen(user)}>
          <CardContent className="friend-card-content">
            <Typography variant="h6" className="friend-username">
              {user.firstName} {user.lastName}
            </Typography>
          </CardContent>
        </Card>
      ))}

      {selectedProfile && (
        <FriendPopup
          open={open}
          onClose={handleClose}
          user={selectedProfile}
          posts={userPosts}
          jobs={userJobs}
          education={userEducation}
        />
      )}
    </Box>
  );
};

export default FriendsList;
