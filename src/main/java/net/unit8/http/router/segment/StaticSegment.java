package net.unit8.http.router.segment;


import net.unit8.http.router.ARStringUtil;
import net.unit8.http.router.Options;
import net.unit8.http.router.RegexpUtil;
import net.unit8.http.router.Segment;

public class StaticSegment extends Segment {
	private boolean raw;

	public StaticSegment(String value) {
		this(value, new Options());
	}
	public StaticSegment(String value, Options options) {
		super(value);
		if (options.containsKey("raw"))
			this.raw = options.getBoolean("raw");
		if (options.containsKey("optional"))
			setOptional(options.getBoolean("optional"));
	}

	@Override
	public String interpolationChunk(Options hash) {
		return raw ? getValue() : super.interpolationChunk(hash);
	}

	@Override
	public String regexpChunk() {
		String chunk = RegexpUtil.escape(getValue());
		return isOptional() ? RegexpUtil.optionalize(chunk) : chunk;
	}

	@Override
	public int numberOfCaptures() {
		return 0;
	}

	@Override
	public String buildPattern(String pattern) {
		String escaped = RegexpUtil.escape(getValue());
		if (isOptional() && !ARStringUtil.isEmpty(pattern)) {
			return "(?:" + RegexpUtil.optionalize(escaped) + "\\Z|" + escaped + RegexpUtil.unoptionalize(pattern) + ")";
		} else if (isOptional()) {
			return RegexpUtil.optionalize(escaped);
		} else {
			return escaped + pattern;
		}
	}

	@Override
	public String toString() {
		return getValue();
	}
}
