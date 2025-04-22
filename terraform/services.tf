locals {
  services = [
    { name = "user-service", image = "user-service:latest" },
  ]
}

resource "aws_ecs_task_definition" "tasks" {
  for_each                 = { for svc in local.services : svc.name => svc }
  family                   = each.value.name
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.iam_for_ecs.arn

  container_definitions = jsonencode([{
    name  = each.value.name
    image = each.value.image
    memory = 512
    portMappings = [
      {
        containerPort = 8080
        hostPort      = 0
      }
    ]
    environment = [
      {
        name  = "AWS_CONTAINER_CREDENTIALS_RELATIVE_URI"
        value = "/ecs_credentials"
      }
    ]
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = "/ecs/${each.value.name}"
        awslogs-region        = "us-east-1"
        awslogs-stream-prefix = "ecs"
      }
    }
  }])
}

resource "aws_ecs_service" "services" {
  for_each        = aws_ecs_task_definition.tasks
  name            = "${each.key}-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = each.value.arn
  desired_count   = 1
  launch_type     = "EC2"
}