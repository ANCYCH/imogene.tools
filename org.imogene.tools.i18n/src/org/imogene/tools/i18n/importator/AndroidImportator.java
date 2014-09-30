package org.imogene.tools.i18n.importator;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AndroidImportator implements Importator {

	private static final String REGEX = "(.*)(-\\d+)";
	private static final Pattern PATTERN = Pattern.compile(REGEX);

	private final HashMap<String, Element> elementsMap = new HashMap<String, Element>();

	private Table sheet;
	private int startingRow = Importator.DEFAULT_STARTING_ROW;
	private int valueColumn = Importator.DEFAULT_VALUE_COLUMN;
	private boolean array;

	public void setSheet(Table sheet) {
		this.sheet = sheet;
	}

	public void setStartingRow(int startingRow) {
		this.startingRow = startingRow;
	}

	public void setValueColumn(int valueColumn) {
		this.valueColumn = valueColumn;
	}

	public void setArray(boolean array) {
		this.array = array;
	}

	@Override
	public void importProperties(File destinationFile) {
		try {
			Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			/* create a ROOT element */
			Element root = dom.createElement("resources");
			dom.appendChild(root);
			/* create the DOM */
			parseTable(dom, root);
			writeDomToFile(destinationFile, dom);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a DOM from the sheet
	 * 
	 * @param dom the parent DOM
	 * @param rootthe root element
	 * @param sheet the spreadsheet
	 */
	private void parseTable(Document dom, Element root) {
		elementsMap.clear();
		int currentRow = startingRow;
		boolean stop = false;
		while (!stop) {
			Cell keyCell = sheet.getCellByPosition(0, currentRow);
			Cell valueCell = sheet.getCellByPosition(valueColumn, currentRow);
			String key = keyCell.getStringValue();
			String value = cleanString(valueCell.getStringValue());
			if (key != null && !key.equals("")) {
				if (array) {
					Matcher m = PATTERN.matcher(key);
					if (m.find()) {
						String arrayKey = m.group(1);
						Element tag = elementsMap.get(arrayKey);
						if (tag == null) {
							tag = dom.createElement("string-array");
							tag.setAttribute("name", arrayKey);
							root.appendChild(tag);
							elementsMap.put(arrayKey, tag);
						}
						Element item = dom.createElement("item");
						item.appendChild(dom.createTextNode(value));
						tag.appendChild(item);
					}
				} else {
					Element tag = dom.createElement("string");
					tag.setAttribute("name", key);
					tag.appendChild(dom.createTextNode(value));
					root.appendChild(tag);
				}
				currentRow++;
			} else {
				stop = true;
			}
		}
	}

	private String cleanString(String value) {
		String cleaned = value.replace("'", "\'");
		if (cleaned.startsWith("\"")) {
			cleaned = cleaned.substring(1);
		}
		if (cleaned.endsWith("\"")) {
			cleaned = cleaned.substring(0, cleaned.length() - 1);
		}
		return cleaned;
	}

	/**
	 * Write tehXML to the file
	 * 
	 * @param dest the file
	 * @param dom the XML document
	 */
	private void writeDomToFile(File dest, Document dom) {
		dom.setXmlVersion("1.0");
		Source source = new DOMSource(dom);
		Result result = new StreamResult(dest);
		Transformer xformer;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

}
