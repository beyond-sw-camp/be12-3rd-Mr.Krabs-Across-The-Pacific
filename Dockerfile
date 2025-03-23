FROM    openjdk:17-jdk-slim
ADD     ./build/libs/ATP_Back-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE  8080
CMD     java -jar /app.jar