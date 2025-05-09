variable "bucket_name" {
  description = "Name of the S3 bucket for frontend hosting"
  type        = string
  default     = "dailygrind"
}

variable "cognito_client_secret" {
  type        = string
  description = "Cognito client secret"
}
