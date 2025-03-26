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

docker compose down
docker_containers=$(docker ps -a -q)
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'rococo')

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

if [ ! -z "$docker_images" ]; then
  echo "### Remove images: $docker_images ###"
  docker rmi $docker_images
fi

echo '### Java version ###'
java --version
bash ./gradlew clean
bash ./gradlew jibDockerBuild -x :rococo-tests:test

docker pull selenoid/vnc_chrome:127.0
docker pull selenoid/firefox:125.0
docker compose up -d
docker ps -a
