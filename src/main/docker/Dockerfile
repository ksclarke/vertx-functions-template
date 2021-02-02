
#
# Builds a Vert.x application for an Alpine Docker image
#

ARG ALPINE_TAG
ARG BUILDER_TAG

# Set up a build environment to compile the application
FROM ksclarke/maven-graalvm-musl:${BUILDER_TAG} AS builder

ARG PROJECT_NAME
ARG USE_UPX

# Run the Maven build
WORKDIR /opt/${PROJECT_NAME}
COPY /maven/ .
RUN mvn -ntp -q -B package -DskipBuild=false && \
    if [ "${USE_UPX}" = true ] ; then \
      upx --best -q /opt/${PROJECT_NAME}/target/bootstrap ; \
    fi

# Set up the image to which to copy our build artifacts
FROM alpine:${ALPINE_TAG}

ENV javax.net.ssl.trustStore /etc/default/cacerts
ENV javax.net.ssl.trustAnchors /etc/default/cacerts

ARG PROJECT_NAME

COPY --from=builder /opt/${PROJECT_NAME}/target/bootstrap /usr/bin/bootstrap
COPY --from=builder /etc/default/cacerts /etc/default/cacerts

ENTRYPOINT ["tail", "-f", "/dev/null"]
