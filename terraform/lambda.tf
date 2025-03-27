data "aws_iam_policy_document" "assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "iam_for_lambda" {
  name               = "iam_for_lambda"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json
}

data "archive_file" "lambda" {
  type        = "zip"
  source_dir  = "${path.module}/../userConfirmationLambda/src"
  output_path = "${path.module}/lambda-functions/userConfirmationLambda.zip"
}

resource "aws_lambda_function" "confirm_user_lambda" {
  filename         = "${path.module}/lambda-functions/userConfirmationLambda.zip"
  function_name    = "userConfirmationLambda"
  role          = aws_iam_role.iam_for_lambda.arn
  handler          = "handler.lambda_handler"
  runtime          = "python3.8"
  source_code_hash = data.archive_file.lambda.output_base64sha256
}