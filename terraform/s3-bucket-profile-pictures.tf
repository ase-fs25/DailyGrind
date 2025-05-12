resource "aws_s3_bucket" "profile_pictures" {
  bucket = "dailygrind-profile-pictures"
  force_destroy = true
}

resource "aws_s3_bucket_ownership_controls" "profile_pictures_ownership" {
  bucket = aws_s3_bucket.profile_pictures.id

  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_public_access_block" "profile_pictures_access" {
  bucket = aws_s3_bucket.profile_pictures.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_acl" "profile_pictures_acl" {
  depends_on = [
    aws_s3_bucket_ownership_controls.profile_pictures_ownership,
    aws_s3_bucket_public_access_block.profile_pictures_access,
  ]

  bucket = aws_s3_bucket.profile_pictures.id
  acl    = "public-read"
}


resource "aws_s3_bucket_cors_configuration" "profile_pictures_cors" {
  bucket = aws_s3_bucket.profile_pictures.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["PUT", "POST", "GET", "HEAD", "DELETE"]
    allowed_origins = ["http://localhost:4566", "http://localhost:3000", "http://localhost:8080"]
    expose_headers  = ["ETag"]
    max_age_seconds = 3000
  }
}


# IAM role for accessing profile pictures S3 bucket
resource "aws_iam_role" "profile_pictures_access_role" {
  name = "profile-pictures-access-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "ec2.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

# IAM policy for profile pictures operations
resource "aws_iam_policy" "profile_pictures_policy" {
  name        = "profile-pictures-policy"
  description = "Policy for managing profile pictures in S3"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:DeleteObject",
          "s3:ListBucket"
        ],
        Resource = [
          aws_s3_bucket.profile_pictures.arn,
          "${aws_s3_bucket.profile_pictures.arn}/*"
        ]
      }
    ]
  })
}

# Attach the policy to the role
resource "aws_iam_role_policy_attachment" "profile_pictures_policy_attachment" {
  role       = aws_iam_role.profile_pictures_access_role.name
  policy_arn = aws_iam_policy.profile_pictures_policy.arn
}

# # Allow EC2 instances to use role
# resource "aws_iam_instance_profile" "profile_pictures_instance_profile" {
#   name = "profile-pictures-instance-profile"
#   role = aws_iam_role.profile_pictures_access_role.name
# }
