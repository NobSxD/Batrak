FROM openjdk:17-jdk

COPY ./build/libs/services-telegram-5.1.2-SNAPSHOT.jar /app/services-telegram.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "services-telegram.jar"]