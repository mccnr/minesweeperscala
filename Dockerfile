FROM eclipse-temurin:17-jdk

WORKDIR /minesweeperse

# sbt installieren
RUN apt-get update && \
    apt-get install -y curl gnupg && \
    curl -fsSL https://repo.scala-sbt.org/scalasbt/debian/sbt-1.9.9.deb -o sbt.deb && \
    apt-get install -y ./sbt.deb && \
    rm sbt.deb

RUN apt-get update && apt-get install -y \
    libx11-6 \
    libxext6 \
    libxi6 \
    libxtst6 \
    libxrender1 \
    libxrandr2 \
    libxxf86vm1 \
    libfreetype6 \
    libfontconfig1 \
    libpango-1.0-0 \
    libpangocairo-1.0-0 \
    libpangoft2-1.0-0 \
    libcairo2 \
    libgtk-3-0 \
    && rm -rf /var/lib/apt/lists/*

COPY . .

CMD ["sbt", "run"]
