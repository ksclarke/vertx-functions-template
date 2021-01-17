
ARG GRAALVM_TAG
ARG ALPINE_TAG

# Set up a build environment to compile the application
FROM oracle/graalvm-ce:${GRAALVM_TAG} AS builder

ARG GRAALVM_HOME
ARG PROJECT_NAME
ARG MAVEN_VERSION
ARG MAVEN_CHECKSUM

# Configure Maven build properties
ENV M2_HOME=/opt/maven
ENV MAVEN_HOME=${M2_HOME}
ENV PATH=${MAVEN_HOME}/bin:${PATH}

WORKDIR ${MAVEN_HOME}

# Install Maven
RUN MAVEN_BASE_URL="https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries/" \
    && MAVEN_DOWNLOAD="/tmp/apache-maven.tar.gz" \
    && curl -s -o ${MAVEN_DOWNLOAD} ${MAVEN_BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    # Check the download's SHA512 (the double space in the `echo` is necessary)
    && echo "${MAVEN_CHECKSUM}  ${MAVEN_DOWNLOAD}" | sha512sum -c - \
    && tar -xzf ${MAVEN_DOWNLOAD} -C ${MAVEN_HOME} --strip-components=1

# Install GraalVM's native-image
RUN ${GRAALVM_HOME}/bin/gu install native-image

# Install musl-gcc through the forensics package repo
RUN curl -s -o cert-forensics-tools-release-el7.rpm https://forensics.cert.org/cert-forensics-tools-release-el7.rpm \
    && rpm -Uvh cert-forensics-tools-release*rpm \
    && yum -y --enablerepo=forensics install musl-gcc

# Run the Maven build
WORKDIR /opt/${PROJECT_NAME}
COPY /maven/ .
RUN mvn -ntp -q -B package -DskipBuild=false

# Set up the image to which to copy our build artifacts
FROM ksclarke/alpine-glibc:latest
#FROM alpine:latest

LABEL maintainer="Kevin S. Clarke <ksclarke@ksclarke.io>"

ENV javax.net.ssl.trustStore /usr/share/misc/cacerts
ENV javax.net.ssl.trustAnchors /usr/share/misc/cacerts

ARG GRAALVM_HOME
ARG PROJECT_NAME

COPY --from=builder /opt/${PROJECT_NAME}/target/bootstrap /usr/bin/bootstrap
COPY --from=builder ${GRAALVM_HOME}/lib/security/cacerts /usr/share/misc/cacerts

ENTRYPOINT ["tail", "-f", "/dev/null"]
