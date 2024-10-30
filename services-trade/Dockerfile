FROM  huecker.io/library/openjdk:17-jdk-slim

COPY build/libs/services-trade-5.1.2-SNAPSHOT.jar /app/services-trade.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "services-trade.jar"]