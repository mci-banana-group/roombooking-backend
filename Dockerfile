FROM gradle:8-jdk17 AS build
WORKDIR /app
COPY . .
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew #converts CRLF endings to LF (UNIX-style), when using Windows.
RUN ./gradlew clean build --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/roombooking-all.jar app.jar
EXPOSE 8080
CMD ["java","-jar","/app/app.jar"]