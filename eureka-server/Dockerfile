FROM eclipse-temurin:17-jdk-jammy

WORKDIR /movieticketsapp

COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY movie-media-service/pom.xml movie-media-service/pom.xml
COPY movie-service/pom.xml movie-service/pom.xml
COPY shared/pom.xml shared/pom.xml
COPY shopping-service/pom.xml shopping-service/pom.xml
COPY user-service/pom.xml user-service/pom.xml

RUN chmod 775 mvnw

RUN ./mvnw dependency:go-offline

COPY . .

RUN chmod 775 mvnw

RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar"]