package org.imogene.tools.i18n.importator;

import java.io.File;

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

import org.eclipse.core.runtime.IPath;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AndroidImportator implements Importator {

	private static int DEFAULT_START_ROW = 3;
	private static int DEFAULT_VALUE_INDEX = 2;

	public void importProperties(File parent, Table sheet, String fileName) {
		importProperties(parent, sheet, fileName, DEFAULT_START_ROW, DEFAULT_VALUE_INDEX);
	}

	@Override
	public void importProperties(File parent, Table sheet, String fileName, int startRow, int valueColumn) {
		try {
			Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			/* create a ROOT element */
			Element root = dom.createElement("resources");
			dom.appendChild(root);
			/* create the DOM */
			parseTable(dom, root, sheet, startRow, valueColumn);
			writeDomToFile(new File(parent, fileName), dom);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void importProperties(IPath path, SpreadsheetDocument document) {
		// TODO Auto-generated method stub
	}

	/**
	 * Create a DOM from the sheet
	 * 
	 * @param dom the parent DOM
	 * @param rootthe root element
	 * @param sheet the spreadsheet
	 */
	private void parseTable(Document dom, Element root, Table sheet, int startRow, int valueColumn) {
		boolean stop = false;
		while (!stop) {
			Cell keyCell = sheet.getCellByPosition(0, startRow);
			Cell valueCell = sheet.getCellByPosition(valueColumn, startRow);
			String key = keyCell.getStringValue();
			String value = cleanString(valueCell.getStringValue());
			if (key != null && !key.equals("")) {
				/* xml */
				Element tag = dom.createElement("string");
				tag.setAttribute("name", key);
				tag.appendChild(dom.createTextNode(value));
				root.appendChild(tag);
				startRow++;
			} else {
				stop = true;
			}
		}
	}

	private String cleanString(String value) {
		String cleaned = value.replace("'", "\\'");
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
