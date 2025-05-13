variable "name" {
  description = "Name prefix for Cognito pool and client"
  type        = string
  default     = "daily-grind"
}

variable "lambda_pre_signup_arn" {
  description = "ARN of the pre sign-up lambda"
  type        = string
}

variable "callback_urls" {
  type = list(string)
  default = ["http://localhost:3000", "http://localhost:4566", "https://oauth.pstmn.io/v1/callback"]
}

variable "logout_urls" {
  type = list(string)
  default = ["http://localhost:3000", "http://localhost:4566"]
}
