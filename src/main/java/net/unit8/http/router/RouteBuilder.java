package net.unit8.http.router;

import net.unit8.http.router.segment.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteBuilder {
	public static final String[] SEPARATORS = {"/", ".", "?", "(", ")"};
	private static final Pattern PTN_OPTIONAL_FORMAT = Pattern.compile("\\A\\.(:format?)\\/");
	private static final Pattern PTN_SYMBOL          = Pattern.compile("\\A(?::(\\w+)|\\(:(\\w+)\\))");
	private static final Pattern PTN_PATH            = Pattern.compile("\\A\\*(\\w+)");
	private static final Pattern PTN_STATIC          = Pattern.compile("\\A\\?(.*?)\\?");

	private List<String> optionalSeparators;
	private Pattern separatorRegexp;
	private Pattern nonseparatorRegexp;

	public RouteBuilder() {
		optionalSeparators = Collections.singletonList("/");
		separatorRegexp = Pattern.compile("[" + RegexpUtil.escape(ARStringUtil.join(SEPARATORS)) + "]");
		nonseparatorRegexp = Pattern.compile("\\A([^" + RegexpUtil.escape(ARStringUtil.join(SEPARATORS))+ "]+)");
	}

	public List<Segment> segmentsForRoutePath(String path) {
		List<Segment> segments = new ArrayList<Segment>();
		StringBuilder rest = new StringBuilder(path);

		while (rest.length() > 0) {
			Segment segment = segmentFor(rest);
			segments.add(segment);
		}
		return segments;
	}

	public Segment segmentFor(StringBuilder sb) {
		String str = sb.toString();
		Segment segment = null;
		Matcher m;
		if ((m = PTN_OPTIONAL_FORMAT.matcher(str)).find()) {
			segment = new OptionalFormatSegment();
		} else if ((m = PTN_SYMBOL.matcher(str)).find()) {
			Options options = new Options();
			String key = m.group(1);
			if (ARStringUtil.isEmpty(key)) {
				key = m.group(2);
				options.$("wrapParentheses", true);
			}
			segment = ARStringUtil.equals(key, "controller") ? new ControllerSegment(key, options) : new DynamicSegment(key, options);
		} else if ((m = PTN_PATH.matcher(str)).find()) {
			segment = new PathSegment(m.group(1), new Options().$("optional", true));
		} else if ((m = PTN_STATIC.matcher(str)).find()) {
			segment = new StaticSegment(m.group(1), new Options().$("optional", true));
		} else if ((m = nonseparatorRegexp.matcher(str)).find()) {
			segment = new StaticSegment(m.group(1));
		} else if ((m = separatorRegexp.matcher(str)).find()) {
			segment = new DividerSegment(m.group(), new Options().$("optional", optionalSeparators.contains(m.group())));
		}
		sb.delete(0, m.end());
		return segment;
	}

	public Options[] divideRouteOptions(List<Segment> segments, Options options) {
		options = options.except("pathPrefix", "namePrefix");

		if (options.containsKey("namespace")) {
			String namespace = options.getString("namespace").replace("/$", "");
			options.remove("namespace");
			options.put("controller", namespace.replace('/', '.') + "." + options.get("controller"));
		}

		Options requirements = options.takeoutOptions("requirements");
		Options defaults = options.takeoutOptions("defaults");
		Options conditions = options.takeoutOptions("conditions");

		validateRouteConditions(conditions);

		List<String> pathKeys = new ArrayList<String>();
		for (Segment segment : segments)
			if (segment.hasKey())
				pathKeys.add(segment.getKey());

		for (Map.Entry<String, Object> e : options.entrySet()) {
			if (pathKeys.contains(e.getKey()) && !(e.getValue() instanceof Pattern)) {
				defaults.put(e.getKey(), e.getValue());
			} else {
				requirements.put(e.getKey(), e.getValue());
			}
		}

		return new Options[]{ defaults, requirements, conditions };
	}

	private Segment findSegment(List<Segment> segments, String key) {
		for (Segment seg : segments) {
			if (seg.hasKey() && ARStringUtil.equals(key, seg.getKey())) {
				return seg;
			}
		}
		return null;
	}
	public Options assignRouteOptions(List<Segment> segments, Options defaults, Options requirements) {
		Options routeRequirements = new Options();

		for (Map.Entry<String, Object> e : requirements.entrySet()) {
			final String key = e.getKey();
			final Object requirement = e.getValue();
			Segment segment = findSegment(segments, key);
			if (segment != null) {
				segment.setRegexp((Pattern)requirement);
			} else {
				routeRequirements.put(key, requirement);
			}
		}

		for (String key : defaults.keySet()) {
			final String defaultValue = defaults.getString(key);
			Segment segment = findSegment(segments, key);
			if (segment == null)
				throw new IllegalArgumentException(key + ": No matching segment exists; cannot assign default");
			if (defaultValue != null)
				segment.setOptional(true);
				segment.setDefault(defaultValue);
		}

		assignDefaultRouteOptions(segments);
		ensureRequiredSegments(segments);
		return routeRequirements;
	}

	private void assignDefaultRouteOptions(List<Segment> segments) {
		for (Segment segment : segments) {
			if (!(segment instanceof DynamicSegment))
				continue;
			String key = segment.getKey();
			if (ARStringUtil.equals(key, "action")) {
				segment.setDefault("index");
				segment.setOptional(true);
			} else if (ARStringUtil.equals(key, "id")) {
				segment.setOptional(true);
			}
		}
	}

	private void ensureRequiredSegments(List<Segment> segments) {
		boolean allowOptional = true;
		for (int i=segments.size() - 1; i >= 0; i--) {
			Segment segment = segments.get(i);
			allowOptional = allowOptional && segment.isOptional();
			if (!allowOptional && segment.isOptional()) {
				segment.setOptional(false);
			} else if (allowOptional && segment.hasDefault() && !ARStringUtil.isEmpty(segment.getDefault())) {
				segment.setOptional(true);
			}
		}
	}

	public Route build(String path, Options options) {
		if (path.charAt(0) != '/')
			path = "/" + path;

		String prefix = options.getString("pathPrefix").replace("^/", "");
		if (!ARStringUtil.isEmpty(prefix))
			path = "/" + prefix + path;

		List<Segment> segments = segmentsForRoutePath(path);
		Options[] extOptions = divideRouteOptions(segments, options);
		Options requirements = assignRouteOptions(segments, extOptions[0]/*defaults*/ , extOptions[1]/*requirements*/);

		return new Route(segments, requirements, extOptions[2]/*conditions*/);
	}

	private void validateRouteConditions(Options conditions) {
 		List<Object> methods = conditions.getList("method");

		for (Object m : methods) {
			String method = (String)m;
			if (ARStringUtil.equals(method, "HEAD")) {
				throw new IllegalArgumentException("HTTP method HEAD is invalid in route conditions.");
			}

			if (!Routes.HTTP_METHODS.contains(method)) {
				throw new IllegalArgumentException("Invalid HTTP method specified in route conditions: " + conditions);
			}
		}
	}
}
