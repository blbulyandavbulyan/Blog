FROM openjdk:17
LABEL authors="david"
EXPOSE 8080
VOLUME /tmp
ARG JAR_FILE=target/blog.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]