FROM maven:3-openjdk-18 AS MAVEN_BUILD

ENV JWT_SECRET ${JWT_SECRET}
ENV POSTGRES_USER ${POSTGRES_USER}
ENV POSTGRES_PASSWORD ${POSTGRES_PASSWORD}
ENV POSTGRES_DB ${POSTGRES_DB}

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn package

FROM openjdk:18

WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/backend-quickstart-0.0.1-SNAPSHOT.jar /app/

ENTRYPOINT ["java", "-jar", "backend-quickstart-0.0.1-SNAPSHOT.jar"]