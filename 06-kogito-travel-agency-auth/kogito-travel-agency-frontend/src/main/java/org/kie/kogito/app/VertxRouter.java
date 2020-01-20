package org.kie.kogito.app;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.vertx.http.runtime.security.QuarkusHttpUser;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static java.nio.charset.StandardCharsets.UTF_8;

@ApplicationScoped
public class VertxRouter {

    @Inject
    @ConfigProperty(name = "kogito.dataindex.http.url", defaultValue = "http://localhost:8180")
    String dataIndexHttpURL;

    @Inject
    @ConfigProperty(name = "kogito.travel-agency.service.url", defaultValue = "http://localhost:8082")
    String travelAgencyServiceURL;

    @Inject
    Vertx vertx;

    private String resource;

    @PostConstruct
    public void init() {
        try {
            resource = vertx.fileSystem()
                    .readFileBlocking("META-INF/resources/index.html")
                    .toString(UTF_8)
                    .replace("__GRAPHQL_HTTP_ENDPOINT__", "\"" + dataIndexHttpURL + "/graphql\"")
                    .replace("__TRAVEL_AGENCY_SERVICE_ENDPOINT__",  "\"" +travelAgencyServiceURL +"\"" );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void setupRouter(@Observes Router router) {
        router.route("/").handler(context -> {
            context.response()
                    .putHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf8")
                    .putHeader(HttpHeaders.AUTHORIZATION,
                               "Bearer " + ((QuarkusHttpUser) context.user()).getSecurityIdentity().getCredential(AccessTokenCredential.class).getToken())
                    .end(resource);
        });
        router.route("/token").handler(context -> {
            context.response()
                    .putHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf8")
                    .putHeader(HttpHeaders.AUTHORIZATION,
                               "Bearer " + ((QuarkusHttpUser) context.user()).getSecurityIdentity().getCredential(AccessTokenCredential.class).getToken())
                    .end(resource);
        });
    }
}
