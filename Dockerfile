#Stage 1

FROM maven:3.9-eclipse-temurin-17 as builder

# spped up JVM
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY ./src ./src

RUN mvn clean package -DskipTests

#Stage 2

FROM eclipse-temurin:17.0.7_7-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/user-manager-0.0.1-SNAPSHOT.jar /app

ENTRYPOINT ["java", "-jar", "/app/user-manager-0.0.1-SNAPSHOT.jar"]