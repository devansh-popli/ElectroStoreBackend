FROM openjdk:17-slim
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app.jar"]
