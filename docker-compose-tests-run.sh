#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"

export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

echo '### Java version ###'
java --version

docker_container=rococo-tests
docker_image=romketa/rococo-tests:latest

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
#bash ./gradlew clean
#bash ./gradlew jibDockerBuild -x :rococo-tests:test

#docker pull selenoid/vnc_chrome:127.0
docker compose -f docker-compose.test.yml up -d
docker ps -a
