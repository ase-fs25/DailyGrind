provider "aws" {
  access_key = "test"
  secret_key = "test"
  region     = "us-east-1"

  endpoints {
    cognitoidp = "http://locahost:4566"
  }
}