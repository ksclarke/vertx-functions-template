
#
# Builds our OpenFaaS function into a Vert.x application the runs in an Alpine Docker image.
#

ARG ALPINE_TAG
ARG BUILDER_TAG
ARG WATCHDOG_TAG

# Set up the OpenFaas of-watchdog source container
FROM ghcr.io/openfaas/of-watchdog:${WATCHDOG_TAG} as watchdog

# Set up a build environment to compile the application
FROM ksclarke/maven-graalvm-musl:${BUILDER_TAG} AS builder

ARG USE_UPX

# Hard-coded arguments to the build
ARG PARENT_DIR="/opt/${project.parent.artifactId}"
ARG PROJECT_DIR="${PARENT_DIR}/${project.artifactId}"
ARG LIBS_DIR="src/main/docker/libs"
ARG SRC_DIR="src/main/docker/src"
ARG POM_NS="http://maven.apache.org/POM/4.0.0"

# Copy our Maven directory into the container
WORKDIR "${PROJECT_DIR}"
COPY "/maven/" .

# Compile jsmin and put it on our path
RUN gcc -o "/usr/bin/jsmin" "${SRC_DIR}/jsmin.c"

# Setup the parent project for our build
WORKDIR "${PARENT_DIR}"
RUN xmlstarlet ed -N pom="${POM_NS}" -d "//pom:modules/pom:module[text()!='${project.artifactId}']" \
  "${PROJECT_DIR}/${LIBS_DIR}/.flattened-pom.xml" > "pom.xml"

# Setup the builder project
WORKDIR "${PROJECT_DIR}"
RUN xmlstarlet ed -N pom="${POM_NS}" -u "//pom:parent/pom:version" -v "${project.version}" "pom.xml" > "/tmp/pom.xml" \
  && mv "/tmp/pom.xml" "pom.xml" \
  && mvn -ntp install:install-file -Dfile="pom.xml" -DgeneratePom="false" -DgroupId="${project.parent.groupId}" \
    -DartifactId="${project.parent.artifactId}" -DpomFile="pom.xml" -Dpackaging="pom"

# Install the builder project's local dependencies
RUN mvn -ntp install:install-file -Dfile="${LIBS_DIR}/vertx-functions-core-${project.version}.jar" -Dpackaging="jar" \
    -DgroupId="info.freelibrary" -DartifactId="vertx-functions-core" -Dversion="${project.version}" \
  && mvn -ntp install:install-file -Dfile="${LIBS_DIR}/${module.artifact}-${module.version}.jar" \
    -DgroupId="${module.group}" -DartifactId="${module.artifact}" -Dversion="${module.version}" -Dpackaging="jar"

# Pull out resource bundles for re-packaging
# RUN jar tf "${LIBS_DIR}/${module.artifact}-${module.version}.jar"

RUN for RESOURCE in $(cat "src/main/resources/META-INF/native-image/info.freelibrary/vertx-functions-function/resource-config.json" | \
      jq '.bundles[].name') ; do \
        echo "$RESOURCE" ; \
      done
#  && echo | && echo | \
  # && jar tf "${LIBS_DIR}/vertx-functions-core-${project.version}.jar" \
#  && exit 1

# Build project using GraalVM's native-image -- turn off UPX to see what's wrong if you get a core dump
RUN mvn -ntp -B package -DskipBuild="false" -DlogLevel="DEBUG" -DtestLogLevel="DEBUG" -Drevision="${revision}" \
    -Dfunction.version="${module.version}" \
  && if [ "${USE_UPX}" = true ] ; then upx --lzma --best -q "${PROJECT_DIR}/target/${module.artifact}" ; fi

RUN ls -l "${PROJECT_DIR}/target/${module.artifact}"

# Set up the image to which to copy our build artifacts
FROM alpine:${ALPINE_TAG}

ENV javax.net.ssl.trustStore /etc/default/cacerts
ENV javax.net.ssl.trustAnchors /etc/default/cacerts

# Application configuration
ENV OPENAPI_SPEC openapi.yaml

# The location of our project directory inside the build container
ARG PROJECT_DIR="/opt/${project.parent.artifactId}/${project.artifactId}"

COPY --from=watchdog "/fwatchdog" "/usr/bin/fwatchdog"
RUN chmod +x "/usr/bin/fwatchdog"

COPY --from=builder "${PROJECT_DIR}/target/${module.artifact}" "/usr/bin/${module.artifact}"
COPY --from=builder "/etc/default/cacerts" "/etc/default/cacerts"

# Set some environmental properties used by the watchdog wrapper
ENV upstream_url="http://127.0.0.1:8888"
ENV mode="http"
ENV fprocess="/usr/bin/${module.artifact}"

# Setup our system user and their home directory
WORKDIR "/home/${module.artifact}"
RUN addgroup --system "${module.artifact}" \
  && adduser --system --ingroup "${module.artifact}" "${module.artifact}" \
  && chown -R "${module.artifact}" "/home/${module.artifact}"
USER "${module.artifact}"

# This is the default outward facing port that OpenFaaS expects
EXPOSE 8080

# How do we tell if the container is still up and functioning as expected?
HEALTHCHECK --interval=5s CMD [ -e /tmp/.lock ] || exit 1

# The watchdog wrapper hands off incoming requests to vertx-functions
CMD ["fwatchdog"]
