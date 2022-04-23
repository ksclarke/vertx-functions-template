
package info.freelibrary.vertx.functions;

import static info.freelibrary.util.Constants.INADDR_ANY;

import java.util.function.Function;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * A function server.
 */
final class Server implements Function<Router, Future<Void>> {

    /** The server's logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class, MessageCodes.VNF_002);

    /** Port within the container at which the server runs. */
    private static final int HTTP_PORT = 8888;

    /** An underlying Vert.x instance. */
    private final Vertx myVertx;

    /** An application configuration. */
    private final JsonObject myConfig;

    /**
     * Creates a new function server.
     *
     * @param aVertx A Vert.x instance
     */
    Server(final Vertx aVertx, final JsonObject aConfig) {
        myConfig = aConfig;
        myVertx = aVertx;
    }

    @Override
    public Future<Void> apply(final Router aRouter) {
        final Promise<Void> promise = Promise.promise();

        myVertx.createHttpServer().requestHandler(request -> {
            request.response().putHeader("content-type", "text/plain").end("Hello from Vert.x!");
        }).listen(HTTP_PORT, INADDR_ANY).onSuccess(server -> {
            LOGGER.info(MessageCodes.VNF_001, server.actualPort());
            promise.complete();
        }).onFailure(promise::fail);

        return promise.future();
    }

}
