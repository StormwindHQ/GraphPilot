# Pull base image
FROM openjdk:8u181

MAINTAINER Jason Shin <visualbbasic@gmail.com>

# Env variables
ENV SCALA_VERSION 2.12.6
ENV SBT_VERSION 1.2.1

# Install sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

RUN sbt compile

RUN mkdir -p /app/www
WORKDIR /app/www

CMD ["java", "-version"]
CMD ["sbt", "sbtVersion"]
