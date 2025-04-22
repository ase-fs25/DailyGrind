data "aws_iam_policy_document" "assume_role_ecs" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "iam_for_ecs" {
  name               = "iam_for_ecs"
  assume_role_policy = data.aws_iam_policy_document.assume_role_ecs.json
}


resource "aws_ecs_cluster" "main" {
  name = "localstack-cluster"
}