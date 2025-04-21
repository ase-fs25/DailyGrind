# Push-Notification Lambda Function
data "archive_file" "push_notification_lambda" {
  type       = "zip"
  source_dir  = "./../lambda-functions/push-notification-lambda/src"
  output_path = "./../lambda-functions/push-notification-lambda.zip"
}

resource "aws_lambda_function" "push_notification_lambda" {
  filename         = "./../lambda-functions/push-notification-lambda.zip"
  function_name    = "pushNotificationLambda"
  role           = aws_iam_role.iam_for_lambda.arn
  handler          = "index.handler"
  runtime          = "nodejs18.x"
  source_code_hash = data.archive_file.push_notification_lambda.output_base64sha256

  environment {
    variables = {
      PUBLIC_VAPID_KEY  = "BGNKMIqVDc7udZPZ8manv9UF7uzQtCaYJvzEEe7rr6zor3HPkFuPTN5q1cUoABwYR-Dwa5Fwhx0BUOImZJC-rG8"
      PRIVATE_VAPID_KEY = "1tyL-Kw64dsVRGE7uUew77koormjIC67lu-jbTLWW0k"
      VAPID_SUBJECT     = "mailto:tim.vorburger@uzh.ch"
    }
  }
}