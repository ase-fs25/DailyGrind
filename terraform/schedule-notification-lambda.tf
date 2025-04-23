# Schedule notification Lambda triggers the daily reminder
data "archive_file" "schedule_notification_lambda" {
  type        = "zip"
  source_dir  = "./../lambda-functions/schedule-notification-lambda/src"
  output_path = "./../lambda-functions/schedule-notification-lambda.zip"
}

resource "aws_lambda_function" "schedule_notification_lambda" {
  filename         = "./../lambda-functions/schedule-notification-lambda.zip"
  function_name    = "scheduleNotificationLambda"
  role             = aws_iam_role.iam_for_lambda.arn
  handler          = "index.handler"
  runtime          = "nodejs18.x"
  source_code_hash = data.archive_file.schedule_notification_lambda.output_base64sha256

  environment {
    variables = {
      API_HOSTNAME = "host.docker.internal"
      API_PORT     = "8082"
    }
  }
}

resource "aws_iam_role_policy_attachment" "lambda_basic_execution" {
  role       = aws_iam_role.iam_for_lambda.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# Rule to trigger Lambda every day at 12:00 PM
resource "aws_cloudwatch_event_rule" "daily_notification_trigger" {
  name                = "DailyNotificationTrigger"
  description         = "Triggers push notification service daily at 12:00 PM"
  #schedule_expression = "cron(0 12 * * ? *)" #for production
  schedule_expression = "rate(1 minute)" #for testing
  state = "DISABLE"
}

# Target to connect EventBridge rule
resource "aws_cloudwatch_event_target" "notification_lambda_target" {
  rule      = aws_cloudwatch_event_rule.daily_notification_trigger.name
  target_id = "ScheduleNotificationLambda"
  arn       = aws_lambda_function.schedule_notification_lambda.arn
}

# Permission for EventBridge to invoke Lambda
resource "aws_lambda_permission" "allow_eventbridge" {
  statement_id  = "AllowExecutionFromEventBridge"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.schedule_notification_lambda.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.daily_notification_trigger.arn
}