FROM node:alpine AS builder
WORKDIR /app/frontend
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM alpine:latest
RUN apk add --no-cache bash curl python3 py3-pip netcat-openbsd && pip install --break-system-packages awscli
WORKDIR /app/frontend
COPY --from=builder /app/frontend/dist ./dist
COPY --from=builder /app/frontend/deploy-frontend.sh ./deploy-frontend.sh
RUN chmod +x ./deploy-frontend.sh
ENTRYPOINT ["bash", "./deploy-frontend.sh"]
