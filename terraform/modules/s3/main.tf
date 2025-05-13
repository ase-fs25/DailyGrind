#################################################
# FRONTEND WEBSITE BUCKET
#################################################
resource "aws_s3_bucket" "frontend" {
  bucket        = var.bucket_name
  force_destroy = true
}

resource "aws_s3_bucket_website_configuration" "website" {
  bucket = aws_s3_bucket.frontend.id

  index_document { suffix = "index.html" }
  error_document { key = "index.html" }
}

resource "aws_s3_bucket_public_access_block" "public_access" {
  bucket                  = aws_s3_bucket.frontend.id
  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "bucket_policy" {
  bucket = aws_s3_bucket.frontend.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = "*"
        Action = ["s3:GetObject"]
        Resource  = "${aws_s3_bucket.frontend.arn}/*"
      }
    ]
  })
}


#################################################
# PROFILE PICTURES BUCKET
#################################################
resource "aws_s3_bucket" "profile_pictures" {
  bucket        = var.profile_pictures_bucket_name
  force_destroy = true
}

resource "aws_s3_bucket_ownership_controls" "profile_pictures_ownership" {
  bucket = aws_s3_bucket.profile_pictures.id

  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_public_access_block" "profile_pictures_access" {
  bucket                  = aws_s3_bucket.profile_pictures.id
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
    allowed_methods = var.profile_pictures_cors_methods
    allowed_origins = var.profile_pictures_cors_origins
    expose_headers = ["ETag"]
    max_age_seconds = var.profile_pictures_cors_max_age
  }
}

#################################################
# IAM FOR PROFILE PICTURES ACCESS
#################################################
resource "aws_iam_role" "profile_pictures_access_role" {
  name = "${var.profile_pictures_bucket_name}-access-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = { Service = "ec2.amazonaws.com" }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "profile_pictures_policy" {
  name        = "${var.profile_pictures_bucket_name}-policy"
  description = "Policy for managing profile pictures in S3"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:DeleteObject",
          "s3:ListBucket",
        ]
        Resource = [
          aws_s3_bucket.profile_pictures.arn,
          "${aws_s3_bucket.profile_pictures.arn}/*",
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "profile_pictures_policy_attachment" {
  role       = aws_iam_role.profile_pictures_access_role.name
  policy_arn = aws_iam_policy.profile_pictures_policy.arn
}

# Optionally, if you need an instance profile:
# resource "aws_iam_instance_profile" "profile_pictures_instance_profile" {
#   name = "${var.profile_pictures_bucket_name}-instance-profile"
#   role = aws_iam_role.profile_pictures_access_role.name
# }
