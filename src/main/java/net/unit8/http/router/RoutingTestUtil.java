package net.unit8.http.router;

public class RoutingTestUtil {
	public static void assertGenerates(String expected, String optionString) {
		Options options = UrlRewriter.parseOptionString(optionString);
		String actual = Routes.generate(options);
		if (!ARStringUtil.equals(expected, actual)) {
			fail(format(null, expected, actual));
		}
	}

	public static void  assertRecognizes(String optionString, String path) {
		Options expected = UrlRewriter.parseOptionString(optionString);
		Options actual = Routes.recognizePath(path);
		if (!expected.equals(actual)) {
			fail(format(null, expected, actual));
		}
	}

	public static void fail(String message) {
		throw new AssertionError(message);
	}

	public static String format(String message, Object expected, Object actual) {
		String formatted= "";
		if (message != null)
			formatted= message+" ";
		return formatted+"expected:<"+expected+"> but was:<"+actual+">";
	}
}
