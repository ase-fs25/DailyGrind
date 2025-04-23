# Getting Started with Create React App

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Available Scripts

In the project directory, you can run:

### `npm install`

Do this first to install all the necessary dependencies.

### Set ENV variables

Copy the env.template in your root folder and name it .env
Then insert the values for the following keys:

VITE_USER_POOL_CLIENT_ID=
VITE_USER_POOL_ID=
VITE_USER_POOL_ENDPOINT=
VITE_DOMAIN=

Get the first two from your terraform container (should be printed to console after start).
The third one is either http://localhost:4566/aws/cognito-idp or http://localhost.localstack.cloud:4566/_aws/cognito-idp and the fourth one is either http://localhost:4566/aws/cognito-idp/login or http://localhost.localstack.cloud:4566/_aws/cognito-idp/login.

Try out the third and fourth one. We haven't figured out why it is different for some machines.

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.\
You will also see any lint errors in the console.

### TODO:

- Connect to backend
- Fully build the different pages
- Create login/registration flow (also with use profile in settings)
- Change Logo
- Change Color scheme?
