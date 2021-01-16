
package info.freelibrary.vertx.lambda;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class HttpVerticle extends AbstractVerticle {

    @Override
    public void start(final Promise<Void> aPromise) {
        getVertx().createHttpServer().requestHandler(request -> {
            request.response().putHeader("content-type", "text/plain").end("Hello from Vert.x!");
        }).listen(8080, listen -> {
            if (listen.succeeded()) {
                System.out.println("Server listening on http://localhost:8080/");
            } else {
                listen.cause().printStackTrace();
                System.exit(1);
            }
        });

        aPromise.complete();
    }

    @SuppressWarnings("uncommentedmain")
    public static void main(final String[] args) {
        Vertx.vertx().deployVerticle(new HttpVerticle(), startup -> {
            if (startup.succeeded()) {

            } else {
                startup.cause().printStackTrace();
            }
        });
    }

}
