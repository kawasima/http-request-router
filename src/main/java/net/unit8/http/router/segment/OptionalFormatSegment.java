package net.unit8.http.router.segment;

import net.unit8.http.router.ARStringUtil;
import net.unit8.http.router.Options;

import java.util.regex.Matcher;

public class OptionalFormatSegment extends DynamicSegment {
	public OptionalFormatSegment(String key, Options options) {
		super("format", options.$("optional", true));
	}

	public OptionalFormatSegment() {
		this(null, new Options());
	}

	@Override
	public String interpolationChunk(Options hash) {
		return "." + super.interpolationChunk(hash);
	}

	@Override
	public String regexpChunk() {
		return "/|(\\.[^/?\\.]+)?";
	}

	@Override
	public String toString() {
		return "(.:format)?";
	}

	@Override
	public void matchExtraction(Options params, Matcher match, int nextCapture) {
		String m = match.group(nextCapture);
		if (m != null) {
			params.put(getKey(), ARStringUtil.urldecode(m.substring(1)));
		} else {
			params.put(getKey(), ARStringUtil.urldecode(getDefault()));
		}
	}
}
