FROM gradle:8.8.0-jdk21-alpine AS BUILD
COPY . /app/

WORKDIR /app/
RUN --mount=type=cache,target=/root/.vaadin --mount=type=cache,target=/home/gradle/.gradle --mount=type=cache,target=/home/gradle/.vaadin  gradle clean build -Pvaadin.productionMode --no-daemon --info --stacktrace
#RUN gradle clean build -Pvaadin.productionMode --no-daemon --info --stacktrace
WORKDIR /app/build/distributions/
RUN ls -la
RUN unzip app.zip

FROM openjdk:21-bookworm
COPY --from=BUILD /app/build/distributions/app /app/
WORKDIR /app/bin
EXPOSE 8888
ENTRYPOINT ./app
