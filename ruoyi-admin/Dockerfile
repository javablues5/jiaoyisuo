# 阶段 1: 基础镜像
# 使用 Amazon Corretto 8 的 JRE 版本，它是基于 OpenJDK 8 的稳定发行版，体积小且安全。
# FROM amazoncorretto:8-jre-alpine-slim
# FROM openjdk:8-jre-alpine
FROM eclipse-temurin:8-jdk-focal

# 设置工作目录，容器启动后默认进入此目录
WORKDIR /app

# 暴露 Spring Boot 应用所使用的端口，默认为 8080
EXPOSE 8080

# -----------------------------------------------------------
# 【重要】 IDEA 部署占位符
# 当你使用 IDEA 的 "Build and Run" 或 "Run 'Docker'" 功能时，
# IDEA 会自动将你的 JAR 包复制到镜像的指定位置。
# 默认情况下，IDEA 通常将 JAR 文件命名为 app.jar 或放在 /app 目录下。
# 我们只需要确保运行命令是正确的即可。
# -----------------------------------------------------------


# 定义 JAR 包的文件名，便于统一管理和CMD引用   ${JAR_FILE}
ENV JAR_FILE=echo2-admin.jar
# 复制 JAR 包
# 这一行是必要的，但具体的文件名和路径在 IDEA 运行时会根据你的配置自动填充或覆盖。
# 这里的 app.jar 作为一个占位符。
COPY target/*.jar ${JAR_FILE}

# 设置时区（可选，根据需要设置）
# 使用 Alpine 基础镜像需要额外安装 tzdata
# RUN apk add --no-cache tzdata
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone


# 【新】设置默认的 Spring Profile 环境变量  ${YAML_FILE}
ENV YAML_FILE=dev1

# 设置容器启动时执行的命令：运行 JAR 包
# 推荐添加适当的 JVM 参数，例如限制内存使用，以适应容器环境
# CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-Xmx512m", "-Xms256m", "-jar", "${JAR_FILE}", "--spring.profiles.active=${YAML_FILE}"]
CMD java -Djava.security.egd=file:/dev/./urandom -Xmx512m -Xms256m -jar ${JAR_FILE} --spring.profiles.active=${YAML_FILE}
# 示例：如果需要指定 Spring Profile
# CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]

# 说明：
# -Djava.security.egd=file:/dev/./urandom: 提高在容器中生成随机数的效率。
# -Xmx512m -Xms256m: 限制 JVM 的最大和最小内存，请根据你的应用需求调整。
# app.jar: IDEA 默认使用的 JAR 包名称。