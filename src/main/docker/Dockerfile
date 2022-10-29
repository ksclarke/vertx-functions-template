
#
# Builds a Vert.x application for an Alpine Docker image
#

ARG ALPINE_TAG
ARG BUILDER_TAG
ARG WATCHDOG_TAG

# Set up the OpenFaas of-watchdog source container
FROM ghcr.io/openfaas/of-watchdog:${WATCHDOG_TAG} as watchdog

# Set up a build environment to compile the application
FROM ksclarke/maven-graalvm-musl:${BUILDER_TAG} AS builder

ARG PROJECT_NAME
ARG USE_UPX

# Run the Maven build
WORKDIR /opt/${PROJECT_NAME}
COPY /maven/ .

RUN mvn -ntp -B package -DskipBuild=false -DlogLevel=DEBUG -DtestLogLevel=DEBUG && \
  if [ "${USE_UPX}" = true ] ; then \
    upx --best -q /opt/${PROJECT_NAME}/target/bootstrap ; \
  fi

# Set up the image to which to copy our build artifacts
FROM alpine:${ALPINE_TAG}

ENV javax.net.ssl.trustStore /etc/default/cacerts
ENV javax.net.ssl.trustAnchors /etc/default/cacerts

# Application configuration
ENV OPENAPI_SPEC openapi.yaml

# We use project.artifactId (i.e. PROJECT_NAME) for many different purposes
ARG PROJECT_NAME

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
RUN chmod +x /usr/bin/fwatchdog

COPY --from=builder /opt/${PROJECT_NAME}/target/bootstrap /usr/bin/bootstrap
COPY --from=builder /etc/default/cacerts /etc/default/cacerts

# Set some environmental properties used by the watchdog wrapper
ENV upstream_url="http://127.0.0.1:8888"
ENV mode="http"
ENV fprocess="/usr/bin/bootstrap"

# Setup our system user and their home directory
WORKDIR "/home/${PROJECT_NAME}"
RUN addgroup --system "${PROJECT_NAME}" \
  && adduser --system --ingroup "${PROJECT_NAME}" "${PROJECT_NAME}" \
  && chown -R "${PROJECT_NAME}" "/home/${PROJECT_NAME}"
USER "${PROJECT_NAME}"

# This is the default outward facing port that OpenFaaS expects
EXPOSE 8080

# How do we tell if the container is still up and functioning as expected?
HEALTHCHECK --interval=5s CMD [ -e /tmp/.lock ] || exit 1

# The watchdog wrapper hands off incoming requests to vertx-native-functions
CMD ["fwatchdog"]
