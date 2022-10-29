
package info.freelibrary.vertx.functions;

import java.util.Set;

import info.freelibrary.util.HTTP;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * A simple echo handler to test the functionality of the service.
 */
public class Echo implements Handler<RoutingContext> {

    @Override
    public void handle(final RoutingContext aEvent) {
        final HttpServerRequest request = aEvent.request();
        final HttpServerResponse response = request.response();
        final MultiMap headers = request.headers();
        final MultiMap params = request.params();
        final JsonObject body = new JsonObject();

        body.put(SpecKeys.METHOD, request.method().name());
        body.put(SpecKeys.PATH, request.path());
        body.put(SpecKeys.ABSOLUTE_URI, request.absoluteURI());
        body.put(SpecKeys.REMOTE_ADDRESS, request.remoteAddress().toString());

        if (headers.size() > 0) {
            body.put(SpecKeys.HEADERS, serialize(request.headers()));
        }

        if (params.size() > 0) {
            body.put(SpecKeys.PARAMS, serialize(request.params()));
        }

        if (request.cookieCount() > 0) {
            body.put(SpecKeys.COOKIES, serialize(request.cookies()));
        }

        response.setStatusCode(HTTP.OK);
        response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()).end(body.encode());
    }

    /**
     * Converts a set of cookies into a JsonArray.
     *
     * @param aCookieSet A set of cookies
     * @return A JsonArray of cookies
     */
    private JsonArray serialize(final Set<Cookie> aCookieSet) {
        final JsonArray cookies = new JsonArray();

        aCookieSet.forEach(cookie -> {
            final JsonObject json = new JsonObject();

            json.put(SpecKeys.COOKIE_NAME, cookie.getName());
            json.put(SpecKeys.COOKIE_PATH, cookie.getPath());
            json.put(SpecKeys.COOKIE_VALUE, cookie.getValue());
            json.put(SpecKeys.COOKIE_MAX_AGE, cookie.getMaxAge());
            json.put(SpecKeys.COOKIE_DOMAIN, cookie.getDomain());

            cookies.add(json);
        });

        return cookies;
    }

    /**
     * Converts a MultiMap into a JsonObject.
     *
     * @param aMultiMap A map that may contain duplicate keys
     * @return A JsonObject representing the MultiMap
     */
    private JsonObject serialize(final MultiMap aMultiMap) {
        final JsonObject json = new JsonObject();

        // Cycle through the MultiMap entries and add them to the output JSON
        aMultiMap.forEach((key, value) -> {
            if (!HttpHeaders.CONTENT_LENGTH.equals(key)) {
                if (json.containsKey(key)) {
                    final Object object = json.getValue(key);

                    if (object instanceof JsonArray) {
                        ((JsonArray) object).add(value);
                    } else {
                        // Convert the existing non-array value into a JsonArray
                        json.put(key, new JsonArray().add(json.remove(key)).add(value));
                    }
                } else {
                    json.put(key, value);
                }
            }
        });

        return json;
    }
}
