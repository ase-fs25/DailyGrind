# Stage 1: Build React app
FROM node AS builder

WORKDIR /app/frontend
COPY package.json package-lock.json ./
RUN npm install
COPY . .

CMD ["npm", "run", "dev:docker"]


## Stage 2: Serve with Nginx
#FROM nginx:latest
#COPY --from=builder /app/build /usr/share/nginx/html
#
## Copy Nginx config
#COPY nginx.conf /etc/nginx/conf.d/default.conf
#
#EXPOSE 80
#
#CMD ["nginx", "-g", "daemon off;"]
