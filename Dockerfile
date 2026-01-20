FROM hseeberger/scala-sbt:graalvm-ce-21.3.0-java17_1.6.2_3.1.1

WORKDIR /minesweeperse
COPY . /minesweeperse

RUN apt-get update && apt-get install -y \
    libx11-6 \
    libxext6 \
    libxi6 \
    libxtst6 \
    libxrender1 \
    libxrandr2 \
    libfreetype6 \
    libfontconfig1 \
    libpango-1.0-0 \
    libpangocairo-1.0-0 \
    libpangoft2-1.0-0 \
    libcairo2 \
    libgtk-3-0 \
    && rm -rf /var/lib/apt/lists/*

CMD ["sbt", "run"]
