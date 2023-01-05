
package info.freelibrary.vertx.functions;

import static info.freelibrary.util.Constants.INADDR_ANY;

import info.freelibrary.util.Env;
import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.RouterBuilder;

/**
 * A default verticle that runs our functions.
 */
public class HttpVerticle extends AbstractVerticle {

    /** The logger class name property used to configure which logger implementation should be used. */
    public static final String LOGGER_CLASS_NAME = "vertx.logger-delegate-factory-class-name";

    /** The application's base cache directory. */
    public static final String CACHE_DIRECTORY = "vertx.cacheDirBase";

    /** The configuration file for the application's loggers. */
    public static final String LOGGING_CONFIG = "logback.configurationFile";

    /** The HTTP server's logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpVerticle.class, MessageCodes.BUNDLE);

    @Override
    public void start(final Promise<Void> aPromise) {
        final HttpServerOptions serverOpts = new HttpServerOptions().setCompressionSupported(true);
        final String apiConfig = Env.get(Configs.OPENAPI_SPEC, "target/classes/openapi.yaml");
        final int port = Env.get(Configs.HTTP_PORT, 8888); // Port only set in local testing, not in Docker container
        final Vertx vertx = getVertx();

        RouterBuilder.create(vertx, apiConfig).onSuccess(builder -> {
            final Router router = new OperationInitializer(builder).getRouter();

            vertx.createHttpServer(serverOpts).requestHandler(router).listen(port, INADDR_ANY).onSuccess(server -> {
                LOGGER.info(MessageCodes.VFC_001, server.actualPort());
                aPromise.complete();
            }).onFailure(aPromise::fail);
        }).onFailure(details -> aPromise.fail(new ConfigurationException(details, MessageCodes.VFC_003, apiConfig)));
    }

    /**
     * Runs the verticle as the main program.
     *
     * @param aArgsArray An array of arguments
     */
    @SuppressWarnings("uncommentedmain")
    public static void main(final String[] aArgsArray) {
        // We pre-configure all this because we control the container this is running in
        System.setProperty(CACHE_DIRECTORY, "/tmp/.vertx-cache");
        System.setProperty(LOGGING_CONFIG, "logback.xml");
        System.setProperty(LOGGER_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());

        // This (below) is needed for the serialization / encoding of certain JsonObject(s)
        // DatabindCodec.mapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // Deploy the main verticle that responds to incoming requests
        Vertx.vertx().deployVerticle(new HttpVerticle()).onFailure(error -> {
            LOGGER.error(error, MessageCodes.VFC_002, error.getMessage());
        });
    }
}
