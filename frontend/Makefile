APP_PORT ?= 9001

## Initiates frontend development environment. Default port is set to 9001
frontend-dev:
	sbt "~run ${APP_PORT}"

clean:
	# Removes all .db files
	find ./ -name "*.db" -exec rm -rf {} \;
