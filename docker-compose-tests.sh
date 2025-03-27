#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export BROWSER="CHROME" # default browser

export ALLURE_DOCKER_API=http://allure:5050/
export EXECUTION_TYPE="docker build"
export BUILD_URL="https%3A%2F%2Fgithub.com%2Fromketa%2Frococo"
export HEAD_COMMIT_MESSAGE="Executed via shell script in docker"
export ARCH=$(uname -m)

# flags
TESTS_ONLY=false
ROCOCO_TESTS_ONLY=false

# check is that first arg is a browser
if [[ "$1" =~ ^(CHROME|FIREFOX|EDGE|OPERA)$ ]]; then
  export BROWSER="$1"
  shift # remove from list of args
fi

# check other args
for arg in "$@"; do
  case $arg in
    --tests-only)
      TESTS_ONLY=true
      ;;
    --rococo-tests-only)
      ROCOCO_TESTS_ONLY=true
      ;;
    *)
      echo "Unexpected parameter: $arg"
      exit 1
      ;;
  esac
done

echo "Selected browser: $BROWSER"

echo '### Java version ###'
java --version

if $ROCOCO_TESTS_ONLY; then
  echo "### Start only rococo-tests container with tests ###"
  docker compose rm -sf rococo-tests
  docker compose up -d --build rococo-tests

elif $TESTS_ONLY; then
  echo "### Remove and start all test related containers (rococo-tests, selenoid, allure) ###"
  docker compose rm -sf rococo-tests selenoid selenoid-ui allure allure-ui
  docker compose up -d --build rococo-tests selenoid selenoid-ui allure allure-ui

else
  echo "### Remove and start all containers ###"
  docker compose down
  docker_containers=$(docker ps -a -q)
  docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'rococo')

if [ ! -z "$docker_containers" ]; then
    echo "### Stop and removing containers: $docker_containers ###"
    docker stop $docker_containers
    docker rm $docker_containers
  fi

  if [ ! -z "$docker_images" ]; then
    echo "### Removing images: $docker_images ###"
    docker rmi $docker_images
  fi

  bash ./gradlew clean
  bash ./gradlew jibDockerBuild -x :rococo-tests:test

  docker pull selenoid/vnc_chrome:127.0
  docker pull selenoid/firefox:125.0

  echo "### Запуск всех контейнеров ###"
  docker compose up -d
fi
