package net.unit8.http.router;

public class ControllerUtil {

	public static String fromPathToClassName(String path) {
		String className = path.replace("/", ".");
		if (className.lastIndexOf('.') >= 0) {
			className = ARStringUtil.substringBeforeLast(className, ".") + "." + ARStringUtil.capitalize(ARStringUtil.substringAfterLast(className, "."));
		} else {
			className = ARStringUtil.capitalize(className);
		}
		return className;
	}

	public static String fromClassNameToPath(String className) {
		String path = className.replace(".", "/");
		if (path.lastIndexOf('/') >= 0) {
			path = ARStringUtil.substringBeforeLast(path, "/") + "/" + ARStringUtil.decapitalize(ARStringUtil.substringAfterLast(path, "/"));
		} else {
			path = ARStringUtil.decapitalize(path);
		}
		return path;
	}

    public static String currentController() {
        if (Routes.currentControllerFinder != null) {
            return Routes.currentControllerFinder.find();
        } else {
            throw new UnsupportedOperationException("To use this feature, configure it by properties.");
        }
    }

}
