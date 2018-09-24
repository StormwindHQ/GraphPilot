APP_PORT ?= 9002

## Initiates frontend development environment. Default port is set to 9001
frontend-dev:
	sbt "~run ${APP_PORT}"

frontend-clean:
	# Removes all .db files
	find ./ -name "*.db" -exec rm -rf {} \;

## Starts frontend in production mode using Docker
frontend-docker-prod:
	docker run \
	 -e DB_DEFAULT_PROFILE="slick.jdbc.H2Profile\$$" \
	 -e DB_DEFAULT_DRIVER="org.h2.Driver" \
	 -e DB_DEFAULT_URL="jdbc:h2:./stormwind" \
	 -e DB_DEFAULT_USER="sa" \
	 -e DB_DEFAULT_PASSWORD="" \
	 --entrypoint "bin/play-scala-starter-example" \
	 -p 9000:9000 play-scala-starter-example:1.0-SNAPSHOT \
	 "-Dconfig.file=/opt/docker/conf/production.conf"
