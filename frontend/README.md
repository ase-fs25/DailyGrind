# DailyGrind Frontend

This is the frontend application for the **DailyGrind** platform — a social productivity web app combining professional growth features with real-time daily check-ins.

Built with **React**, **TypeScript**, and **Vite**, the application is styled using **Material UI** and integrated with a backend microservices architecture via secure JWT-authenticated APIs. It also includes support for **Web Push notifications**.

## Tech Stack

- React with TypeScript
- Vite for fast builds and dev server
- Material UI (MUI) for component styling
- JWT-based authentication (OAuth2 Resource Server)
- Web Push Library (integrated with AWS Lambda + EventBridge backend)

## Technology Choices & Motivation

### React with TypeScript
We chose React with TypeScript for the following reasons:
- Type Safety: Brings static typing to React, catching errors at compile time and improving developer confidence.
- Developer Productivity: Autocomplete, refactoring tools, and clear interfaces speed up development.
- Maintainability: Strong typing makes large codebases easier to navigate and refactor.
- Ecosystem: Vast collection of type-aware libraries and community-maintained type definitions.
- Community Support: Large, active community continuously improving type definitions and best practices.
- Integration: Seamlessly works with modern React features (hooks, context, suspense).

### Vite
We chose Vite for the following reasons:
- Super-fast Cold Starts: Leverages native ES modules to avoid bundling on every change, yielding near-instant dev server startup.
- Hot Module Replacement (HMR): Blazing-fast updates during development without full page reloads.
- Out-of-the-box Optimization: Built-in support for code splitting, asset handling, and pre-bundling.
- Simplicity: Minimal configuration required to get started, yet highly extensible via plugins.
- Modern Features: Native support for TypeScript, JSX, CSS modules, and various asset types.
- Future-proof: Designed around the latest browser capabilities and standards.

### Material UI (MUI)
We chose Material UI (MUI) for the following reasons:
- Pre-built Components: Rich library of accessible, theme-ready React components out of the box.
- Theming & Customization: Deep theming API allows you to match your brand’s design language across all components.
- Responsiveness: Built-in grid system and style utilities adapt seamlessly to different screen sizes.
- Accessibility: Components follow WAI-ARIA guidelines, ensuring an inclusive experience.
- Developer Experience: Intuitive props API and consistent component patterns reduce boilerplate.
- Community & Ecosystem: Large community, extensive documentation, and a wealth of examples.
- Integration: Plays nicely with React Router, Formik, and other popular React libraries.

### JWT-based Authentication (OAuth2 Resource Server)
We chose JWT-based Authentication for the following reasons:
- Standards-compliant: Built on OAuth2 and JWT, widely adopted specifications that interoperate across platforms.
- Stateless Sessions: Tokens carry their own claims, eliminating the need for server-side session storage and simplifying horizontal scaling.
- Performance: No database lookup required on each request—authentication is as simple as signature verification.
- Security: Supports secure algorithms (e.g., RS256), token expiration, and revocation strategies.
- Decoupling: Resource servers only need the public key to verify tokens, keeping authentication logic centralized.

### Web Push Library (integrated with AWS Lambda + EventBridge backend)
We chose Web Push Library for the following reasons:
- Real-time Engagement: Enables push notifications directly to the browser or device, driving user engagement.
- Serverless Scalability: AWS Lambda handlers scale automatically to match event volume without provisioning servers.
- Event-driven Architecture: EventBridge decouples producers and consumers, allowing other microservices to subscribe to the same events in the future.
- Cost-Efficiency: Pay-per-invoke model of Lambda and EventBridge means you only pay for actual usage.
- Fault Tolerance: Automatic retries and dead-letter queues ensure reliable delivery even under failure.
- Security: Fine-grained IAM permissions lock down who can publish or consume events.
- Extensibility: New notification channels (e.g., SMS, email) can be added by wiring additional EventBridge targets.

## Running the frontend locally

### Set ENV variables

Copy the env.template in your root folder and name it .env
Then insert the values for the following keys:

```bash
VITE_USER_POOL_CLIENT_ID=
VITE_USER_POOL_ID=
VITE_USER_POOL_ENDPOINT=
VITE_DOMAIN=
VITE_API_URL=
VITE_VAPID_PUBLIC_KEY=
```

Get the first two from your terraform container (should be printed to console after start).
The third one is either `http://localhost:4566/_aws/cognito-idp` or `http://localhost.localstack.cloud:4566/_aws/cognito-idp` and the fourth one is
either `http://localhost:4566/_aws/cognito-idp/login` or `http://localhost.localstack.cloud:4566/_aws/cognito-idp/login`.

Try out the third and fourth one. We haven't figured out why it is different for some machines.

### Running the frontend

Make sure that the backend is running properly, as well as the containers. For this, refer to the `README.md` files in the microservices folders and the terraform folder.

Install dependencies:

`npm install`

Start the the frontend application:

`npm run dev`

This runs the app in the development mode. Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits. You will also see any lint errors in the console.

### Format code

Run `npm run prettier` to format the code correctly.
