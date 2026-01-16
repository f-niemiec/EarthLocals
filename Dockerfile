FROM maven:3.9.12-eclipse-temurin-25 AS build

WORKDIR /app

# Copy pom and source code
COPY Deliverables/Code/pom.xml .
COPY Deliverables/Code/package.json .
COPY Deliverables/Code/src ./src

# Build the application
RUN mvn clean package -DskipTests

FROM amazoncorretto:25-alpine

WORKDIR /app

COPY --from=build /app/target/earthlocals-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]