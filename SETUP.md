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

- Easiest Way: Make a custom run config in InteliJ where you set the environment variable `LOCALSTACK_AUTH_TOKEN` to
  your LocalStack Pro license key.

## 6. Verify LocalStack Pro

Once LocalStack is running, you can verify that the services are working:

- Check the status of the containers:

```bash
    docker ps
```

This should show the localstack container running.

### Access the LocalStack Web UI:

Go to the LocalStack at [Web UI](https://app.localstack.cloud/dashboard) and log in

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

Connect to you localstack container:

```bash
  docker exec -it your_container_id sh
```

#### Get user pool id

```bash
  awslocal cognito-idp list-user-pools --max-results 10 --region us-east-1
```

#### Get user pool client id

```bash
  awslocal cognito-idp list-user-pool-clients --region us-east-1 --user-pool-id your_user_pool_id
```

#### Create a user

```bash
  awslocal cognito-idp sign-up --username testuser@gmail.com --password Testuser@123 --region us-east-1 --client-id your_client_id
```

#### Confirm the user

```bash
  awslocal cognito-idp admin-confirm-sign-up --username testuser@gmail.com --region us-east-1 --user-pool-id your_user_pool_id
```

#### Check all users in the user pool

```bash
  awslocal cognito-idp list-users --region us-east-1 --user-pool-id your_user_pool_id
```

#### Get the user's access token

- via cli
```bash
  awslocal cognito-idp initiate-auth --auth-flow USER_PASSWORD_AUTH --auth-parameters USERNAME=testuser@gmail.com,PASSWORD=Testuser@123 --region us-east-1 --client-id your_client_id
```

or

```bash
awslocal cognito-idp admin-initiate-auth --user-pool-id your-user-pool-id --client-i your-client-id --auth-flow ADMIN_NO_SRP_AUTH --auth-parameters USERNAME=testuser,PASSWORD=Testuser@123
```


### Log in / Sign up with cognito hosted UI

http://localhost.localstack.cloud:4566/_aws/cognito-idp/login?response_type=code&client_id=<your_client_id>&redirect_uri=http://google.com
