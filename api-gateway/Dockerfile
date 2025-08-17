FROM openjdk:17-jdk-alpine
EXPOSE 8085
ARG GOOGLE_CLIENTID
ARG GOOGLE_CLIENTSECRET

# Make them available at runtime inside the container
ENV GOOGLE_CLIENTID=${GOOGLE_CLIENTID}
ENV GOOGLE_CLIENTSECRET=${GOOGLE_CLIENTSECRET}
ADD target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
