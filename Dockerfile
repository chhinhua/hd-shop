FROM maven:3.8.5-openjdk-17 AS build

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim

COPY --from=build /target/duck-shop-0.0.1-SNAPSHOT.jar duck.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "/duck.jar"]

