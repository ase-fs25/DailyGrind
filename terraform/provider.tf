provider "aws" {
  access_key = "test"
  secret_key = "test"
  region     = "us-east-1"

  endpoints {
    cognito-idp = "http://localhost:4566"
  }
}