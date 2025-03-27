#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export BROWSER=${1:-CHROME}

export ALLURE_DOCKER_API=http://allure:5050/
export EXECUTION_TYPE="docker build"
export BUILD_URL="https%3A%2F%2Fgithub.com%2Fromketa%2Frococo"
export HEAD_COMMIT_MESSAGE="Executed via shell script in docker"
export ARCH=$(uname -m)

echo '### Java version ###'
java --version

docker_container=rococo-tests
docker_image=PREFIX/rococo-tests:latest

if [ ! -z "$docker_container" ]; then
  echo "### Stop container with tests: $docker_container ###"
  docker stop $docker_container
  docker rm $docker_container
fi

if [ ! -z "$docker_image" ]; then
  echo "### Remove image tests: $docker_image ###"
  docker rmi $docker_image
fi

echo '### Java version ###'
java --version
bash ./gradlew clean
bash ./gradlew jibDockerBuild -x :rococo-tests:test

docker pull selenoid/vnc_chrome:127.0
docker pull selenoid/firefox:125.0
docker compose -f docker-compose.test.yml up -d
docker ps -a
