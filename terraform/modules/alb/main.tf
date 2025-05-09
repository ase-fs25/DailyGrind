resource "aws_lb" "main" {
  name               = "main-alb"
  internal           = false
  load_balancer_type = "application"
  subnets = [var.subnet_id]
  security_groups = [var.security_group_id]
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "Not Found"
      status_code  = "404"
    }
  }
}

# === Target Group: user-service ===
resource "aws_lb_target_group" "user" {
  name        = "tg-user"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"
}

resource "aws_lb_listener_rule" "user" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 10

  condition {
    path_pattern {
      values = ["/user*", "/users*"]
    }
  }

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.user.arn
  }
}

# === Target Group: post-service ===
resource "aws_lb_target_group" "post" {
  name        = "tg-post"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"
}

resource "aws_lb_listener_rule" "post" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 20

  condition {
    path_pattern {
      values = ["/post*", "/posts*"]
    }
  }

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.post.arn
  }
}

# === Target Group: push-notification-service ===
resource "aws_lb_target_group" "push" {
  name        = "tg-push"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"
}

resource "aws_lb_listener_rule" "push" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 30

  condition {
    path_pattern {
      values = ["/push*", "/notifications*"]
    }
  }

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.push.arn
  }
}

resource "aws_lb_target_group" "user_service" {
  name        = "user-service-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"
}

resource "aws_lb_target_group" "post_service" {
  name        = "post-service-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"
}

resource "aws_lb_target_group" "push_notification_service" {
  name        = "push-service-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"
}
