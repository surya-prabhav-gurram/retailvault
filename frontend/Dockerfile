# ---- Build stage ----
FROM node:18-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm install --legacy-peer-deps && \
    npm install --legacy-peer-deps ajv@^8 ajv-keywords@^5

COPY . .
ENV GENERATE_SOURCEMAP=false
ENV REACT_APP_API_BASE_URL=http://localhost:8080
RUN npm run build

# ---- Serve stage ----
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]