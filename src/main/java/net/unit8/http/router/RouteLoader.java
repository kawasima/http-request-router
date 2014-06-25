package net.unit8.http.router;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class RouteLoader extends DefaultHandler {
	private String controller = null;
	private Stack<String> pathScope = new Stack<String>();
	private Stack<String> moduleScope = new Stack<String>();
	private Locator locator;
	private RouteBuilder builder;
	private Options options;
	private String path;
	private List<Route> routes;

	public RouteLoader(RouteBuilder builder) {
		this.routes = new ArrayList<Route>();
		this.builder = builder;
	}

	public List<Route> load(File config) {
		FileInputStream in = null;
		try {
            in = new FileInputStream(config);
			return load(in);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
		} finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {

                }
            }
		}
	}

	public List<Route> load(InputStream stream) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(stream), this);
            return routes;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
	}

	@Override
	public void setDocumentLocator(final Locator locator) {
		this.locator = locator;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("match") || Routes.HTTP_METHODS.contains(qName.toUpperCase())) {
			path = attributes.getValue("path");
			if (ARStringUtil.isEmpty(path)) {
				throw new SAXParseException("Can't find path in match.", locator);
			}
			options = processAttributes(attributes);
			if (!qName.equalsIgnoreCase("match")) {
				Options conditions = (Options)options.get("conditions");
				if (conditions == null) {
					conditions = new Options();
					options.put("conditions", conditions);
				}
				conditions.$("method", qName.toUpperCase());
			}
		} else if (qName.equalsIgnoreCase("controller")) {
			controller = attributes.getValue("name");
			if (ARStringUtil.isEmpty(controller)) {
				throw new SAXParseException("Can't find controller name.", locator);
			}
		} else if (qName.equalsIgnoreCase("root")) {
			Options options = processAttributes(attributes);
			routes.add(builder.build("/", options));
		} else if (qName.equalsIgnoreCase("namespace")) {
			String name = attributes.getValue("name");
			if (ARStringUtil.isEmpty(name)) {
				throw new SAXParseException("Can't find namespace's name.", locator);
			}
			pathScope.push(name);
			moduleScope.push(name);
		} else if (qName.equalsIgnoreCase("scope")) {
			String name = ARStringUtil.defaultIfEmpty(attributes.getValue("name"), "");
			String module = ARStringUtil.defaultIfEmpty(attributes.getValue("module"), "");
			if (ARStringUtil.isEmpty(name) && ARStringUtil.isEmpty(module)) {
				throw new SAXParseException("Scope must have any attributes, name or module.", locator);
			}
			pathScope.push(name);
			moduleScope.push(module);
		} else if (qName.equalsIgnoreCase("requirements")) {
			options.$("requirements", new Options());
		} else if (qName.equalsIgnoreCase("requirement")) {
			Options requirements = (Options)options.get("requirements");
			if (requirements == null) {
				throw new SAXParseException("Requirement must be in the requirements.", locator);
			}
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			requirements.$(name, Pattern.compile(value));
		} else if (qName.equalsIgnoreCase("defaults")) {
			options.$("defaults", new Options());
		} else if (qName.equalsIgnoreCase("default")) {
			Options defaults = (Options)options.get("defaults");
			if (defaults == null) {
				throw new SAXParseException("Default must be in the defaults.", locator);
			}
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			defaults.$(name, value);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("match")  || Routes.HTTP_METHODS.contains(qName.toUpperCase())) {
			routes.add(builder.build(path, options));
			options = null;
		} else if (qName.equalsIgnoreCase("controller")) {
			controller = null;
		} else if (qName.equalsIgnoreCase("namespace") || qName.equalsIgnoreCase("scope")) {
			pathScope.pop();
			moduleScope.pop();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}

	private Options processAttributes(Attributes attributes) {
		int attrLen = attributes.getLength();
		Options options = new Options();
		for (int i=0; i < attrLen; i++) {
			String optionName = attributes.getQName(i);
			if (ARStringUtil.equals(optionName, "path")) {
				continue;
			} else if (ARStringUtil.equals(optionName, "to")) {
				String[] tokens = attributes.getValue(i).split("#", 2);
				if (tokens.length == 1) {
					options.$("action", tokens[0]);
				} else {
					options.$("controller", tokens[0]).$("action", tokens[1]);
				}
			} else if (ARStringUtil.equals(optionName, "via")){
				String[] methods = attributes.getValue(i).split(",");
				for (String method : methods) {
					if (Routes.HTTP_METHODS.contains(method.trim().toUpperCase())) {
						Options conditions = (Options)options.get("conditions");
						if (conditions == null) {
							conditions = new Options();
							options.put("conditions", conditions);
						}

						List<Object> methodList;
						if (!conditions.containsKey("method")) {
							methodList = new ArrayList<Object>();
							conditions.$("method", methodList);
						} else {
							methodList = conditions.getList("method");
						}
						methodList.add(method.trim().toUpperCase());
					}
				}
			} else {
				options.put(optionName, attributes.getValue(i));
			}
		}
		if (controller != null) {
			options.put("controller", controller);
		}
		if (!pathScope.empty()) {
			String pathPrefix = ARStringUtil.strip(ARStringUtil.join(pathScope, '/').replaceAll("/+", "/"), "/");
			if (!ARStringUtil.isEmpty(pathPrefix) && !ARStringUtil.equals(pathPrefix, "/"))
				options.put("pathPrefix", pathPrefix);
		}
		if (!moduleScope.empty()) {
			String namespace = ARStringUtil.strip(ARStringUtil.join(moduleScope, '/').replaceAll("/+", "/"), "/");
			if (!ARStringUtil.isEmpty(namespace) && !ARStringUtil.equals(namespace, "/"))
				options.put("namespace", namespace);
		}
		return options;
	}
}
