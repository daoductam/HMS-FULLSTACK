FROM maven:3-eclipse-temurin-21-alpine as build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-ea-1-jdk-slim
COPY --from=build /target/PharmacyMS-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","app.jar"]