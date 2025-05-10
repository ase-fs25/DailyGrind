# Builder for frontend
FROM node:alpine AS builder
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend ./
RUN npm run build

# Deploy image
FROM docker:cli
RUN apk add --no-cache bash curl python3 py3-pip netcat-openbsd aws-cli
WORKDIR /app
COPY --from=builder /app/frontend/dist ./frontend/dist
COPY ./deploy.sh ./deploy.sh
COPY microservices ./microservices
RUN chmod +x ./deploy.sh
ENTRYPOINT ["bash", "./deploy.sh"]
