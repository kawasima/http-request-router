package net.unit8.http.router;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

public class UrlRewriterTest {
    @Test
    public void test() {
        assertThat(UrlRewriter.parseOptionString("User#list?id=1"))
            .containsExactly(
                entry("controller", "User"),
                entry("action", "list"),
                entry("id", "1"));
    }

    @Test
    public void testPluralParameters() {
        assertThat(UrlRewriter.parseOptionString("User#search?cond=hoge&cond=fuga&cond=huge"))
            .contains(
                entry("controller", "User"),
                entry("action", "search"),
                entry("cond", Arrays.asList("hoge", "fuga", "huge")));
    }

    @Test
    public void testNoController() {
        Routes.currentControllerFinder = new CurrentControllerFinder() {
            public String find() {
                return "User";
            }
        };
        assertThat(UrlRewriter.parseOptionString("list?id=1"))
            .contains(
                entry("controller", "User"),
                entry("action", "list"),
                entry("id", "1"));
    }

}
