package net.unit8.http.router;

import net.unit8.http.router.recognizer.OptimizedRecognizer;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public class RouteSet {
	private TreeSet<File> configurationFiles;
	private List<Route> routes;
	private RouteBuilder builder = new RouteBuilder();
	private Recognizer recognizer;
	private Map<String, Map<String, List<Route>>> routesByController;

	public RouteSet() {
		routes = new ArrayList<Route>();
		configurationFiles = new TreeSet<File>();
		routesByController = new HashMap<String, Map<String,List<Route>>>();
		recognizer = new OptimizedRecognizer();
	}

	public void clear() {
		routes.clear();
		routesByController.clear();
	}

	public void addConfigurationFile(File file) {
		configurationFiles.add(file);
	}

	public Set<File> getConfigurationFiles() {
		return configurationFiles;
	}

	public void load() {
		clear();
		loadRoutes();
	}

	public void loadStream(InputStream stream) {
		clear();
		routes = new RouteLoader(builder).load(stream);
		recognizer.setRoutes(routes);
	}

	private void loadRoutes() {
		if (!configurationFiles.isEmpty()) {
			for (File config : configurationFiles) {
				routes = new RouteLoader(builder).load(config);
			}
		} else {
			addRoute(":controller/:action/:id", new Options());
		}
		recognizer.setRoutes(routes);
	}

	public Route addRoute(String path, Options options) {
		Route route = builder.build(path, options);
		routes.add(route);
		return route;
	}

	public Options recognizePath(String path, String method) {
		if (!recognizer.isOptimized()) {
			recognizer.setRoutes(routes);
		}
		return recognizer.recognize(path, method);
	}

	public String generate(Options options) {
		Options merged = new Options(options);
		String controller = merged.getString("controller");
		String action     = merged.getString("action");
		if (ARStringUtil.isEmpty(controller) || ARStringUtil.isEmpty(action)) {
			throw new RoutingException("Need controller and action!");
		}
		List<Route> routes = routesByController(controller, action);
		for(Route route : routes) {
			if (!hasAllKey(route, options))
				continue;
			String results = route.generate(options, merged);
			if (!ARStringUtil.isEmpty(results)) {
				return results;
			}
		}
		throw new RoutingException("No route matches " + options.toString());
	}

	private boolean hasAllKey(Route route, Options options) {
		for (String key : route.significantKeys()) {
			if (!options.containsKey(key))
				return false;
		}
		return true;
	}

	private List<Route> routesByController(String controller, String action) {
		Map<String, List<Route>> actionMap = routesByController.get(controller);
		if (actionMap == null) {
			actionMap = new HashMap<String, List<Route>>();
			routesByController.put(controller, actionMap);
		}
		List<Route> routesByAction = actionMap.get(action);
		if (routesByAction == null) {
			routesByAction = new ArrayList<Route>();
			for (Route route : routes) {
				if (route.matchesControllerAndAction(controller, action)) {
					routesByAction.add(route);
				}
			}
			actionMap.put(action, routesByAction);
		}
		return routesByAction;
	}

	public String toString() {
		StringBuilder out = new StringBuilder(1024);
		for (Route route : routes) {
			out.append(route.toString());
		}
		return out.toString();
	}
}