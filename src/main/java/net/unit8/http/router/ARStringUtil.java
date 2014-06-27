package net.unit8.http.router;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ARStringUtil {
	public static final String EMPTY = "";
	public static final int INDEX_NOT_FOUND = -1;

    public static boolean equals(String s1, String s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    public static String urlencode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String urldecode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String capitalize(String str) {
        if (isEmpty(str))
            return str;
        char[] cs = str.toCharArray();
        cs[0] = Character.toUpperCase(cs[0]);
        return new String(cs);
    }

    public static String decapitalize(String str) {
        if (isEmpty(str))
            return str;

        char cs[] = str.toCharArray();
        if (cs.length >=2 && Character.isUpperCase(cs[0])
                && Character.isUpperCase(cs[1]))
            return str;

        cs[0] = Character.toLowerCase(cs[0]);
        return new String(cs);
    }

	public static String join(Object[] array) {
		return join(array, null);
	}

	public static String join(Object[] array, char separator) {
		if (array == null) {
			return null;
		}

		return join(array, separator, 0, array.length);
	}

	public static String join(Object[] array, char separator, int startIndex,
			int endIndex) {
		if (array == null) {
			return null;
		}
		int bufSize = (endIndex - startIndex);
		if (bufSize <= 0) {
			return EMPTY;
		}

		bufSize *= ((array[startIndex] == null ? 16 : array[startIndex]
				.toString().length()) + 1);
		StringBuilder buf = new StringBuilder(bufSize);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	public static String join(Object[] array, String separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	public static String join(Object[] array, String separator, int startIndex,
			int endIndex) {
		if (array == null) {
			return null;
		}
		if (separator == null) {
			separator = EMPTY;
		}

		// endIndex - startIndex > 0: Len = NofStrings *(len(firstString) +
		// len(separator))
		// (Assuming that all Strings are roughly equally long)
		int bufSize = (endIndex - startIndex);
		if (bufSize <= 0) {
			return EMPTY;
		}

		bufSize *= ((array[startIndex] == null ? 16 : array[startIndex]
				.toString().length()) + separator.length());

		StringBuilder buf = new StringBuilder(bufSize);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	public static String join(Collection collection, char separator) {
		if (collection == null) {
			return null;
		}
		return join(collection.iterator(), separator);
	}

	public static String join(Collection collection, String separator) {
		if (collection == null) {
			return null;
		}
		return join(collection.iterator(), separator);
	}

	public static String join(Iterator iterator, char separator) {

		// handle null, zero and one elements before building a buffer
		if (iterator == null) {
			return null;
		}
		if (!iterator.hasNext()) {
			return EMPTY;
		}
		Object first = iterator.next();
		if (!iterator.hasNext()) {
			return first.toString();
		}

		// two or more elements
		StringBuilder buf = new StringBuilder(256); // Java default is 16,
													// probably too small
		if (first != null) {
			buf.append(first);
		}

		while (iterator.hasNext()) {
			buf.append(separator);
			Object obj = iterator.next();
			if (obj != null) {
				buf.append(obj);
			}
		}

		return buf.toString();
	}

	public static String join(Iterator iterator, String separator) {

		// handle null, zero and one elements before building a buffer
		if (iterator == null) {
			return null;
		}
		if (!iterator.hasNext()) {
			return EMPTY;
		}
		Object first = iterator.next();
		if (!iterator.hasNext()) {
			return first.toString();
		}

		// two or more elements
		StringBuilder buf = new StringBuilder(256); // Java default is 16,
													// probably too small
		if (first != null) {
			buf.append(first);
		}

		while (iterator.hasNext()) {
			if (separator != null) {
				buf.append(separator);
			}
			Object obj = iterator.next();
			if (obj != null) {
				buf.append(obj);
			}
		}
		return buf.toString();
	}

    public static String substringBeforeLast(String str, String separator) {
        if (ARStringUtil.isEmpty(str) || ARStringUtil.isEmpty(separator)) {
            return str;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

	public static String substringAfterLast(String str, String separator) {
		if (ARStringUtil.isEmpty(str)) {
			return str;
		}
		if (ARStringUtil.isEmpty(separator)) {
			return EMPTY;
		}
		int pos = str.lastIndexOf(separator);
		if (pos == INDEX_NOT_FOUND
				|| pos == (str.length() - separator.length())) {
			return EMPTY;
		}
		return str.substring(pos + separator.length());
	}

    public static String strip(String str, String stripChars) {
        if (ARStringUtil.isEmpty(str)) {
            return str;
        }
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }

    public static String stripStart(String str, String stripChars) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while ((start != strLen) && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((start != strLen) && (stripChars.indexOf(str.charAt(start)) != INDEX_NOT_FOUND)) {
                start++;
            }
        }
        return str.substring(start);
    }

    public static String stripEnd(String str, String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }

        if (stripChars == null) {
            while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != INDEX_NOT_FOUND)) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    public static String removeStart(String str, String started) {
        if (isEmpty(str) || isEmpty(started))
            return str;
        return str.startsWith(started) ? str.substring(started.length()) : str;
    }
}
