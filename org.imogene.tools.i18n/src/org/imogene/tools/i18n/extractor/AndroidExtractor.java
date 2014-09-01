package org.imogene.tools.i18n.extractor;

import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.imogene.tools.i18n.extractor.xml.NodeHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AndroidExtractor implements Extractor {

	private static final String STRING_TAG = "string";

	private static final String STRING_ARRAY_TAG = "string-array";

	private static final String ITEM_TAG = "item";

	private static final String NAME_PROP = "name";

	@Override
	public Properties extract(IFile selection) {
		Properties p = new Properties();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(selection.getContents());
			handleStringArrayTag(doc, p);
			handleStringTag(doc, p);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return p;
	}

	private void handleStringTag(Document doc, Properties p) {
		NodeList list = doc.getElementsByTagName(STRING_TAG);
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			String key = NodeHelper.getAttributes(node.getAttributes(), NAME_PROP);
			String value = NodeHelper.getNodeString(node);
			p.put(key, value != null ? value : "");
		}
	}

	private void handleStringArrayTag(Document doc, Properties p) {
		NodeList list = doc.getElementsByTagName(STRING_ARRAY_TAG);
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			String key = NodeHelper.getAttributes(node.getAttributes(), NAME_PROP);
			NodeList items = node.getChildNodes();
			int index = 0;
			for (int j = 0; j < items.getLength(); j++) {
				Node nItem = items.item(j);
				if (ITEM_TAG.equals(nItem.getNodeName())) {
					String value = NodeHelper.getNodeString(nItem);
					p.put(key + "-" + index, value);
					index++;
				}
			}
		}

	}

}
