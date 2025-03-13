# LocalStack Pro Setup Guide with Docker Compose

This guide provides step-by-step instructions for setting up **LocalStack Pro** using Docker Compose in your local
development environment. LocalStack allows you to emulate AWS services locally for testing and development.

## Prerequisites

Ensure that the following are installed on your machine:

- **Docker**: A platform for developing, shipping, and running applications in containers.
- **Docker Compose**: A tool for defining and running multi-container Docker applications.
- **AWS CLI**: The AWS Command Line Interface for interacting with AWS services.
- **LocalStack Pro License**: An API key obtained by subscribing to a LocalStack Pro plan.

## 1. Obtain a LocalStack Pro License

To access advanced features like service persistence and the HTTPS Gateway, subscribe to a LocalStack Pro plan and
retrieve your API key:

- Hobby License should be sufficient

## 2. Install Prerequisites

### Docker and Docker Compose

- **macOS**:
    - Install [Homebrew](https://brew.sh/).
    - Install Docker and Docker Compose:
```bash
      brew install --cask docker
      brew install docker-compose
```
- Start Docker Desktop from your Applications folder.

- **Windows**:
    - Download and install [Docker Desktop](https://www.docker.com/products/docker-desktop).
    - Docker Compose is included with Docker Desktop.

- **Linux**:
    - Follow the [Docker installation guide](https://docs.docker.com/engine/install/) for your distribution.
    - Install Docker Compose:
```bash
      sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
      sudo chmod +x /usr/local/bin/docker-compose
```

### AWS CLI

- **macOS/Linux**:
```bash
  brew install awscli
```
- **Windows**:
    - Download and install the [AWS CLI](https://aws.amazon.com/cli/).

### Terraform

- **macOS**:
```bash
  brew install terraform
```

- **Windows**:
    - Download the [Terraform binary](https://www.terraform.io/downloads.html) and add it to your PATH.

- **Linux**:
```bash
  sudo apt-get update
  sudo apt-get install terraform
```

- After installing Terraform, verify the installation by running:
```bash
  terraform --version
```

### Terraform Local

- Download Terraform Local via pip:
```bash
  pip install terraform-local
```

## 3. Clone the Repository

Clone the repository that contains the `docker-compose.yml` and `terraform` configuration files.
```bash
   git clone https://your-repository-url.git
   cd your-repository
  ```

## 4. Configure AWS CLI

You will need to configure the AWS CLI with dummy credentials since LocalStack emulates AWS services locally. Run the
following command:
```bash
  aws configure
```

Provide the following values when prompted:

- AWS Access Key ID: test
- AWS Secret Access Key: test
- Default region name: us-east-1
- Default output format: json

This configuration allows you to interact with LocalStack, which mimics AWS locally.

## 5. Set Up LocalStack with Docker Compose

### Set the Authentication Token

You must set the LocalStack Pro authentication token in your environment. The token is provided when you subscribe to
LocalStack Pro.

Set the LOCALSTACK_AUTH_TOKEN environment variable with your Pro license token (you can copy the token from the
LocalStack dashboard):

- macOS/Linux

```bash
    export LOCALSTACK_AUTH_TOKEN="your_auth_token"
```

- Windows (Command Prompt)

```bash
    set LOCALSTACK_AUTH_TOKEN=your_auth_token
```

- Windows (PowerShell)

```bash
    $env:LOCALSTACK_AUTH_TOKEN="your_auth_token"
```

### Run Docker Compose

With the docker-compose.yml and your authentication token set, you can start LocalStack Pro by running:

```bash
    docker-compose up -d
```

```bash
    docker-compose up -d
```

This will download and start LocalStack Pro, including all services specified in your docker-compose.yml.

## 6. Verify LocalStack Pro

Once LocalStack is running, you can verify that the services are working:

- Check the status of the containers:

```bash
    docker ps
```

This should show the localstack container running.

### Access the LocalStack Web UI:

open your browser and navigate to http://localhost:8080. This is the LocalStack Web Application that provides an
interface to monitor and manage emulated AWS services.

## 7. Troubleshooting

- **API Not Implemented Error**: If you encounter errors like "API for service 'cognito-idp' not yet implemented,"
  ensure you are using the LocalStack Pro image (`localstack/localstack-pro`) in your `docker-compose.yml` and that your
  authentication token is correctly set.

- **Command Not Found**: If you receive a "command not found" error for `aws`, ensure that the AWS CLI is installed
  correctly. You can install it using Homebrew on macOS:
  ```bash
      brew install awscli
  ```
  
## Create a test user

### Via the LocalStack Web UI

- Navigate to the Cognito service in the LocalStack Web UI.
- Create a test user inside the User Pool.

### Via the AWSLocal CLI

#### Get user pool id

```bash
  awslocal cognito-idp list-user-pools --max-results 10 --region us-east-1
```

#### Get user pool client id

```bash
  awslocal cognito-idp list-user-pool-clients --user-pool-id your_user_pool_id --region us-east-1
```

#### Create a user

```bash
  awslocal cognito-idp sign-up --client-id your_client_id --username testuser --password Testuser@123 --region us-east-1
```

#### Confirm the user

```bash
  awslocal cognito-idp admin-confirm-sign-up --user-pool-id your_user_pool_id --username testuser --region us-east-1
```

#### Check all users in the user pool

```bash
  awslocal cognito-idp list-users --user-pool-id your_user_pool_id --region us-east-1
```

#### Get the user's access token

- via cli
```bash
  awslocal cognito-idp initiate-auth --auth-flow USER_PASSWORD_AUTH --auth-parameters USERNAME=testuser,PASSWORD=Testuser@123 --client-id your_client_id --region us-east-1
```

- via curl
```bash
  curl 'http://localhost:4566/<pool_id>/.well-known/jwks.json'
  {"keys": [{"kty": "RSA", "alg": "RS256", "use": "sig", "kid": "test-key", "n": "k6lrbEH..."]}
```
