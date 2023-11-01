FROM openjdk:17

EXPOSE 8080

ADD target/hdshop-0.0.1-SNAPSHOT.jar hdshop.jar

ENTRYPOINT ["java", "-jar", "/hdshop.jar"]

