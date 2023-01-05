
package info.freelibrary.vertx.functions;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;
import info.freelibrary.util.ThrowingBiConsumer;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.OpenAPIHolder;
import io.vertx.ext.web.openapi.RouterBuilder;

/**
 * Initializes handlers for the operations defined in the router builder's OpenAPI specification.
 */
public class OperationInitializer {

    /** The operation initializer's logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationInitializer.class, MessageCodes.BUNDLE);

    /** The service's router builder */
    private final RouterBuilder myRouterBuilder;

    /**
     * Creates an operation initializer used to load the handlers associated with the router's operation IDs.
     *
     * @param aRouterBuilder A router builder
     */
    public OperationInitializer(final RouterBuilder aRouterBuilder) {
        myRouterBuilder = aRouterBuilder;
    }

    /**
     * Gets a configured router from the underlying router builder.
     *
     * @return A service router
     */
    @SuppressWarnings("unchecked")
    public Router getRouter() {
        getHandlerMetadata(myRouterBuilder.getOpenAPI())
                .forEach((ThrowingBiConsumer<String, HandlerMetadata>) (handler, metadata) -> {
                    final Constructor<?> constructor = Class.forName(handler).getDeclaredConstructor();
                    final List<String> paths = metadata.getPaths();

                    if (LOGGER.isWarnEnabled()) {
                        final List<String> methods = metadata.getMethods();

                        for (int index = 0; index < paths.size(); index++) {
                            final String method = methods.get(index).toUpperCase(Locale.US);

                            if (index == paths.size() - 1) {
                                LOGGER.debug(MessageCodes.VFC_004, handler, method, paths.get(index));
                            } else {
                                LOGGER.warn(MessageCodes.VFC_005, method, handler);
                            }
                        }
                    }

                    myRouterBuilder.operation(handler).handler((Handler<RoutingContext>) constructor.newInstance());
                });

        return myRouterBuilder.createRouter();
    }

    /**
     * Gets a map of handler metadata from the router builder's configured OpenAPI specification.
     *
     * @param anOpenAPI An OpenAPI specification
     * @return A map of handler metadata
     */
    private Map<String, HandlerMetadata> getHandlerMetadata(final OpenAPIHolder anOpenAPI) {
        final Map<String, HandlerMetadata> handlers = new HashMap<>();

        anOpenAPI.getOpenAPI().getJsonObject("paths").forEach(requestPath -> {
            final String path = requestPath.getKey();
            final Object endpoint = requestPath.getValue();

            if (endpoint instanceof JsonObject) {
                ((JsonObject) endpoint).forEach(action -> {
                    final Object operation = action.getValue();

                    if (operation instanceof JsonObject) {
                        final String handler = ((JsonObject) operation).getString("operationId");
                        final String method = action.getKey();

                        if (handlers.containsKey(handler)) {
                            handlers.get(handler).addMethod(method).addPath(path);
                        } else {
                            handlers.put(handler, new HandlerMetadata(method, path, handler));
                        }
                    }
                });
            }
        });

        return handlers;
    }

    /**
     * Metadata that stores information about the operation mapping. For what it's worth, this class supports a mapping
     * that is not supported by the OpenAPI RouterBuilder. An <code>operationId</code> (in our case, Handler) cannot be
     * mapped to more than one path + method in the OpenAPI spec. In practice, the last combination set in the Router is
     * the one that's preserved.
     */
    private class HandlerMetadata {

        /** The methods used in the mapping. */
        private final List<String> myMethods;

        /** The request paths used in the mapping. */
        private final List<String> myPaths;

        /**
         * Creates new metadata from the supplied method and handler name.
         *
         * @param aMethod An HTTP method
         * @param aPath A request path
         */
        private HandlerMetadata(final String aMethod, final String aPath, final String aHandlerName) {
            myMethods = new ArrayList<>(Collections.singletonList(Objects.requireNonNull(aMethod)));
            myPaths = new ArrayList<>(Collections.singletonList(Objects.requireNonNull(aPath)));
        }

        /**
         * Gets the HTTP methods associated with this handler metadata.
         *
         * @return The handler's methods
         */
        private List<String> getMethods() {
            return myMethods;
        }

        /**
         * Adds a new method to the handler metadata.
         *
         * @param aMethod A method to add to the handler metadata
         * @return This handler metadata
         */
        private HandlerMetadata addMethod(final String aMethod) {
            myMethods.add(aMethod);
            return this;
        }

        /**
         * Gets the paths associated with this handler metadata.
         *
         * @return The handler's paths
         */
        private List<String> getPaths() {
            return myPaths;
        }

        /**
         * Adds a new path to the handler metadata.
         *
         * @param aPath A path to add to the handler metadata
         * @return This handler metadata
         */
        private HandlerMetadata addPath(final String aPath) {
            myPaths.add(aPath);
            return this;
        }
    }
}
