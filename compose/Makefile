#   _________ __                              .__            .___
#  /   _____//  |_  ___________  _______  _  _|__| ____    __| _/
#  \_____  \\   __\/  _ \_  __ \/     \ \/ \/ /  |/    \  / __ |
#  /        \|  | (  <_> )  | \/  Y Y  \     /|  |   |  \/ /_/ |
# /_______  /|__|  \____/|__|  |__|_|  /\/\_/ |__|___|  /\____ |
#         \/                         \/               \/      \/
#										  visualbbasic@gmail.com

UNAME_STR ?= $(shell uname)

# detect local ip of host as this is needed within containers to find the openwhisk API container
ifeq ("$(UNAME_STR)","Linux")
	LOCAL_IP=$(shell route | grep default | tr -s " " | cut -d " " -f 8 | xargs /sbin/ifconfig | grep "inet addr:" | cut -d ":" -f 2 | cut -d " " -f 1)
	# inet addr: not present, trying with inet.
	ifeq ($(LOCAL_IP), )
		LOCAL_IP=$(shell route | grep default | tr -s " " | cut -d " " -f 8 | xargs /sbin/ifconfig | grep "inet " | tr -s " " | cut -d " " -f 3)
	endif
else
	LOCAL_IP ?= $(shell ifconfig | grep "inet " | grep -v 127.0.0.1 | cut -d\  -f2 | head -1)
endif
# if no IP was found, fallback to "localhost"
ifeq ($(LOCAL_IP), )
	LOCAL_IP = "localhost"
endif

DIR_PATH := ${CURDIR}/compose
COMPOSE_FILE := ${DIR_PATH}/docker-compose.yml
DOCKER_HOST_IP ?= $(shell echo ${DOCKER_HOST} | grep -o "[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}" || echo ${LOCAL_IP})
DOCKER_REGISTRY ?= ""
DOCKER_IMAGE_PREFIX ?= openwhisk
OPENWHISK_PROJECT_HOME ?= ${DIR_PATH}/openwhisk-master
OPENWHISK_CATALOG_HOME ?= ${DIR_PATH}/openwhisk-catalog
WSK_CLI ?= $(OPENWHISK_PROJECT_HOME)/bin/wsk
OPEN_WHISK_DB_PREFIX ?= local_

DOCKER_KERNEL ?= $(shell docker version --format "{{.Server.KernelVersion}}")
ifeq ("$(UNAME_STR)","Linux")
	DOCKER_BINARY ?= $(shell (which docker))
else
	DOCKER_BINARY ?= $(shell if [[ $(DOCKER_KERNEL) == *-moby || $(DOCKER_KERNEL) ==  *-boot2docker ]]; then (docker run --rm --privileged --pid=host debian nsenter -t 1 -m -u -n -i sh -c "which docker"); else (which docker); fi)
endif
ifndef VERBOSE
.SILENT:
endif

# compose-bootstrap process
# 1. Downloads the OpenWhisk master branch
# 2. Download the wsk cli
# 3. pulls all the Docker images
# 4.
## Bootstraps wsk cli, docker images and run the compose stack
compose-bootstrap: compose-download \
 			 download-cli \
 			 compose-docker_pull \
 			 compose-run \
 			 compose-bootstrap-pause

# Download and adds catalog to the OpenWhisk compose environment
compose-add-catalog: compose-download-catalog \
 					 compose-init-catalog

# Runs the compose OpenWhisk
.PHONY: compose-run
## Starts all the docker-compose resources
compose-run: compose-print-host \
 			 compose-check-required-ports \
 			 compose-setup \
 			 compose-start-docker-compose \
 			 compose-init-couchdb \
 			 compose-init-whisk-cli \
 			 compose-init-api-management

## Build the OpenWhisk docker-compose services
compose-build: compose-build-docker-compose

.PHONY: compose-stop
## Stops all the docker-compose resources
compose-stop:
	$(shell cat ~/tmp/openwhisk/local.env) docker-compose \
			-f ${COMPOSE_FILE} \
	 		--project-name openwhisk stop

.PHONY: compose-rm
## Cleans docker-compose containers and projects
compose-rm:
	echo "cleaning docker-compose containers and projects"
	$(shell cat ~/tmp/openwhisk/local.env) docker-compose \
			-f ${COMPOSE_FILE} \
	 		--project-name openwhisk rm -f

