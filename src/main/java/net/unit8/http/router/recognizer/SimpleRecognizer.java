package net.unit8.http.router.recognizer;

import net.unit8.http.router.Options;
import net.unit8.http.router.Recognizer;
import net.unit8.http.router.Route;
import net.unit8.http.router.segment.RoutingException;

import java.util.List;

public class SimpleRecognizer extends Recognizer {
	private List<Route> routes;

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

    @Override
	public Options recognize(String path, String method) {
		for (Route route : routes) {
			Options result = route.recognize(path, method);
			if (result != null) return result;
		}
		throw new RoutingException("No route matches " + path);
	}

	@Override
	public boolean isOptimized() {
		return true;
	}

	@Override
	public void optimize() {
		// nop
	}
}
