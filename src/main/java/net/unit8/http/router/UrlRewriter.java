package net.unit8.http.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UrlRewriter {
	public static String contextPath = null;

	public static String urlFor(Map<String, Object> opts) {
		Options options = new Options(opts);
		return urlFor(options);
	}

	public static String urlFor(String optionString) {
		Options options = parseOptionString(optionString);
		return urlFor(options);
	}

	public static String urlFor(Options options) {
		StringBuilder url = new StringBuilder();

		boolean trailingSlash = false;
		if (options.containsKey("trailing_slash")) {
			trailingSlash = options.getBoolean("trailing_slash");
			options.remove("trailing_slash");
		}

		String anchor = null;
		if (options.containsKey("anchor")) {
			anchor = "#" + ARStringUtil.urlencode(options.getString("anchor"));
			options.remove("anchor");
		}
		if (!options.getBoolean("skip_context_path") && !ARStringUtil.isEmpty(contextPath)) {
            url.append(contextPath);
            options.remove("skip_context_path");
        }
		String generated = Routes.generate(options);
		String path = trailingSlash ? trailingSlash(generated) : generated;

        // TODO call encodeURL

		url.append(path);
		if (!ARStringUtil.isEmpty(anchor))
			url.append(anchor);
		return url.toString();
	}

	private static String trailingSlash(String url) {
		int queryStringIdx = url.indexOf('?');
		if (queryStringIdx < 0 || url.length() < 1)
			return url + "/";
		if (queryStringIdx != 0 && url.charAt(queryStringIdx - 1) == '/') {
			return url;
		} else {
			return url.substring(0, queryStringIdx) + "/" + url.substring(queryStringIdx);
		}
	}

	@SuppressWarnings("unchecked")
	public static Options parseOptionString(String optionString) {
		String[] urlTokens = optionString.split("\\?", 2);
		String[] actionTokens = urlTokens[0].split("#", 2);

		Options options = new Options();
		if (actionTokens.length == 1) {
			options.$("action", actionTokens[0]);
			options.$("controller", ControllerUtil.currentController());
		} else {
			options.$("controller", actionTokens[0]).$("action",
					actionTokens[1]);
		}
		if (urlTokens.length == 2 && !ARStringUtil.isEmpty(urlTokens[1])) {
			String[] paramToken = urlTokens[1].split("&");
			for (String keyValuePair : paramToken) {
				String[] pair = keyValuePair.split("=", 2);
				if (pair.length == 1) {
					options.$(pair[0], null);
				} else if (pair.length == 2) {
					Object value = options.get(pair[0]);
					if (value == null) {
						options.$(pair[0], pair[1]);
					} else if (value instanceof ArrayList) {
						((ArrayList<String>) value).add(pair[1]);
					} else {
						List<String> values = new ArrayList<String>();
						values.add(value.toString());
						values.add(pair[1]);
						options.$(pair[0], values);
					}
				}
			}
		}
		return options;
	}
}
