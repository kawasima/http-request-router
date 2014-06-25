package net.unit8.http.router;

import java.util.List;

public abstract class Recognizer {
	public abstract void setRoutes(List<Route> routes);
	public abstract Options recognize(String path, String method);
	public abstract boolean isOptimized();
	public abstract void optimize();
}
