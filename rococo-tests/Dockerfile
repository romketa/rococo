ARG ALLURE_DOCKER_API
ARG BUILD_URL
ARG HEAD_COMMIT_MESSAGE
ARG EXECUTION_TYPE
ARG BROWSER

FROM eclipse-temurin:21-jdk

ENV ALLURE_DOCKER_API=${ALLURE_DOCKER_API}
ENV BUILD_URL=${BUILD_URL}
ENV HEAD_COMMIT_MESSAGE=${HEAD_COMMIT_MESSAGE}
ENV EXECUTION_TYPE=${EXECUTION_TYPE}
ENV BROWSER=${BROWSER}

WORKDIR /rococo
COPY ./gradle ./gradle
COPY ./rococo-tests ./rococo-tests
COPY ./rococo-grpc-common ./rococo-grpc-common
COPY ./gradlew ./
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

RUN chmod +x gradlew

CMD ./gradlew test --tests "guru.qa.rococo.test.web.ProfileTest.shouldFillEmptyUser" -Dtest.env=docker -Dbrowser=${BROWSER} -Duser.timezone=Europe/Moscow
