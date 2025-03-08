FROM fedora:39
WORKDIR /app
COPY target/oda-payment-service /app

CMD ["./oda-payment-service"]
