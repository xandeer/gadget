FROM openjdk:8-alpine

COPY target/uberjar/gadget.jar /gadget/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/gadget/app.jar"]
