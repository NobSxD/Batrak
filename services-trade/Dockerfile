FROM  huecker.io/library/openjdk:17-jdk-slim

COPY target/services-trade-5.1.2-SNAPSHOT.jar /app/services-trade.jar

WORKDIR /app

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "services-trade.jar"]