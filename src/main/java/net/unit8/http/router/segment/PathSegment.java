package net.unit8.http.router.segment;

import net.unit8.http.router.ARStringUtil;
import net.unit8.http.router.Options;

import java.util.regex.Matcher;

public class PathSegment extends DynamicSegment {
	public PathSegment(String key, Options options) {
		super(key, options);
	}

	@Override
	public String interpolationChunk(Options hash) {
		String value = hash.getString(getKey());
		return value;
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
		params.put(getKey(), value);
	}
}
