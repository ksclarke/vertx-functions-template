
package info.freelibrary.vertx.functions;

import info.freelibrary.util.Constants;
import info.freelibrary.util.Env;

import io.vertx.ext.web.client.WebClientOptions;

/**
 * A Vert.x WebClient configuration used in testing.
 */
public class ClientTestConfig extends WebClientOptions {

    /**
     * Creates a WebClient configuration used in testing.
     */
    public ClientTestConfig() {
        super.setDefaultHost(Constants.INADDR_ANY).setDefaultPort(Env.get(Configs.HTTP_PORT, 8080));
    }
}
