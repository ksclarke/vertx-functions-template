
package info.freelibrary.vertx.functions;

import info.freelibrary.util.HTTP;
import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;
import info.freelibrary.util.StringUtils;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * A simple 'Hello World' handler to test the functionality of the service.
 */
public class HelloWorld implements Handler<RoutingContext> {

    /** The logger for this handler. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorld.class, MessageCodes.BUNDLE);

    /** A super simple HTML template. */
    private static final String TEMPLATE = "<!DOCTYPE html><html><body><h3>{}</h3></body></html>";

    @Override
    public void handle(final RoutingContext aEvent) {
        final HttpServerResponse response = aEvent.request().response();
        final String body = StringUtils.format(TEMPLATE, LOGGER.getMessage(MessageCodes.vnf_006));

        response.setStatusCode(HTTP.OK).putHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML.toString()).end(body);
    }
}
