package net.unit8.http.router;

import java.util.*;

public class Options extends HashMap<String, Object>{
	private static final long serialVersionUID = 1L;

	public Options() {
	}

	public Options(Map<String, Object> options) {
		super(options);
	}

	public static Options newInstance() {
		return new Options();
	}

	public Options $(String key, Object value) {
		this.put(key, value);
		return this;
	}
	public String getString(String key) {
		Object value = this.get(key);
		return value == null ? "" : value.toString();
	}

	public String getUrlEncodedString(String key) {
		Object value = this.get(key);
		if (value == null) {
			return ARStringUtil.EMPTY;
		} else if (value instanceof Collection) {
			Collection<?> values = Collection.class.cast(value);
			List<String> pairs = new ArrayList<String>(values.size());
			for (Object val : values) {
                if (val == null)
                    val = ARStringUtil.EMPTY;
				pairs.add(ARStringUtil.urlencode(key) +  "=" + ARStringUtil.urlencode(val.toString()));
			}
			return ARStringUtil.join(pairs, "&");
		} else {
			return ARStringUtil.urlencode(key) + "=" + ARStringUtil.urlencode(value.toString());
		}
	}
	@SuppressWarnings("unchecked")
	public List<Object> getList(String key) {
		Object value = this.get(key);
		if (value == null) {
			return new ArrayList<Object>();
		}
		List<Object> valueList;
		if (List.class.isAssignableFrom(value.getClass())) {
			valueList = (List<Object>) value;
		} else if (value.getClass().isArray()) {
			valueList = Arrays.asList((Object[])value);
		} else if (Collection.class.isAssignableFrom(value.getClass())) {
			valueList = new ArrayList<Object>(Collection.class.cast(value));
		} else {
			valueList = new ArrayList<Object>(1);
			valueList.add(value);
		}
		return valueList;
	}


	public Options except(String...keys) {
		Options copy = new Options(this);
		for (String key : keys) {
			copy.remove(key);
		}
		return copy;
	}
	public boolean getBoolean(String key) {
		Object value = this.get(key);
        return value != null && (
                (value instanceof Boolean && (Boolean) value) ||
                        (value instanceof Number && ((Number) value).intValue() == 1) ||
                        ARStringUtil.equals(value.toString(), "true")
        );
    }
	public Options takeoutOptions(String key) {
		Object obj = this.remove(key);
		if (obj instanceof Options) {
			return (Options)obj;
		} else {
			return new Options();
		}
	}

	public String toQueryString() {
		StringBuilder queryString = new StringBuilder(512);
		for (String key : this.keySet()) {
			if (ARStringUtil.equals(key, "controller") || ARStringUtil.equals(key, "action"))
				continue;
			queryString.append(ARStringUtil.urlencode(key))
				.append('=')
				.append(ARStringUtil.urlencode(getString(key)));
		}
		return queryString.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(256).append("{ ");
		for (Map.Entry<String, Object> entry : this.entrySet()) {
			sb.append(":")
				.append(entry.getKey())
				.append(" => '")
				.append(entry.getValue() == null ? "null" : entry.getValue().toString())
				.append("', ");
		}
		if (sb.toString().endsWith(", "))
			sb.delete(sb.length() - 2, sb.length());
		sb.append(" }");

		return sb.toString();
	}

	@Override
	public boolean equals(Object another) {
		if (another == null || !(another instanceof Options))
			return false;
		Options anotherOptions = (Options) another;
		if (anotherOptions.keySet().size() != this.keySet().size())
			return false;

		for (String key : this.keySet()) {
			Object anotherValue = anotherOptions.get(key);
			Object thisValue    = this.get(key);
			if (anotherValue != thisValue && (thisValue == null || !thisValue.equals(anotherValue))) {
				return false;
			}
		}
		return true;
	}
}
