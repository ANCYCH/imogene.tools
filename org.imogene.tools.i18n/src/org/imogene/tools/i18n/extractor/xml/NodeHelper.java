package org.imogene.tools.i18n.extractor.xml;

import java.util.Date;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class NodeHelper {

	public static String getAttributes(NamedNodeMap atts, String name) {
		Node att = atts.getNamedItem(name);
		if (att == null)
			return "";
		else
			return att.getNodeValue();
	}

	public static String getNodeString(Node node) {
		return node.getFirstChild().getNodeValue();
	}

	public static Date getNodeDate(Node node) {
		String value = node.getFirstChild().getNodeValue();
		Date res = null;
		try {
			res = new Date(Long.parseLong(value));
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		return res;
	}

	public static int getNodeInt(Node node) throws NumberFormatException {
		String value = node.getFirstChild().getNodeValue();
		return Integer.parseInt(value);
	}

	public static long getNodeLong(Node node) throws NumberFormatException {
		String value = node.getFirstChild().getNodeValue();
		return Long.parseLong(value);
	}

	public static double getNodeDouble(Node node) throws NumberFormatException {
		String value = node.getFirstChild().getNodeValue();
		return Double.parseDouble(value);
	}

	public static boolean getNodeBoolean(Node node) {
		String value = node.getFirstChild().getNodeValue();
		return Boolean.parseBoolean(value);
	}
}
