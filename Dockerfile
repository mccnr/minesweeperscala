FROM eclipse-temurin:17-jdk

WORKDIR /minesweeperse

# sbt installieren
RUN apt-get update && \
    apt-get install -y curl gnupg && \
    curl -fsSL https://repo.scala-sbt.org/scalasbt/debian/sbt-1.9.9.deb -o sbt.deb && \
    apt-get install -y ./sbt.deb && \
    rm sbt.deb
    
ADD . /minesweeperse
CMD sbt run

EXPOSE 8080
