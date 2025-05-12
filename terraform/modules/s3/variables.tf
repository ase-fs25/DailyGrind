variable "bucket_name" {
  type        = string
  description = "Frontend hosting bucket name"
}

variable "profile_pictures_bucket_name" {
  type        = string
  description = "Bucket name for storing profile pictures"
}

variable "profile_pictures_cors_origins" {
  type = list(string)
  default = ["http://localhost:4566", "http://localstack:4566", "http://localhost:3000", "http://localhost:30001", "http://localhost:8080"]
  description = "Allowed origins for profile pictures bucket CORS"
}

variable "profile_pictures_cors_methods" {
  type = list(string)
  default = ["PUT", "POST", "GET", "HEAD", "DELETE"]
  description = "Allowed HTTP methods for profile pictures bucket CORS"
}

variable "profile_pictures_cors_max_age" {
  type        = number
  default     = 3000
  description = "Max age (seconds) for profile pictures bucket CORS preflight"
}
