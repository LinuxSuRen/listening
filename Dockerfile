FROM maven:3.9.6-amazoncorretto-21 AS builder

WORKDIR /workspace
COPY . .
RUN mvn package -DskipTests -Djavafx.platform=linux -DskipLaunch4j

FROM ubuntu/jre:17-22.04_39

WORKDIR /workspace
COPY --from=builder /workspace/target/listening-0.0.1-SNAPSHOT-full-linux.jar listening.jar

EXPOSE 8080

CMD ["-cp", "listening.jar", "listening.linuxsuren.github.io.cli.CacheServerCLI"]