.PHONY: compose-destroy
## Removes all the docker-compose resources
compose-destroy: compose-stop compose-rm
	echo "cleaning other openwhisk containers started by the invoker ... "
	# docker ps | grep whisk | awk '{print $$1}' | xargs docker stop | xargs docker rm
	echo "cleaning dangling docker volumes ... "
	docker volume ls -qf dangling=true | xargs docker volume rm
	if [ "$(UNAME_STR)" = "Linux" ]; then \
	  sudo rm -rf ~/tmp/openwhisk ;\
	else \
	  rm -rf ~/tmp/openwhisk ;\
	fi;
	rm -rf ${DIR_PATH}/openwhisk-master*

.PHONY: compose-ngrok-url
## compose ngrok find url
compose-ngrok-url:
	curl "$(docker port ngrok 4040)/api/tunnels"

.PHONY: compose-api-auth
## Retrieves OpenWhisk API auth details
compose-api-auth:
	echo "Invoking scripts/getAuthPair.sh"
	./scripts/getAuthPair.sh

.PHONY: compose-download
compose-download:
	# Always download the latest openwhisk-master
	rm -rf ${DIR_PATH}/openwhisk-master*
	if [ "$(OPENWHISK_PROJECT_HOME)" = "${DIR_PATH}/openwhisk-master" ]; then \
        echo "Downloading source tar ball...."; \
	    curl -o ${DIR_PATH}/openwhisk-master.tar.gz -L https://github.com/apache/incubator-openwhisk/archive/master.tar.gz; \
        echo "Unpacking tarball."; \
	    mkdir ${DIR_PATH}/openwhisk-master; \
	    tar -xf ${DIR_PATH}/openwhisk-master.tar.gz --strip 1 -C ${DIR_PATH}/openwhisk-master; \
	else \
	     echo "Skipping downloading the code from git as OPENWHISK_PROJECT_HOME is not default:" $(OPENWHISK_PROJECT_HOME); \
	fi

.PHONY: download-catalog
compose-download-catalog:
	# Always download the latest openwhisk-catalog
	rm -rf ${DIR_PATH}/openwhisk-catalog*
	if [ "$(OPENWHISK_CATALOG_HOME)" = "${OPENWHISK_CATALOG_HOME}" ]; then \
	    curl -O ${DIR_PATH}/openwhisk-catalog.tar.gz -L https://api.github.com/repos/apache/incubator-openwhisk-catalog/tarball/master > ${DIR_PATH}/openwhisk-catalog.tar.gz; \
	    mkdir ${DIR_PATH}/openwhisk-catalog; \
	    tar -xf ${DIR_PATH}/openwhisk-catalog.tar.gz --strip 1 -C ${DIR_PATH}/openwhisk-catalog; \
	else \
	     echo "Skipping downloading the code from git as OPENWHISK_CATALOG_HOME is not default:" $(OPENWHISK_CATALOG_HOME); \
	fi

