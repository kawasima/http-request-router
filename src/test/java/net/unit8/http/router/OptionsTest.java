package net.unit8.http.router;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class OptionsTest {
    @Test
    public void getListByEmpty() {
        assertThat(new Options().getList("hoge")).isEmpty();
    }

    @Test
    public void getListByArray() {
        assertThat(new Options().$("hoge", new String[] {"foo", "bar"}).getList("hoge"))
                .containsExactly("foo", "bar");
    }

    @Test
    public void getListByString() {
        assertThat(new Options().$("hoge", "moga").getList("hoge"))
                .containsExactly("moga");
    }

    @Test
    public void getListByList() {
        assertThat(new Options().$("hoge", Arrays.asList("foo", "bar")).getList("hoge"))
                .containsExactly("foo", "bar");
    }

    @Test
    public void getListByCollection() {
        Set<String> value = new LinkedHashSet<String>();
        value.add("foo");
        value.add("bar");
        assertThat(new Options().$("hoge", value).getList("hoge"))
                .containsExactly("foo", "bar");
    }
}
