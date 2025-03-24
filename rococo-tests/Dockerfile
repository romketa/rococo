# ARG DOCKER
ARG ALLURE_DOCKER_API
ARG GITHUB_TOKEN
ARG BUILD_URL
ARG HEAD_COMMIT_MESSAGE
ARG EXECUTION_TYPE

#
# FROM ${DOCKER}
#

FROM eclipse-temurin:21-jdk

ENV ALLURE_DOCKER_API=${ALLURE_DOCKER_API}
ENV GITHUB_TOKEN=${GITHUB_TOKEN}
ENV BUILD_URL=${BUILD_URL}
ENV HEAD_COMMIT_MESSAGE=${HEAD_COMMIT_MESSAGE}
ENV EXECUTION_TYPE=${EXECUTION_TYPE}

WORKDIR /rococo
COPY ./gradle ./gradle
COPY ./rococo-tests ./rococo-tests
COPY ./rococo-grpc-common ./rococo-grpc-common
COPY ./gradlew ./
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

RUN chmod +x gradlew

CMD ./gradlew test -Dtest.env=docker -Duser.timezone=Europe/Moscow
