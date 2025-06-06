FROM docker.io/library/eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/oda-payment-service-0.1.5.jar /app

CMD ["java","--add-opens","java.base/java.time=ALL-UNNAMED","-jar","oda-payment-service-0.1.5.jar"]
