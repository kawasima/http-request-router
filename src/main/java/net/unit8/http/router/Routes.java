package net.unit8.http.router;

import net.unit8.http.router.detector.NopControllerDetector;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author kawasima
 */
public class Routes {
	private static volatile List<String> possibleControllers = null;
	private static RouteSet routeSet = new RouteSet();
    private static ControllerDetector controllerDetector;
    static CurrentControllerFinder currentControllerFinder;

	public static final List<String> HTTP_METHODS = Collections.unmodifiableList(
			Arrays.asList("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

	public static String generate(Options options) {
		return getRouteSet().generate(options);
	}

    public static Options recognizePath(String path) {
        return recognizePath(path, "GET");
    }

	public static Options recognizePath(String path, String method) {
		return getRouteSet().recognizePath(path, method);
	}

	public static void load(InputStream stream) {
		getRouteSet().loadStream(stream);
	}

	public static void load(File config) {
		getRouteSet().getConfigurationFiles().clear();
		getRouteSet().addConfigurationFile(config);
		getRouteSet().load();
	}

	public static synchronized RouteSet getRouteSet() {
		return routeSet;
	}

	public static synchronized List<String> possibleControllers() {
		if (possibleControllers == null) {
			synchronized(Routes.class) {
				if (possibleControllers == null) {
					possibleControllers = controllerDetector.detect();
				}
			}
		}
		return possibleControllers;
	}

    static {
        InputStream is = Routes.class.getResourceAsStream("routes.properties");
        if (is == null) {
            controllerDetector = new NopControllerDetector();
        } else {
            Properties props = new Properties();
            try {
                props.load(is);
                String detectorClassName = props.getProperty("router.controllerDetector");
                if (detectorClassName != null) {
                    controllerDetector = (ControllerDetector)Class.forName(detectorClassName)
                            .newInstance();
                }
                String finderClassName = props.getProperty("router.currentControllerFinder");
                if (finderClassName != null) {
                    currentControllerFinder = (CurrentControllerFinder)Class.forName(finderClassName)
                            .newInstance();
                }
            } catch(Exception e) {
                throw new IllegalStateException(e);
            } finally {
                try {
                    is.close();
                } catch (IOException ignore) {}
            }
            if (controllerDetector == null)
                controllerDetector = new NopControllerDetector();
        }
    }
}
