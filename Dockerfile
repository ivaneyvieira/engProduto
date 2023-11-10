FROM gradle:8.3.0-jdk17-alpine AS BUILD
#RUN apt update && apt install unzip -y
COPY . /app/
WORKDIR /app/
# RUN ./gradlew clean test --no-daemon --info --stacktrace
RUN gradle clean build -Pvaadin.productionMode --no-daemon --info --stacktrace
WORKDIR /app/build/distributions/
RUN ls -la
RUN unzip app.zip


FROM eclipse-temurin:17-jdk-alpine
COPY --from=BUILD /app/build/distributions/app /app/
WORKDIR /app/bin
EXPOSE 8080
ENTRYPOINT ./app
