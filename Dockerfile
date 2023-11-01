FROM openjdk:17
EXPOSE: 8080
ADD target/hdshop-0.0.1-SNAPSHOT.jar hdshop-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/hd-shop-0.0.1-SNAPSHOT.jar"]