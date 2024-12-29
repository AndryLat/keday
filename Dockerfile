FROM gradle:8.12.0-jdk23-corretto AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon 

FROM amazoncorretto:23

EXPOSE 8080

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/kedat*SNAPSHOT.jar /app/kedat.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/kedat.jar"]