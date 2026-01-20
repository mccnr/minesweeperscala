FROM hseeberger/scala-sbt
WORKDIR /minesweeperse
ADD . /minesweeperse
CMD sbt test