.PHONY: compose-bootstrap-pause
compose-bootstrap-pause:
	echo "waiting for the Whisk invoker to come up ... "
	until $$(curl --output /dev/null --silent --head --fail http://$(DOCKER_HOST_IP):8085/ping); do printf '.'; sleep 5; done
	sleep 30

docker_build:
	echo "building the openwhisk core docker images ... "
	cd $(OPENWHISK_PROJECT_HOME) && \
		./gradlew distDocker -PdockerImagePrefix=$(DOCKER_IMAGE_PREFIX) 

compose-docker_pull:
	echo "pulling the docker images short list... "
	docker pull $(DOCKER_IMAGE_PREFIX)/controller
	docker pull $(DOCKER_IMAGE_PREFIX)/invoker
	docker pull $(DOCKER_IMAGE_PREFIX)/nodejs6action
	docker pull $(DOCKER_IMAGE_PREFIX)/dockerskeleton

docker_pull_full:
	echo "pulling the docker images full list ... "
	docker pull $(DOCKER_IMAGE_PREFIX)/controller
	docker pull $(DOCKER_IMAGE_PREFIX)/invoker
	docker pull $(DOCKER_IMAGE_PREFIX)/nodejs6action
	docker pull $(DOCKER_IMAGE_PREFIX)/action-nodejs-v8
	docker pull $(DOCKER_IMAGE_PREFIX)/python2action
	docker pull $(DOCKER_IMAGE_PREFIX)/python3action
	docker pull $(DOCKER_IMAGE_PREFIX)/action-swift-v3.1.1
	docker pull $(DOCKER_IMAGE_PREFIX)/action-swift-v4.1
	docker pull $(DOCKER_IMAGE_PREFIX)/java8action
	docker pull $(DOCKER_IMAGE_PREFIX)/action-php-v7.1
	docker pull $(DOCKER_IMAGE_PREFIX)/dockerskeleton

download-cli:
	echo "downloading the CLI tool ... "
	if [ "$(OPENWHISK_PROJECT_HOME)" = "${OPENWHISK_PROJECT_HOME}" ]; then \
        if [ "$(UNAME_STR)" = "Darwin" ]; then \
          echo "downloading cli for mac" ; \
          curl -o ${OPENWHISK_PROJECT_HOME}/bin/wsk.zip -L https://github.com/apache/incubator-openwhisk-cli/releases/download/latest/OpenWhisk_CLI-latest-mac-amd64.zip ; \
            cd ${OPENWHISK_PROJECT_HOME}/bin ; \
        unzip -o wsk.zip; \
        elif [ "$(UNAME_STR)" = "Linux" ]; then \
          echo "downloading cli for linux" ; \
          curl -o ${OPENWHISK_PROJECT_HOME}/bin/wsk.tgz -L https://github.com/apache/incubator-openwhisk-cli/releases/download/latest/OpenWhisk_CLI-latest-linux-amd64.tgz ; \
			cd ${OPENWHISK_PROJECT_HOME}/bin ; \
			tar -xf wsk.tgz ; \
        fi; \
	else \
         echo "Skipping downloading the cli from git as OPENWHISK_PROJECT_HOME is not default:" $(OPENWHISK_PROJECT_HOME); \
	fi

.PHONY: compose-print-host
compose-print-host:
	echo "host ip address: ${DOCKER_HOST_IP}"

.PHONY: compose-check-required-ports
compose-check-required-ports:
	echo "checking required ports ... "
	for port in 2888 5984 8085 8888 9092 8001; do \
		pid=`lsof -Pi :$$port -sTCP:LISTEN -t` ; \
		if [ ! -z "$$pid" ];  then echo "$$(tput setaf 1)Port $$port is taken by PID:$$pid.$$(tput sgr0)"; exit 1; fi; \
	done
	echo " ... OK"

.PHONY: compose-check-alarm-ports
compose-check-alarm-ports:
	echo "checking required ports ... "
	for port in 8081; do \
		pid=`lsof -Pi :$$port -sTCP:LISTEN -t` ; \
		if [ ! -z "$$pid" ];  then echo "$$(tput setaf 1)Port $$port is taken by PID:$$pid.$$(tput sgr0)"; exit 1; fi; \
	done
	echo " ... OK"

.PHONY: check-cloudant-ports
check-cloudant-ports:
	echo "checking required ports ... "
	for port in 8082; do \
		pid=`lsof -Pi :$$port -sTCP:LISTEN -t` ; \
		if [ ! -z "$$pid" ];  then echo "$$(tput setaf 1)Port $$port is taken by PID:$$pid.$$(tput sgr0)"; exit 1; fi; \
	done
	echo " ... OK"

.PHONY: check-kafka-ports
check-kafka-ports:
	echo "checking required ports ... "
	for port in 5000; do \
		pid=`lsof -Pi :$$port -sTCP:LISTEN -t` ; \
		if [ ! -z "$$pid" ];  then echo "$$(tput setaf 1)Port $$port is taken by PID:$$pid.$$(tput sgr0)"; exit 1; fi; \
	done
	echo " ... OK"

# Setup compose including OpenWhisk and API gateway
.PHONY: compose-setup
compose-setup:
	mkdir -p ~/tmp/openwhisk
	> ~/tmp/openwhisk/local.env
	printf "DOCKER_BINARY=$(DOCKER_BINARY)\n" >> ~/tmp/openwhisk/local.env
	printf "DOCKER_COMPOSE_HOST=$(DOCKER_HOST_IP)\n" >> ~/tmp/openwhisk/local.env
	printf "DOCKER_REGISTRY=$(DOCKER_REGISTRY)\n" >> ~/tmp/openwhisk/local.env
	printf "DOCKER_IMAGE_PREFIX=$(DOCKER_IMAGE_PREFIX)\n" >> ~/tmp/openwhisk/local.env
	echo "  ... preparing api-gateway configuration"
	rm -rf ~/tmp/openwhisk/api-gateway-config
	mkdir -p ~/tmp/openwhisk/api-gateway-config/api-gateway
	cp -r ${DIR_PATH}/apigateway/* ~/tmp/openwhisk/api-gateway-config/api-gateway/
	cp -r ${DIR_PATH}/apigateway/rclone ~/tmp/openwhisk
	# compose-up OpenWhisk stack
	$(shell cat ~/tmp/openwhisk/local.env) docker-compose \
					-f ${COMPOSE_FILE} \
	 				--project-name openwhisk up minio 2>&1 > ~/tmp/openwhisk/setup.log &
	until (curl --silent http://$(DOCKER_HOST_IP):9001/); do printf '.'; sleep 5; done
	docker run --rm -v ~/tmp/openwhisk/rclone:/root/.config/rclone \
					--link="openwhisk_minio_1:minio.docker" --network=openwhisk_default \
					--entrypoint=rclone openwhisk/apigateway mkdir minio:api-gateway
	docker run --rm -v ~/tmp/openwhisk/api-gateway-config/api-gateway/generated-conf.d:/etc/api-gateway/generated-conf.d \
					-v ~/tmp/openwhisk/rclone:/root/.config/rclone \
					--link="openwhisk_minio_1:minio.docker" --network=openwhisk_default \
					--entrypoint=rclone openwhisk/apigateway copy --exclude .git /etc/api-gateway/ minio:api-gateway/
	# $(shell cat ~/tmp/openwhisk/local.env) docker-compose --project-name openwhisk stop minio
	# $(shell cat ~/tmp/openwhisk/local.env) docker-compose --project-name openwhisk rm -f minio
	$(OPENWHISK_PROJECT_HOME)/ansible/files/genssl.sh $(DOCKER_HOST_IP) server $(OPENWHISK_PROJECT_HOME)/ansible/roles/nginx/files
	mkdir -p ~/tmp/openwhisk/api-gateway-ssl
	cp $(OPENWHISK_PROJECT_HOME)/ansible/roles/nginx/files/*.pem ~/tmp/openwhisk/api-gateway-ssl

.PHONY: gw 
gw:
	$(shell cat ~/tmp/openwhisk/local.env) docker-compose --project-name openwhisk up apigateway

.PHONY: restart
restart: compose-stop \
 		 compose-rm \
 		 compose-start-docker-compose

.PHONY: compose-restart-controller
compose-restart-controller:
	$(shell cat ~/tmp/openwhisk/local.env) docker-compose --project-name openwhisk stop controller
	$(shell cat ~/tmp/openwhisk/local.env) docker-compose --project-name openwhisk rm -f controller
	$(shell cat ~/tmp/openwhisk/local.env) docker-compose --project-name openwhisk up controller 2>&1 >> ~/tmp/openwhisk/docker-compose.log &
	echo "waiting for the controller to see the invoker is 'up' ... "
	until (curl --silent http://$(DOCKER_HOST_IP):8888/invokers | grep "up"); do printf '.'; sleep 5; done

.PHONY: compose-restart-invoker
compose-restart-invoker:
	$(shell cat ~/tmp/openwhisk/local.env) docker-compose --project-name openwhisk stop invoker
	$(shell cat ~/tmp/openwhisk/local.env) docker-compose --project-name openwhisk rm -f invoker
	echo "waiting for the invoker to be marked 'down' ... "
	until (curl --silent http://$(DOCKER_HOST_IP):8888/invokers | grep "down"); do printf '.'; sleep 5; done
	$(shell cat ~/tmp/openwhisk/local.env) docker-compose --project-name openwhisk up invoker 2>&1 >> ~/tmp/openwhisk/docker-compose.log &
	echo "waiting for the invoker to be marked Healthy ... "
	until (curl --silent http://$(DOCKER_HOST_IP):8888/invokers | grep "up"); do printf '.'; sleep 5; done

.PHONY: compose-start-docker-compose
compose-start-docker-compose:
	echo "Testing docker compose file ${COMPOSE_FILE}"
	exit
	$(shell cat ~/tmp/openwhisk/local.env) docker-compose -f ${COMPOSE_FILE} --project-name openwhisk up 2>&1 > ~/tmp/openwhisk/docker-compose.log &

.PHONY: compose-build-docker-compose
compose-build-docker-compose:
	echo "Testing docker compose file ${COMPOSE_FILE}"
	exit
	$(shell cat ~/tmp/openwhisk/local.env) docker-compose -f ${COMPOSE_FILE} --project-name openwhisk build

.PHONY: compose-init-couchdb
compose-init-couchdb:
	echo "waiting for the database to come up ... on $(DOCKER_HOST_IP)"
	until $$(curl --output /dev/null --silent --head --fail http://$(DOCKER_HOST_IP):5984/_all_dbs); do printf '.'; sleep 5; done
	echo "initializing the database ... on $(DOCKER_HOST_IP)"
	# make sure the src files are in a shared folder for docker
	mkdir -p ~/tmp/openwhisk
	rm -rf ~/tmp/openwhisk/src
	rsync -a $(OPENWHISK_PROJECT_HOME)/* ~/tmp/openwhisk/src --exclude .git --exclude build --exclude tests
	echo 'Setting up db using ansible container....'; \
    compose_network=$$(docker-compose \
     	-f ${COMPOSE_FILE} \
     	--project-name openwhisk ps -q db 2>/dev/null | xargs docker inspect -f '{{range $$index, $$element := .NetworkSettings.Networks}}{{$$index}}{{end}}' | head -n 1); \
    db_ip_address=$$(docker-compose \
     	-f ${COMPOSE_FILE} \
     	--project-name openwhisk ps -q db 2>/dev/null | xargs docker inspect -f "{{.NetworkSettings.Networks.$${compose_network}.IPAddress}}"); \
	docker run --rm -v ~/tmp/openwhisk/src:/openwhisk -w /openwhisk/ansible \
		--network="$${compose_network}" -t \
		--add-host="db:$${db_ip_address}" \
		ddragosd/ansible:2.4.0.0-debian8  \
		sh -c "ansible-playbook setup.yml && ansible-playbook couchdb.yml --tags=ini && ansible-playbook initdb.yml wipe.yml \
			-e db_host=db -e openwhisk_home=/openwhisk -e db_prefix=$(OPEN_WHISK_DB_PREFIX)"

.PHONY: compose-init-whisk-cli
compose-init-whisk-cli:
	echo "waiting for the Whisk controller to come up ... "
	until $$(curl --output /dev/null --silent --head --fail http://$(DOCKER_HOST_IP):8888/ping); do printf '.'; sleep 5; done
	echo "initializing CLI ... "
	$(WSK_CLI) -v property set --namespace guest --auth `cat $(OPENWHISK_PROJECT_HOME)/ansible/files/auth.guest` --apihost https://$(DOCKER_HOST_IP) -i

.PHONY: compose-init-api-management
compose-init-api-management:
	$(WSK_CLI) -v property set --namespace whisk.system --auth `cat $(OPENWHISK_PROJECT_HOME)/ansible/files/auth.whisk.system` --apihost $(DOCKER_HOST_IP) -i
	GW_USER="" GW_PWD="" GW_HOST_V2="http://$(DOCKER_HOST_IP):9000/v2" OPENWHISK_HOME=$(OPENWHISK_PROJECT_HOME) \
		$(OPENWHISK_PROJECT_HOME)/ansible/roles/routemgmt/files/installRouteMgmt.sh $(shell cat $(OPENWHISK_PROJECT_HOME)/ansible/files/auth.whisk.system) $(DOCKER_HOST_IP) /whisk.system $(WSK_CLI)
	$(WSK_CLI) -v property set --namespace guest --auth `cat $(OPENWHISK_PROJECT_HOME)/ansible/files/auth.guest` --apihost $(DOCKER_HOST_IP) -i

.PHONY: compose-init-catalog
compose-init-catalog:
	OPENWHISK_HOME=$(OPENWHISK_PROJECT_HOME) $(OPENWHISK_CATALOG_HOME)/packages/installCatalog.sh \
	  `cat $(OPENWHISK_PROJECT_HOME)/ansible/files/auth.whisk.system` \
	  $(DOCKER_HOST_IP):443 \
	  $(WSK_CLI)

.PHONY: pull
pull: download setup
	docker-compose --project-name openwhisk pull

# Optional package configuration stages. These commands will install and set up
# the alarms, cloudant and kakfa packages.
PACKAGES = alarms cloudant kafka
PACKAGE_ALARMS_HOME ?= ./openwhisk-package-alarms
PACKAGE_CLOUDANT_HOME ?= ./openwhisk-package-cloudant
PACKAGE_KAFKA_HOME ?= ./openwhisk-package-kafka
CREATE_PACKAGE_STEPS = download-package- install-package- start-provider-

create-provider-alarms: compose-check-alarm-ports compose-setup-providers $(addsuffix alarms,$(CREATE_PACKAGE_STEPS))

create-provider-cloudant: check-cloudant-ports compose-setup-providers $(addsuffix cloudant,$(CREATE_PACKAGE_STEPS))

create-provider-kafka: check-kafka-ports compose-setup-providers $(addsuffix kafka,$(CREATE_PACKAGE_STEPS))

.PHONY: compose-setup-providers
compose-setup-providers:
	printf "OPENWHISK_HOME=$(realpath $(OPENWHISK_PROJECT_HOME))\n" > ~/tmp/openwhisk/providers.env
	printf "ENDPOINT_AUTH=`cat $(realpath $(OPENWHISK_PROJECT_HOME))/ansible/files/auth.whisk.system`\n" >> ~/tmp/openwhisk/providers.env
	printf "HOST_MACHINE=$(DOCKER_HOST_IP)\n" >> ~/tmp/openwhisk/providers.env
	printf "DOCKER_COMPOSE_HOST=$(DOCKER_HOST_IP)\n" >> ~/tmp/openwhisk/providers.env

.PHONY: $(addprefix download-package-,$(PACKAGES))
$(addprefix download-package-,$(PACKAGES)):
	$(eval PACKAGE_NAME:= $(shell echo $(@) | cut -b 18-))
	$(eval PACKAGE_HOME := $(PACKAGE_$(shell echo $(PACKAGE_NAME) |  tr 'a-z' 'A-Z')_HOME))
	echo "Downloading package" $(PACKAGE_NAME) "into" $(PACKAGE_HOME)
	rm -rf ./openwhisk-package-$(PACKAGE_NAME)*
	if [ "$(PACKAGE_HOME)" = "./openwhisk-package-$(PACKAGE_NAME)" ]; then \
	    curl -O ./openwhisk-package-$(PACKAGE_NAME).tar.gz -L https://api.github.com/repos/apache/incubator-openwhisk-package-$(PACKAGE_NAME)/tarball/master > ./openwhisk-package-$(PACKAGE_NAME).tar.gz; \
	    mkdir openwhisk-package-$(PACKAGE_NAME); \
	    tar -xf ./openwhisk-package-$(PACKAGE_NAME).tar.gz --strip 1 -C openwhisk-package-$(PACKAGE_NAME); \
	else \
	     echo "Skipping downloading the code from git as PACKAGE_HOME is not default:" $(PACKAGE_HOME); \
	fi

.PHONY: $(addprefix install-package-,$(PACKAGES))
$(addprefix install-package-,$(PACKAGES)):
	$(eval PACKAGE_NAME:= $(shell echo $(@) | cut -b 17-))
	$(eval PACKAGE_HOME := $(PACKAGE_$(shell echo $(PACKAGE_NAME) |  tr 'a-z' 'A-Z')_HOME))
	cd $(PACKAGE_HOME) && \
	$(shell cat ~/tmp/openwhisk/providers.env) ./installCatalog.sh $(realpath $(OPENWHISK_PROJECT_HOME))/ansible/files/auth.whisk.system $(DOCKER_HOST_IP) "http://$(DOCKER_HOST_IP):5984" $(OPEN_WHISK_DB_PREFIX) $(DOCKER_HOST_IP)

.PHONY: $(addprefix start-provider-,$(PACKAGES))
$(addprefix start-provider-,$(PACKAGES)):
	$(eval PACKAGE_NAME:= $(shell echo $(@) | cut -b 16-))
	$(shell cat ~/tmp/openwhisk/providers.env) docker-compose -f docker-compose.yml -f docker-compose.packages.yml --project-name openwhisk up --no-recreate $(PACKAGE_NAME)provider 2>&1 > ~/tmp/openwhisk/docker-provider-compose.log &

.PHONY: $(addprefix stop-provider-,$(PACKAGES))
$(addprefix stop-provider-,$(PACKAGES)): setup-providers
	$(eval PACKAGE_NAME:= $(shell echo $(@) | cut -b 15-))
	$(shell cat ~/tmp/openwhisk/providers.env) docker-compose -f docker-compose.yml -f docker-compose.packages.yml --project-name openwhisk stop $(PACKAGE_NAME)provider
