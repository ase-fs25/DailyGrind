# Builder for frontend
FROM node:22-slim AS builder
WORKDIR /app/frontend
COPY frontend ./
RUN rm -rf node_modules
COPY frontend/package.json ./
RUN npm install
# After installing dependencies
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
