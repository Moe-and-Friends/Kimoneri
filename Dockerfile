# TODO: Migrate this application to Java 24.
FROM gradle:8.14.0-jdk21-alpine AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build jar --no-daemon

FROM gcr.io/distroless/java21-debian12

COPY --from=build /home/gradle/src/build/libs/Kimoneri-1.0.jar /Kimoneri.jar
ENTRYPOINT ["java", "-jar", "/Kimoneri.jar"]