package net.unit8.http.router;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.assertj.core.api.Assertions.*;

public class PathMatchingTest {
    @Before
    public void setup() {
        Routes.load(new File("src/test/resources/routes/paths.xml"));
    }
    @Test
    public void pathBasic() {
        assertThat(Routes.recognizePath("/photos/12"))
                .contains(entry("other", "12"));
        assertThat(Routes.recognizePath("/photos/long/path/to/12"))
                .contains(entry("other", "long/path/to/12"));
    }

    @Test
    public void pathMiddle() {
        assertThat(Routes.recognizePath("/books/some/section/last-words-a-memoir"))
                .contains(
                        entry("section", "some/section"),
                        entry("title", "last-words-a-memoir"));
    }

    @Test
    public void pathUtf8() throws UnsupportedEncodingException {
        assertThat(Routes.recognizePath("/photos/😆/💢"))
                .contains(entry("other", "😆/💢"));
        assertThat(Routes.recognizePath("/photos/" + URLEncoder.encode("😆", "UTF-8")
                + "/" + URLEncoder.encode("💢", "UTF-8")))
                .contains(entry("other", "😆/💢"));
    }

    @After
    public void tearDown() {
        Routes.getRouteSet().clear();
    }
}
