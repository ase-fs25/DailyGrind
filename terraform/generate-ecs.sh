#!/bin/bash

set -e

BASE_DIR="./modules/ecs"
MAIN_TF="$BASE_DIR/main.tf"
OUTPUTS_TF="$BASE_DIR/outputs.tf"

# Clean old files
rm -f "$MAIN_TF"
rm -f "$OUTPUTS_TF"

# Start main.tf with ECS cluster definition
cat > "$MAIN_TF" <<EOF
resource "aws_ecs_cluster" "main" {
  name = "dailygrind-cluster"
}
EOF

# Prepare outputs.tf
touch "$OUTPUTS_TF"

# Generate ECS resources and append to main.tf
for dir in /microservices/*/; do
  service_name=$(basename "$dir")

  cat >> "$MAIN_TF" <<EOF

resource "aws_ecs_task_definition" "${service_name}" {
  family                   = "${service_name}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "256"
  memory                   = "512"

  container_definitions = jsonencode([
    {
      name  = "${service_name}"
      image = "${service_name}:latest"
      portMappings = [{
        containerPort = 8080,
        hostPort      = 8080,
        protocol      = "tcp"
      }]
      environment = [
        { name = "AWS_REGION", value = "us-east-1" }
      ]
    }
  ])
}

resource "aws_ecs_service" "${service_name}" {
  name            = "${service_name}"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.${service_name}.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.subnet_ids
    security_groups  = var.security_group_ids
    assign_public_ip = true
  }

  depends_on = [aws_ecs_task_definition.${service_name}]
}
EOF

  # Output block
  cat >> "$OUTPUTS_TF" <<EOF
output "${service_name}_service_name" {
  value = aws_ecs_service.${service_name}.name
}

output "${service_name}_task_arn" {
  value = aws_ecs_task_definition.${service_name}.arn
}

EOF

  echo "✅ Generated ECS config and outputs for: $service_name"
done

echo "🎉 Done: ECS service definitions and outputs written to $MAIN_TF"
