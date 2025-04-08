import { Routes, Route } from 'react-router-dom';
import { Authenticator } from '@aws-amplify/ui-react';
import { Amplify } from 'aws-amplify';

import './styles/app.css';
import '@aws-amplify/ui-react/styles.css';

import Feed from './components/screens/Feed';
import Posts from './components/screens/Posts';
import Friends from './components/screens/Friends';

Amplify.configure({
  Auth: {
    Cognito: {
      userPoolClientId: import.meta.env.VITE_USER_POOL_CLIENT_ID ? import.meta.env.VITE_USER_POOL_CLIENT_ID : '',
      userPoolId: import.meta.env.VITE_USER_POOL_ID ? import.meta.env.VITE_USER_POOL_ID : '',
      userPoolEndpoint: import.meta.env.VITE_USER_POOL_ENDPOINT,
      loginWith: {
        oauth: {
          domain: import.meta.env.VITE_DOMAIN ? import.meta.env.VITE_DOMAIN : '',
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

function App() {
  return (
    <Authenticator>
      <div className="app">
        <Routes>
          <Route path="/" element={<Feed />} />
          <Route path="/posts" element={<Posts />} />
          <Route path="/friends" element={<Friends />} />
        </Routes>
      </div>
    </Authenticator>
  );
}

export default App;
