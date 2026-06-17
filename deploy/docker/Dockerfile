FROM eclipse-temurin:21-jre
WORKDIR /app
COPY backend/target/govbi-ia-*.jar /app/govbi-ia.jar
EXPOSE 8080
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-local}", \
  "-jar", "/app/govbi-ia.jar"]
