FROM eclipse-temurin:17-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} zeronebot.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/zeronebot.jar"]