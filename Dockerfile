FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/app.jar app.jar

EXPOSE 8080

# تشغيل آمن + تحسين memory
ENTRYPOINT ["java",
  "-XX:+UseContainerSupport",
  "-XX:MaxRAMPercentage=75",
  "-jar",
  "app.jar"
]
