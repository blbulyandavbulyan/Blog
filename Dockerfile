# build stage
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY . /app/
RUN mvn clean package
# package stage
FROM openjdk:17-alpine
LABEL authors="david"
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/target/*.jar /app/app.jar
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=prod", "app.jar"]