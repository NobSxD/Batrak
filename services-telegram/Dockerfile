FROM  huecker.io/library/openjdk:17-jdk-slim

COPY target/services-telegram-5.1.2-SNAPSHOT.jar /app/services-telegram.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "services-telegram.jar"]