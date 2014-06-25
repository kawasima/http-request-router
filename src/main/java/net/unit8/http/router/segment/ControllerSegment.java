package net.unit8.http.router.segment;

import net.unit8.http.router.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ControllerSegment extends DynamicSegment {
	public ControllerSegment(String value, Options options) {
		super(value, options);
	}

	public ControllerSegment(String key) {
		super(key);
	}

	@Override
	public String regexpChunk() {
		List<String> possibleNames = new ArrayList<String>();
		for (String name : Routes.possibleControllers()) {
			possibleNames.add(RegexpUtil.escape(name));
		}
		return "(?i-:(" + ARStringUtil.join(possibleNames, "|")+ "))";
	}

	@Override
	public void matchExtraction(Options params, Matcher match, int nextCapture) {
		String key = getKey();
		String token = match.group(nextCapture);
		if (getDefault() != null) {
			params.put(key, !ARStringUtil.isEmpty(token) ? ControllerUtil.fromPathToClassName(token) : getDefault());
		} else {
			 if (!ARStringUtil.isEmpty(token))
				 params.put(key, ControllerUtil.fromPathToClassName(token));
		}
	}

	@Override
	public String interpolationChunk(Options hash) {
		String value = hash.getString(getKey());
		String path = value.replace(".", "/");
		if (path != null) {
			if (path.lastIndexOf('/') >= 0)
				path = ARStringUtil.substringBeforeLast(path, "/") + "/" + ARStringUtil.decapitalize(ARStringUtil.substringAfterLast(path, "/"));
			else
				path = ARStringUtil.decapitalize(path);
		}
		return path;
	}
}
