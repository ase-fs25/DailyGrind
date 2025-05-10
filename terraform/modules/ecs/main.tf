resource "aws_ecs_cluster" "main" {
  name = "dailygrind-cluster"
}

resource "aws_ecs_task_definition" "post-service" {
  family       = "post-service"
  requires_compatibilities = ["FARGATE"]
  network_mode = "awsvpc"
  cpu          = "256"
  memory       = "512"

  container_definitions = jsonencode([
    {
      name  = "post-service",
      image = "post-service:latest",
      portMappings = [
        {
          containerPort = 8080,
          hostPort      = 8080,
          protocol      = "tcp"
        }
      ],
      environment = [
        { name = "AWS_REGION", value = "us-east-1" }
      ]
    }
  ])
}

resource "aws_ecs_service" "post-service" {
  name            = "post-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.post-service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.subnet_ids
    security_groups  = var.security_group_ids
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = var.tg_post_service_arn
    container_name   = "post-service"
    container_port   = 8080
  }

  depends_on = [
    aws_ecs_task_definition.post-service
  ]
}

resource "aws_ecs_task_definition" "push-notification-service" {
  family       = "push-notification-service"
  requires_compatibilities = ["FARGATE"]
  network_mode = "awsvpc"
  cpu          = "256"
  memory       = "512"

  container_definitions = jsonencode([
    {
      name  = "push-notification-service",
      image = "push-notification-service:latest",
      portMappings = [
        {
          containerPort = 8080,
          hostPort      = 8080,
          protocol      = "tcp"
        }
      ],
      environment = [
        { name = "AWS_REGION", value = "us-east-1" }
      ]
    }
  ])
}

resource "aws_ecs_service" "push-notification-service" {
  name            = "push-notification-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.push-notification-service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.subnet_ids
    security_groups  = var.security_group_ids
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = var.tg_push_notification_service_arn
    container_name   = "push-notification-service"
    container_port   = 8080
  }

  depends_on = [
    aws_ecs_task_definition.push-notification-service
  ]
}

resource "aws_ecs_task_definition" "user-service" {
  family       = "user-service"
  requires_compatibilities = ["FARGATE"]
  network_mode = "awsvpc"
  cpu          = "256"
  memory       = "512"

  container_definitions = jsonencode([
    {
      name  = "user-service",
      image = "user-service:latest",
      portMappings = [
        {
          containerPort = 8080,
          hostPort      = 8080,
          protocol      = "tcp"
        }
      ],
      environment = [
        { name = "AWS_REGION", value = "us-east-1" }
      ]
    }
  ])
}

resource "aws_ecs_service" "user-service" {
  name            = "user-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.user-service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.subnet_ids
    security_groups  = var.security_group_ids
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = var.tg_user_service_arn
    container_name   = "user-service"
    container_port   = 8080
  }

  depends_on = [
    aws_ecs_task_definition.user-service
  ]
}
