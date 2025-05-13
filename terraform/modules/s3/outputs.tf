output "website_url" {
  description = "LocalStack URL for the frontend index"
  value       = "http://localhost:4566/${var.bucket_name}/index.html"
}

output "profile_pictures_bucket_name" {
  description = "Name of the profile pictures bucket"
  value       = aws_s3_bucket.profile_pictures.bucket
}

output "profile_pictures_bucket_arn" {
  description = "ARN of the profile pictures bucket"
  value       = aws_s3_bucket.profile_pictures.arn
}

output "profile_pictures_access_role_arn" {
  description = "ARN of the IAM role for profile pictures access"
  value       = aws_iam_role.profile_pictures_access_role.arn
}

output "profile_pictures_policy_arn" {
  description = "ARN of the IAM policy for profile pictures operations"
  value       = aws_iam_policy.profile_pictures_policy.arn
}
