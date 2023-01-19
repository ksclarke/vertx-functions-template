
package info.freelibrary.vertx.functions;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * A simple integration test that confirms the Docker container spins up at least a default function.
 */
@ExtendWith(VertxExtension.class)
public class VertxFunctionIT {

    /**
     * Test the default function.
     *
     * @param aVertx A Vert.x instance
     * @param aContext A Vert.x test context
     */
    @Test
    @DisplayName("Test container's health check endpoint")
    public final void testFunctionHealth(final Vertx aVertx, final VertxTestContext aContext) {
        final WebClient client = WebClient.create(aVertx, new ClientTestConfig());
        final Future<?> futureGet = client.get("/_/health").expect(ResponsePredicate.SC_SUCCESS).send();

        aContext.verify(() -> futureGet.onFailure(error -> {
            fail(error);
        })).completeNow();
    }

}
