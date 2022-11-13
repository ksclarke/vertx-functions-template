
package info.freelibrary.vertx.native_.example.vertx_native_example;

import static info.freelibrary.util.Constants.INADDR_ANY;
import static info.freelibrary.util.Constants.SLASH;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

import info.freelibrary.vertx.functions.Configs;
import info.freelibrary.vertx.functions.HttpVerticle;
import info.freelibrary.vertx.functions.MediaType;
import info.freelibrary.vertx.functions.MessageCodes;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Simple tests of our example function.
 */
@ExtendWith(VertxExtension.class)
public class TestExampleFunction {

    /** A logger for the test to use. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestExampleFunction.class, MessageCodes.BUNDLE);

    /**
     * A test of the example function.
     *
     * @param aVertx A Vert.x instance
     * @param aContext A test instance
     * @throws Throwable An exception thrown during the test
     */
    @Test
    public void testFunction(final Vertx aVertx, final VertxTestContext aContext) throws Throwable {
        final int port = Integer.parseInt(System.getenv(Configs.HTTP_PORT));
        final WebClient client = WebClient.create(aVertx);

        aVertx.deployVerticle(new HttpVerticle())
                .compose(id -> client.get(port, INADDR_ANY, SLASH).expect(ResponsePredicate.SC_OK)
                        .expect(ResponsePredicate.contentType(MediaType.TEXT_HTML.toString())).send()
                        .eventually(cleanup -> {
                            client.close();
                            return aVertx.undeploy(id);
                        }))
                .onSuccess(response -> {
                    aContext.verify(() -> {
                        assertTrue(response.bodyAsString().contains(LOGGER.getMessage(MessageCodes.vnf_006)));
                    });

                    aContext.completeNow();
                }).onFailure(aContext::failNow);
    }
}
