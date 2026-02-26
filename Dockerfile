FROM gradle:8.12-jdk21-corretto AS BUILD
COPY . /app/
WORKDIR /app/
RUN --mount=type=cache,target=/root/.vaadin --mount=type=cache,target=/home/gradle/.gradle --mount=type=cache,target=/home/gradle/.vaadin  ./gradlew clean build -Pvaadin.productionMode --no-daemon --info --stacktrace
#RUN ./gradlew clean build -Pvaadin.productionMode --no-daemon --info --stacktrace
WORKDIR /app/build/distributions/
RUN ls -la
RUN unzip app.zip

FROM eclipse-temurin:21-jdk
COPY --from=BUILD /app/build/distributions/app /app/
WORKDIR /app/bin
EXPOSE 8888
ENTRYPOINT ./app
