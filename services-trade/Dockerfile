FROM openjdk:17-jdk

COPY ./build/libs/services-trade-5.1.2-SNAPSHOT.jar /app/services-trade.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "services-trade.jar"]