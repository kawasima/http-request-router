package net.unit8.http.router.recognizer;

import net.unit8.http.router.*;
import net.unit8.http.router.segment.RoutingException;

import java.util.ArrayList;
import java.util.List;

public class OptimizedRecognizer extends Recognizer {
	private SegmentNode tree;
	private List<Route> routes;

	public String[] toPlainSegments(String str) {
		str = str.replaceAll("^/+", "").replaceAll("/+$", "");
		return str.split("\\.[^/]+\\/+|" + "[" + RegexpUtil.escape(ARStringUtil.join(RouteBuilder.SEPARATORS)) + "]+|\\.[^/]+\\Z");
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
		optimize();
	}

	@Override
	public boolean isOptimized() {
		return tree != null;
	}

	@Override
	public void optimize() {
		tree = new SegmentNode(0);

		int i = -1;
		for (Route route : routes) {
			i += 1;
			SegmentNode node = tree;
			String[] segments = toPlainSegments(ARStringUtil.join(route.getSegments(), ""));
			for (String seg : segments) {
				if (!ARStringUtil.isEmpty(seg) && seg.charAt(0) == ':') {
					seg = ":dynamic";
				}
				if (node.isEmpty() || !ARStringUtil.equals(node.lastChild().getLabel(), seg))
					node.add(new SegmentNode(seg, i));
				node = node.lastChild();
			}
		}
	}
	private int calcIndex(String[] segments, SegmentNode node, int level) {
		if (node.size() <= 1 || segments.length == level)
			return node.getIndex();
		String seg = segments[level];
		for (SegmentNode item : node.getChildNodes()) {
			if (ARStringUtil.equals(item.getLabel(), ":dynamic") || ARStringUtil.equals(item.getLabel(), seg)) {
				return calcIndex(segments, item, level + 1);
			}
		}
		return node.getIndex();
	}

    @Override
	public Options recognize(String path, String method) {
		String[] segments = toPlainSegments(path);

		int index = calcIndex(segments, tree, 0);
		while (index < routes.size()) {
			Options result = routes.get(index).recognize(path, method);
			if (result != null) return result;
			index += 1;
		}
		throw new RoutingException("No route matches " + path);
	}

	private class SegmentNode {
		private int index;
		private String label;
		private List<SegmentNode> childNodes;

		public SegmentNode(int index) {
			this(null, index);
		}

		public SegmentNode(String label, int index) {
			this.index = index;
			this.label = label;
			childNodes = new ArrayList<SegmentNode>();
		}

		public void add(SegmentNode child) {
			childNodes.add(child);
		}

		public boolean isEmpty() {
			return childNodes.isEmpty();
		}

		public SegmentNode lastChild() {
			if (isEmpty())
				return null;
			return childNodes.get(childNodes.size() - 1);
		}

		public String getLabel() {
			return label;
		}

		public int getIndex() {
			return index;
		}

		public List<SegmentNode> getChildNodes() {
			return childNodes;
		}
		public int size() {
			return childNodes.size();
		}
	}
}
