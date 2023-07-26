# vertx-functions-template

This project is a playground for some ideas I'm interested in investigating. I'm not sure there is any value here for
anyone else yet, but feel free to poke around if you want.

As a very basic orientation, this project is split into three parts: the core (named `vertx-functions-core`), the
function template (named `vertx-functions-function`), and the GraalVM builder (named `vertx-functions-builder`). The
end result of a build is a Docker container with a Vert.x-based function that can be deployed into an OpenFaaS server.

To create your own function, you should modify the contents of `vertx-functions-function`. The key parts to modify are
the `src/main/resources/openapi.yaml` and the code in `src/main/java`. Once you have an implementation of a Vert.x
`Handler<RoutingContext>` in `src/main/java`, you should put its full class name in the `openapi.yaml` document as an
endpoint's `operationId`. See the existing openapi.yaml for an example.

In addition, your handler will need to be included in the [reflection-config.json](vertx-functions-function/src/main/resources/META-INF/native-image/info.freelibrary/vertx-functions-function/reflection-config.json) file. You will see examples in that
file for guidance, including the example project's pre-existing Echo handler.

The existing build should work with your function unless you add a dependency that uses reflection and isn't currently
handled in the build's GraalVM configuration. How to configure GraalVM for new reflection is beyond this simple readme.

## Prerequisites

The following must be installed prior to building this project:

* JDK >= 17
* Maven >= 3.6.3
* Docker >= 20.10.1

## Build the Project

To build the Docker image that's produced by this project, simply run:

    mvn verify

This will build the project, but the function that's created will be the generically named `vertx-functions-example`.
It can be renamed using the following:

    mvn -Ptemplate initialize -Dfunction.artifact="vertx-functions-test" -Dfunction.group="edu.ucla.library"

You'd change the values to whatever you want, of course. After that, the `mvn verify` command can be run again and the
project will be rebuilt.

## Deploy the Project

    Something, something...

## Contact Information

If you notice something that is broken or that needs fixing, please submit a ticket to the project's [issues queue](https://github.com/ksclarke/vertx-functions-template/issues). If you have a question or a general comment about the project, please use the project's [discussion board](https://github.com/ksclarke/vertx-functions-template/discussions).
