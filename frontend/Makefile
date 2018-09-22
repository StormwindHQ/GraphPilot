APP_PORT ?= 9001

## Initiates frontend development environment. Default port is set to 9001
frontend-dev:
	sbt "~run ${APP_PORT}"

clean:
	# Removes all .db files
	find ./ -name "*.db" -exec rm -rf {} \;

frontend-help:
	echo "docker build - sbt docker:publishLocal"
	echo "sbt run - run in testing mode under the port 9000"

frontend-docker-local:
	# docker run -p 9000:9000 -e DB_DEFAULT_PROFILE="slick.jdbc.H2Profile$" -e DB_DEFAULT_DRIVER="org.h2.Driver" -e DB_DEFAULT_URL="jdbc:h2:./stormwind" -e DB_DEFAULT_USER="sa" -e DB_DEFAULT_PASSWORD="" --entrypoint "bin/play-scala-starter-example" play-scala-starter-example:1.0-SNAPSHOT -Dconfig.file=/opt/docker/conf/production.conf
	docker run \
	-p 9000:9000 \
	-e DB_DEFAULT_PROFILE="slick.jdbc.H2Profile$" \
	-e DB_DEFAULT_DRIVER="org.h2.Driver" \
	-e DB_DEFAULT_URL="jdbc:h2:./stormwind" \
	-e DB_DEFAULT_USER="sa" \
	-e DB_DEFAULT_PASSWORD="" \
	--entrypoint "bin/play-scala-starter-example" \
	play-scala-starter-example:1.0-SNAPSHOT -Dconfig.file=/opt/docker/conf/production.conf