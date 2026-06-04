FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/app.jar app.jar

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8080

ENTRYPOINT ["java",
  "-XX:+UseContainerSupport",
  "-XX:MaxRAMPercentage=75",
  "-jar",
  "app.jar"
]
