# ============================================================
# Stage 1: Build — Node.js
# ============================================================
FROM node:20-alpine AS build

WORKDIR /build

# 先复制依赖声明，利用 Docker 层缓存
COPY package.json package-lock.json ./
RUN npm ci

# 复制源码并构建
COPY . .
RUN npm run build

# ============================================================
# Stage 2: Runtime — Nginx 托管静态资源
# ============================================================
FROM nginx:alpine

# 复制构建产物
COPY --from=build /build/dist /usr/share/nginx/html

# 复制自定义 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 健康检查
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD wget -qO- http://localhost:80/ || exit 1

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
