package net.unit8.http.router;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

public class RouteGenerationTest {
    @Test
    public void test() {
        RouteSet routeSet = new RouteSet();
        Route route = routeSet.addRoute("/blog/:id/comments",
            new Options().$("controller", "Blog").$("action", "comments"));
        Options hash = new Options().$("controller", "Blog")
            .$("action", "comments").$("id", 8).$("other", "parameter");
        Options options = new Options().$("controller", "Blog")
            .$("action", "comments").$("id", 8).$("other", "parameter");
        String url = route.generate(options, hash);
        assertThat(url)
            .as("ID is a path parameter, and other is sets as query string")
            .isEqualTo("/blog/8/comments?other=parameter");
    }

    @Test
    public void testMultipleParameter() {
        RouteSet routeSet = new RouteSet();
        Route route = routeSet.addRoute("/blog/:id/comments",
            new Options().$("controller", "Blog").$("action", "comments"));
        Options hash = new Options().$("controller", "Blog")
            .$("action", "comments").$("id", 8)
            .$("other", Arrays.asList("parameter", "parameter2"));
        Options options = new Options().$("controller", "Blog")
            .$("action", "comments").$("id", 8)
            .$("other", Arrays.asList("parameter", "parameter2"));
        String url = route.generate(options, hash);
        assertThat(url)
            .isEqualTo("/blog/8/comments?other=parameter&other=parameter2");
    }

    @Test
    public void testController() {
        RouteSet routeSet = new RouteSet();
        Route route = routeSet.addRoute("/:controller/:action", new Options());
        String url = route.generate(new Options(),
            new Options().$("controller", "Blog").$("action", "comments")
                .$("id", 8));
        assertThat(url)
            .isEqualTo("/blog/comments");
    }
}
