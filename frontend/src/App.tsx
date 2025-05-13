import { useEffect, useState } from 'react';
import { Route, Routes, useNavigate } from 'react-router-dom';
import { Authenticator, useAuthenticator } from '@aws-amplify/ui-react';
import { Amplify } from 'aws-amplify';
import '@aws-amplify/ui-react/styles.css';

import Feed from './components/screens/Feed';
import Posts from './components/screens/Posts';
import Friends from './components/screens/Friends';
import Registration from './components/login/Registration';

import { loginUser } from './helpers/loginHelpers';
import { getAuthToken } from './helpers/authHelper';
import { registerUserForSubscription } from './helpers/pushNotificationHelpers';
import userStore from './stores/userStore';
import { getApiUrl } from './helpers/apiHelper';

// Configure Amplify
Amplify.configure({
  Auth: {
    Cognito: {
      userPoolClientId: import.meta.env.VITE_USER_POOL_CLIENT_ID ?? '',
      userPoolId: import.meta.env.VITE_USER_POOL_ID ?? '',
      userPoolEndpoint: import.meta.env.VITE_USER_POOL_ENDPOINT,
      loginWith: {
        oauth: {
          domain: import.meta.env.VITE_DOMAIN ?? '',
          redirectSignIn: ['http://localhost:3000/'],
          redirectSignOut: ['http://localhost:3000/'],
          responseType: 'code',
          scopes: ['email', 'openid', 'profile'],
        },
        username: false,
        email: true,
        phone: false,
      },
      signUpVerificationMethod: 'code',
      passwordFormat: {
        minLength: 8,
        requireNumbers: true,
        requireLowercase: true,
        requireUppercase: true,
        requireSpecialCharacters: true,
      },
    },
  },
});

function AppContent() {
  const { user, authStatus } = useAuthenticator((context) => [context.user, context.authStatus]);
  const [appReady, setAppReady] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const initializeUser = async () => {
      if (authStatus === 'authenticated' && user && !userStore.getUser().userId) {
        try {
          const authToken = await getAuthToken();
          const response = await fetch(getApiUrl('users/me'), {
            headers: {
              Authorization: `Bearer ${authToken}`,
            },
          });

          if (response.ok && authToken) {
            const userInfoRaw = await response.text();

            await registerUserForSubscription();

            if (userInfoRaw && userInfoRaw !== '[]') {
              loginUser(userInfoRaw, authToken);
              navigate('/', { replace: true });
            } else {
              navigate('/registration', { replace: true });
            }
          }
        } catch (error) {
          console.error('User bootstrap failed', error);
        } finally {
          setAppReady(true);
        }
      }
    };

    initializeUser();
  }, [authStatus, user, navigate]);

  if (authStatus !== 'authenticated' || !appReady) {
    return <div />; // Can show a spinner or loading indicator here
  }

  return (
    <Routes>
      <Route path="/" element={<Feed />} />
      <Route path="/posts" element={<Posts />} />
      <Route path="/friends" element={<Friends />} />
      <Route path="/registration" element={<Registration />} />
    </Routes>
  );
}

function App() {
  return (
    <Authenticator>
      <AppContent />
    </Authenticator>
  );
}

export default App;
