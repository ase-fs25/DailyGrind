# DailyGrind Frontend

This is the frontend application for the **DailyGrind** platform — a social productivity web app combining professional growth features with real-time daily check-ins.

Built with **React**, **TypeScript**, and **Vite**, the application is styled using **Material UI** and integrated with a backend microservices architecture via secure JWT-authenticated APIs. It also includes support for **Progressive Web App (PWA)** functionality and **Web Push notifications**.

---

## Tech Stack

- React with TypeScript
- Vite for fast builds and dev server
- Material UI (MUI) for component styling
- JWT-based authentication (OAuth2 Resource Server)
- Web Push API (integrated with AWS Lambda + EventBridge backend)
- PWA support via custom `service-worker.js`
- Dockerized deployment using Nginx

---


# Getting Started

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

### Set ENV variables

Copy the env.template in your root folder and name it .env
Then insert the values for the following keys:

VITE_USER_POOL_CLIENT_ID=
VITE_USER_POOL_ID=
VITE_USER_POOL_ENDPOINT=
VITE_DOMAIN=

Get the first two from your terraform container (should be printed to console after start).
The third one is either `http://localhost:4566/_aws/cognito-idp` or `http://localhost.localstack.cloud:4566/_aws/cognito-idp` and the fourth one is
either `http://localhost:4566/_aws/cognito-idp/login` or `http://localhost.localstack.cloud:4566/_aws/cognito-idp/login`.

Try out the third and fourth one. We haven't figured out why it is different for some machines.

### Start the frontend

Make sure that the backend is running properly, as well as the containers

First: `npm install`

Then: `npm run dev`

This runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.\
You will also see any lint errors in the console.

Run `npm run prettier` to format the code correctly.
