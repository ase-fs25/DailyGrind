data "aws_iam_policy_document" "assume_role_lambda" {
  statement {
    effect = "Allow"
    principals {
      type = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }
    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "lambda_role" {
  name               = "iam_for_lambda"
  assume_role_policy = data.aws_iam_policy_document.assume_role_lambda.json
}

resource "aws_lambda_function" "confirm_user_lambda" {
  function_name = "userConfirmationLambda"
  filename      = "./../lambda-functions/user-confirmation-lambda.zip"
  role          = aws_iam_role.lambda_role.arn
  handler       = "handler.lambda_handler"
  runtime       = "python3.8"
  source_code_hash = filebase64sha256("./../lambda-functions/user-confirmation-lambda.zip")
}

resource "aws_lambda_function" "push_notification_lambda" {
  function_name = "pushNotificationLambda"
  filename      = "./../lambda-functions/push-notification-lambda.zip"
  role          = aws_iam_role.lambda_role.arn
  handler       = "index.handler"
  runtime       = "nodejs18.x"
  source_code_hash = filebase64sha256("./../lambda-functions/push-notification-lambda.zip")

  environment {
    variables = {
      PUBLIC_VAPID_KEY  = var.public_vapid_key
      PRIVATE_VAPID_KEY = var.private_vapid_key
      VAPID_SUBJECT     = var.vapid_subject
    }
  }
}

resource "aws_lambda_function" "schedule_notification_lambda" {
  function_name = "scheduleNotificationLambda"
  filename      = "./../lambda-functions/schedule-notification-lambda.zip"
  role          = aws_iam_role.lambda_role.arn
  handler       = "index.handler"
  runtime       = "nodejs18.x"
  source_code_hash = filebase64sha256("./../lambda-functions/schedule-notification-lambda.zip")

  environment {
    variables = {
      API_HOSTNAME = "host.docker.internal"
      API_PORT     = "8082"
    }
  }
}

resource "aws_iam_role_policy_attachment" "lambda_basic_execution" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_cloudwatch_event_rule" "daily_notification_trigger" {
  name                = "DailyNotificationTrigger"
  description         = "Triggers daily push notifications"
  schedule_expression = "rate(1 minute)"
  state               = "ENABLED"
}

resource "aws_cloudwatch_event_target" "notification_lambda_target" {
  rule      = aws_cloudwatch_event_rule.daily_notification_trigger.name
  target_id = "ScheduleNotificationLambda"
  arn       = aws_lambda_function.schedule_notification_lambda.arn
}

resource "aws_lambda_permission" "allow_eventbridge" {
  statement_id  = "AllowExecutionFromEventBridge"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.schedule_notification_lambda.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.daily_notification_trigger.arn
}
