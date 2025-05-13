resource "aws_ecs_cluster" "main" {
  name = "dailygrind-cluster"
}

resource "aws_ecs_task_definition" "post_service" {
  family                   = "post_service"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "256"
  memory                   = "512"

  container_definitions = jsonencode([{
    name  = "post_service",
    image = "post_service:latest",
    portMappings = [{
      containerPort = 8080,
      hostPort      = 8080,
      protocol      = "tcp"
    }],
    environment = [
      { name = "AWS_REGION", value = "us-east-1" }
    ]
  }])
}

resource "aws_ecs_service" "post_service" {
  name            = "post_service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.post_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.subnet_ids
    security_groups  = var.security_group_ids
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = var.tg_post_service_arn
    container_name   = "post_service"
    container_port   = 8080
  }

  depends_on = [
    aws_ecs_task_definition.post_service
  ]
}

resource "aws_ecs_task_definition" "push_notification_service" {
  family                   = "push_notification_service"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "256"
  memory                   = "512"

  container_definitions = jsonencode([{
    name  = "push_notification_service",
    image = "push_notification_service:latest",
    portMappings = [{
      containerPort = 8080,
      hostPort      = 8081,
      protocol      = "tcp"
    }],
    environment = [
      { name = "AWS_REGION", value = "us-east-1" }
    ]
  }])
}

resource "aws_ecs_service" "push_notification_service" {
  name            = "push_notification_service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.push_notification_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.subnet_ids
    security_groups  = var.security_group_ids
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = var.tg_push_notification_service_arn
    container_name   = "push_notification_service"
    container_port   = 8080
  }

  depends_on = [
    aws_ecs_task_definition.push_notification_service
  ]
}

resource "aws_ecs_task_definition" "user_service" {
  family                   = "user_service"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "256"
  memory                   = "512"

  container_definitions = jsonencode([{
    name  = "user_service",
    image = "user_service:latest",
    portMappings = [{
      containerPort = 8080,
      hostPort      = 8082,
      protocol      = "tcp"
    }],
    environment = [
      { name = "AWS_REGION", value = "us-east-1" }
    ]
  }])
}

resource "aws_ecs_service" "user_service" {
  name            = "user_service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.user_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.subnet_ids
    security_groups  = var.security_group_ids
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = var.tg_user_service_arn
    container_name   = "user_service"
    container_port   = 8080
  }

  depends_on = [
    aws_ecs_task_definition.user_service
  ]
}
