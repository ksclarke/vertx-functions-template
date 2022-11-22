
#
# Builds a Vert.x application for an Alpine Docker image
#

ARG ALPINE_TAG
ARG BUILDER_TAG
ARG WATCHDOG_TAG

# Set up the OpenFaas of-watchdog source container
FROM ghcr.io/openfaas/of-watchdog:${WATCHDOG_TAG} as watchdog

# Set up a build environment to compile the application
FROM maven-graalvm-musl:${BUILDER_TAG} AS builder

ARG USE_UPX

# Hard-coded arguments to the build
ARG PARENT_DIR="/opt/${project.parent.artifactId}"
ARG PROJECT_DIR="${PARENT_DIR}/${project.artifactId}"
ARG LIBS_DIR="src/main/docker/libs"
ARG POM_NS="http://maven.apache.org/POM/4.0.0"

# Copy our Maven directory into the container
WORKDIR "${PROJECT_DIR}"
COPY "/maven/" .

# Setup the parent project for our build
WORKDIR "${PARENT_DIR}"
RUN xmlstarlet ed -N pom="${POM_NS}" -d "//pom:modules/pom:module[text()!='${project.artifactId}']" \
      "${PROJECT_DIR}/${LIBS_DIR}/.flattened-pom.xml" > "pom.xml"

# Setup the builder project
WORKDIR "${PROJECT_DIR}"
RUN xmlstarlet ed -N pom="${POM_NS}" -u "//pom:parent/pom:version" -v "${project.version}" \
    "pom.xml" > "/tmp/pom.xml" && mv "/tmp/pom.xml" "pom.xml" && \
    mvn -ntp install:install-file -Dfile="pom.xml" -DgeneratePom="false" -DgroupId="${project.parent.groupId}" \
      -DartifactId="${project.parent.artifactId}" -DpomFile="pom.xml" -Dpackaging="pom"

# Install the builder project's local dependencies
RUN mvn -ntp install:install-file -Dfile="${LIBS_DIR}/vertx-functions-core-${project.version}.jar" -Dpackaging="jar" \
      -DgroupId="info.freelibrary" -DartifactId="vertx-functions-core" -Dversion="${project.version}" && \
    mvn -ntp install:install-file -Dfile="${LIBS_DIR}/vertx-functions-example-0.0.1-SNAPSHOT.jar" -Dpackaging="jar" \
      -DgroupId="info.freelibrary" -DartifactId="vertx-functions-example" -Dversion="0.0.1-SNAPSHOT"

RUN ls -Rl /root/.m2/repository/info/freelibrary

# Build project using GraalVM's native-image
RUN mvn -ntp -B package -DskipBuild="false" -DlogLevel="DEBUG" -DtestLogLevel="DEBUG" -Drevision="${revision}" && \
  if [ "${USE_UPX}" = true ] ; then \
    upx --best -q "${PROJECT_DIR}/target/bootstrap" ; \
  fi

# Set up the image to which to copy our build artifacts
FROM alpine:${ALPINE_TAG}

ENV javax.net.ssl.trustStore /etc/default/cacerts
ENV javax.net.ssl.trustAnchors /etc/default/cacerts

# Application configuration
ENV OPENAPI_SPEC openapi.yaml

# The location of our project directory inside the build container
ARG PROJECT_DIR="/opt/${project.parent.artifactId}/${project.artifactId}"

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
RUN chmod +x /usr/bin/fwatchdog

COPY --from=builder ${PROJECT_DIR}/target/bootstrap /usr/bin/bootstrap
COPY --from=builder /etc/default/cacerts /etc/default/cacerts

# Set some environmental properties used by the watchdog wrapper
ENV upstream_url="http://127.0.0.1:8888"
ENV mode="http"
ENV fprocess="/usr/bin/bootstrap"

# Setup our system user and their home directory
WORKDIR "/home/${project.artifactId}"
RUN addgroup --system "${project.artifactId}" \
  && adduser --system --ingroup "${project.artifactId}" "${project.artifactId}" \
  && chown -R "${project.artifactId}" "/home/${project.artifactId}"
USER "${project.artifactId}"

# This is the default outward facing port that OpenFaaS expects
EXPOSE 8080

# How do we tell if the container is still up and functioning as expected?
HEALTHCHECK --interval=5s CMD [ -e /tmp/.lock ] || exit 1

# The watchdog wrapper hands off incoming requests to vertx-functions
CMD ["fwatchdog"]
