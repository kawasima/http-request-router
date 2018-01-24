package net.unit8.http.router;

import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.*;

public class RouteLoaderTest {
    @Test
    public void test() {
        File routes = new File("src/test/resources/routes.xml");
        RouteSet routeSet = new RouteSet();
        routeSet.addConfigurationFile(routes);
        routeSet.load();
        Options options = routeSet.recognizePath("/user/list", "get");
        assertThat(options)
            .contains(
                entry("controller", "admin.User"),
                entry("action", "index"));

        try {
            routeSet.recognizePath("/post/unknown", "post");
            fail("Didn't raise RoutingException");
        } catch (RoutingException e) {
            assertThat(e).hasMessageContaining("No route matches");
        }
    }
}
