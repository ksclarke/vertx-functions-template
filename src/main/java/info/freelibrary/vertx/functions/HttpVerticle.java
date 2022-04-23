
package info.freelibrary.vertx.functions;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.openapi.RouterBuilder;

/**
 * A default application verticle.
 */
public class HttpVerticle extends AbstractVerticle {

    /** The HTTP server's logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpVerticle.class, MessageCodes.BUNDLE);

    /** The location of the OpenAPI specification. */
    private static final String API_SPEC = "vertx-functions.yaml";

    @Override
    public void start(final Promise<Void> aPromise) {
        ConfigRetriever.create(vertx).getConfig().compose(config -> {
            return RouterBuilder.create(vertx, API_SPEC).compose(new Routes()).compose(new Server(vertx, config));
        }).onSuccess(aPromise::complete).onFailure(aPromise::fail);
    }

    @SuppressWarnings("uncommentedmain")
    public static void main(final String[] aArgsArray) {
        // aArgsArray will contain information about verticles to deploy

        // Deploy the main verticle that responds to incoming requests
        Vertx.vertx().deployVerticle(new HttpVerticle(), deployment -> {
            if (deployment.failed()) {
                final Throwable error = deployment.cause();
                LOGGER.error(error, MessageCodes.VNF_002, error.getMessage());
            }
        });
    }

}
