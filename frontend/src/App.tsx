import { Routes, Route } from 'react-router-dom';

import './styles/app.css';

import { Authenticator } from '@aws-amplify/ui-react';
import '@aws-amplify/ui-react/styles.css';

import Feed from './components/screens/Feed';
import Posts from './components/screens/Posts';
import Friends from './components/screens/Friends';
import { Amplify } from 'aws-amplify';

// TODO: Move this to a config file
Amplify.configure({
  Auth: {
    Cognito: {
      userPoolClientId: 'qxjav04eh3y6x1vwrjkpvdypmw',
      userPoolId: 'us-east-1_3bb6832f013743d68e09a73fdf1cdf8a',
      userPoolEndpoint: 'http://localhost.localstack.cloud:4566/_aws/cognito-idp',
      loginWith: {
        oauth: {
          domain: 'http://localhost.localstack.cloud:4566/_aws/cognito-idp/login',
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
