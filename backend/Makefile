APP_PORT ?= 9001

backend-dev:
	sbt "~run ${APP_PORT}"

clean:
	# Removes all .db files
	find ./ -name "*.db" -exec rm -rf {} \;
