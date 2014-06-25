package net.unit8.http.router.segment;

@SuppressWarnings("serial")
public class RoutingException extends RuntimeException {
	public RoutingException(String cause) {
		super(cause);
	}
}
