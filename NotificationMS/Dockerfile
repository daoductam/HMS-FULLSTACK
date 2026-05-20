# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy and build the shared library first
COPY hms-common /app/hms-common
RUN mvn -f /app/hms-common/pom.xml clean install -DskipTests

# Copy and build the main service
COPY NotificationMS /app/NotificationMS
RUN mvn -f /app/NotificationMS/pom.xml clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the Quarkus fast-jar files
COPY --from=build /app/NotificationMS/target/quarkus-app/ /app/quarkus-app/

EXPOSE 8085

ENV JAVA_OPTS="-Xms128m -Xmx512m -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/quarkus-app/quarkus-run.jar"]
