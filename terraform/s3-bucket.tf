resource "aws_s3_bucket" "frontend_bucket" {
  bucket        = "dailygrind"
  force_destroy = true
}

resource "aws_s3_bucket_website_configuration" "frontend_website" {
  bucket = aws_s3_bucket.frontend_bucket.id

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "index.html"
  }
}

resource "aws_s3_bucket_public_access_block" "frontend_public_access" {
  bucket = aws_s3_bucket.frontend_bucket.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "frontend_bucket_policy" {
  bucket = aws_s3_bucket.frontend_bucket.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = "*"
        Action = ["s3:GetObject"]
        Resource = ["${aws_s3_bucket.frontend_bucket.arn}/*"]
      }
    ]
  })
}

output "frontend_url" {
  # value = aws_s3_bucket_website_configuration.frontend_website.website_endpoint
  value = "http://localhost:4566/dailygrind/index.html"
}
