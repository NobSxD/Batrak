FROM  huecker.io/library/openjdk:17-jdk-slim

COPY build/libs/services-node-5.1.2-SNAPSHOT.jar /app/services-node.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "services-node.jar"]