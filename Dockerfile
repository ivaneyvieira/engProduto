FROM gradle:8.3.0-jdk17-alpine AS BUILD
#RUN apt update && apt install unzip -y
COPY . /app/
COPY ./cache/gradle /root/.gradle/
COPY ./cache/vaadin /root/.vaadin/
WORKDIR /app/
# RUN ./gradlew clean test --no-daemon --info --stacktrace
RUN gradle clean build -Pvaadin.productionMode --no-daemon --info --stacktrace
WORKDIR /app/build/distributions/
RUN ls -la
RUN unzip app.zip


FROM eclipse-temurin:17
COPY --from=BUILD /app/build/distributions/app /app/
COPY --from=BUILD /root/.gradle /gradle
COPY --from=BUILD /root/.vaadin /vaadin
WORKDIR /app/bin
EXPOSE 8080
ENTRYPOINT ./app
