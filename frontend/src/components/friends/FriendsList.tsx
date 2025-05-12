import React, { useState, useEffect } from 'react';
import { Avatar, Box, Card, CardContent, Typography } from '@mui/material';
import { Post } from '../../types/post';
import FriendPopup from '../common/FriendPopup';
import { fetchFriends, getEducationByUserId, getJobsByUserId } from '../../helpers/friendsHelper';
import { getPinnedPostsByUserId } from '../../helpers/postHelper';
import { User, UserEducation, UserJob } from '../../types/user';
import '../../styles/components/friends/friendList.css';

const FriendsList = () => {
  const [friends, setFriends] = useState<User[]>([]);
  const [selectedProfile, setSelectedProfile] = useState<User | null>(null);
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

  const handleOpen = async (user: User) => {
    setSelectedProfile(user);
    setOpen(true);

    try {
      const [posts, jobs, education] = await Promise.all([
        getPinnedPostsByUserId(user.userId),
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
      {friends.length === 0 && (
        <Typography variant="h6" className="friend-username">
          Add some friends to see them here
        </Typography>
      )}
      <div className="friends-list">
        {friends.map((user) => (
          <Card key={user.userId} className="friend-card" onClick={() => handleOpen(user)}>
            <CardContent className="friend-card-content">
              <Avatar
                src={user.profilePictureUrl}
                alt={`${user.firstName} ${user.lastName}`}
                sx={{ width: 50, height: 50, boxShadow: '0 4px 8px rgba(0,0,0,0.1)' }}
              />
              <Typography variant="h6" className="friend-username">
                {user.firstName} {user.lastName}
              </Typography>
            </CardContent>
          </Card>
        ))}
      </div>

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
