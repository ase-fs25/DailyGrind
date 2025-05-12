variable "bucket_name" {
  description = "Name of the S3 bucket for frontend hosting"
  type        = string
  default     = "dailygrind"
}

variable "profile_pictures_bucket_name" {
  description = "Name of the S3 bucket for profile pictures"
  type        = string
  default     = "dailygrind-profile-pictures"
}

variable "cognito_client_secret" {
  type        = string
  description = "Cognito client secret"
}
