# ============================================================
# Stage 1: Build — Maven + JDK 17
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# 先复制 POM，利用 Docker 层缓存加速依赖下载
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并打包
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================================================
# Stage 2: Runtime — 精简 JRE 镜像
# ============================================================
FROM eclipse-temurin:17-jre

WORKDIR /app

# 从构建阶段复制 JAR
COPY --from=build /build/target/book-manager-1.0.0.jar app.jar

# 健康检查
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD curl -s http://localhost:8081/actuator/health | grep '"status":"UP"' || exit 1

EXPOSE 8081

# JVM 优化参数：内存限制、GC 策略
ENTRYPOINT ["java", \
    "-XX:+UseG1GC", \
    "-XX:+HeapDumpOnOutOfMemoryError", \
    "-jar", "app.jar"]
