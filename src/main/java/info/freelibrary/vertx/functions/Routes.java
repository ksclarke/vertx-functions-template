
package info.freelibrary.vertx.functions;

import java.util.function.Function;

import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.RouterBuilder;

/**
 * Routes for our function service.
 */
final class Routes implements Function<RouterBuilder, Future<Router>> {

    @Override
    public Future<Router> apply(final RouterBuilder aRouterBuilder) {
        return Future.succeededFuture(aRouterBuilder.createRouter());
    }

}
