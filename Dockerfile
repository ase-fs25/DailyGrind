FROM docker:cli

WORKDIR /app
COPY . .

# Install Docker Compose V2
RUN apk add --no-cache curl \
 && mkdir -p /usr/local/libexec/docker/cli-plugins \
 && curl -SL https://github.com/docker/compose/releases/download/v2.36.0/docker-compose-linux-x86_64 \
    -o /usr/local/libexec/docker/cli-plugins/docker-compose \
 && chmod +x /usr/local/libexec/docker/cli-plugins/docker-compose

ENV DOCKER_CONFIG=/root/.docker

CMD ["docker", "compose", "up", "--profile", "prod"]
