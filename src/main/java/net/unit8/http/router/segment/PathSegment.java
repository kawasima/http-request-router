package net.unit8.http.router.segment;

import net.unit8.http.router.ARStringUtil;
import net.unit8.http.router.Options;
import net.unit8.http.router.RoutingException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathSegment extends DynamicSegment {
	private static final Pattern ENCODED_SLASH = Pattern.compile("%2f", Pattern.CASE_INSENSITIVE);

	public PathSegment(String key, Options options) {
		super(key, options);
	}

	@Override
	public String interpolationChunk(Options hash) {
		String value = hash.getString(getKey());
		try {
			value = URLEncoder.encode(value, "UTF-8");
			Matcher m = ENCODED_SLASH.matcher(value);
			return m.replaceAll("/");
		} catch (Exception e) {
			return value;
		}
	}

	@Override
	public String getDefault() {
		return ARStringUtil.EMPTY;
	}

	public void setDefault(String path) {
		if (!ARStringUtil.isEmpty(path))
			throw new RoutingException("paths cannot have non-empty default values");
	}

	public String defaultRegexpChunk() {
		return "(.*)";
	}

	@Override
	public int numberOfCaptures() {
		return 1;
	}

	public boolean optionalityImplied() {
		return true;
	}

	@Override
	public void matchExtraction(Options params, Matcher match, int nextCapture) {
		String value = match.group(nextCapture);
		try {
			value = URLDecoder.decode(value, "UTF-8");
		} catch (Exception ignore) {
			// ignore
		}
		params.put(getKey(), value);
	}
}
