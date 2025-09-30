FROM openjdk:21-jdk-slim

# 设置临时目录卷
VOLUME /tmp

# 设置时区
RUN echo "Asia/Shanghai" > /etc/timezone && \
    ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# 暴露端口
EXPOSE 8080

# 复制 JAR 文件（需要先在本地构建好）
ADD target/ywhc-admin-backend-1.0.0.jar ywhc-admin-backend.jar

# 启动应用
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/ywhc-admin-backend.jar"]
