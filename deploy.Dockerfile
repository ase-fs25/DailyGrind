# Deploy image
FROM docker:cli
RUN apk add --no-cache bash curl python3 py3-pip netcat-openbsd aws-cli nodejs npm
WORKDIR /app
COPY ./deploy.sh ./deploy.sh
COPY microservices ./microservices
RUN chmod +x ./deploy.sh
ENTRYPOINT ["bash", "./deploy.sh"]
