
package info.freelibrary.vertx.functions;

import static info.freelibrary.util.Constants.INADDR_ANY;
import static info.freelibrary.util.Constants.SLASH;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.junit5.RunTestOnContext;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Some baseline tests for HttpVerticle's functionality.
 */
@ExtendWith(VertxExtension.class)
public class HttpVerticleTest {

    /** The tests' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpVerticleTest.class, MessageCodes.BUNDLE);

    /** The larger testing context. */
    @RegisterExtension
    private final RunTestOnContext myContext = new RunTestOnContext();

    /**
     * Sets up the testing environment.
     */
    @BeforeAll
    public static final void setUp() {
        System.setProperty(HttpVerticle.LOGGING_CONFIG, "logback.xml");
        System.setProperty(HttpVerticle.LOGGER_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());
    }

    /**
     * Tests the Echo function as it's invoked through the HttpVerticle.
     *
     * @param aContext A test context
     */
    @Test
    public final void testEchoFunction(final VertxTestContext aContext) {
        final int port = Integer.parseInt(System.getenv(Configs.HTTP_PORT));
        final Vertx vertx = myContext.vertx();
        final WebClient client = WebClient.create(vertx);

        vertx.deployVerticle(new HttpVerticle())
                .compose(id -> client.get(port, INADDR_ANY, SLASH).expect(ResponsePredicate.SC_OK)
                        .expect(ResponsePredicate.contentType(MediaType.TEXT_HTML.toString())).send()
                        .eventually(cleanup -> {
                            client.close();
                            return vertx.undeploy(id);
                        }))
                .onSuccess(response -> {
                    aContext.verify(() -> {
                        assertTrue(response.bodyAsString().contains(LOGGER.getMessage(MessageCodes.VFC_006)));
                    });

                    aContext.completeNow();
                }).onFailure(aContext::failNow);
    }
}
