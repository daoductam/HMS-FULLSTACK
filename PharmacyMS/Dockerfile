FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY PharmacyMS/target/quarkus-app/ /app/quarkus-app/
EXPOSE 9300
ENV JAVA_OPTS="-Xms128m -Xmx512m -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/quarkus-app/quarkus-run.jar"]