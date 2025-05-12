# User Confirmation Lambda

This Lambda function is responsible for confirming user sign-ups in AWS Cognito. It is triggered by the Cognito User Pool during the sign-up process. This is necessary so that we can log in during the development process. AWS Cognit requires users to confirm their email address before they can log in. This Lambda function is used to automatically confirm users during the development process. This is useful for local development and testing.

# Technical Features

We are using pythin for this lambda function. The lambda function is triggered by the Cognito User Pool during the sign-up process.
