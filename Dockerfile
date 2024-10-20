FROM ghcr.io/graalvm/jdk:java21

COPY . .

RUN mvnw -Pnative -Dagent exec:exec@java-agent