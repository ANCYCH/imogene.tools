package org.imogene.tools.i18n.utils;

import java.io.File;
import java.util.Properties;
import java.util.Set;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

public class SpreadsheetHelper {

	/**
	 * Add the given properties to the file at the table name specified.
	 * 
	 * @param dest The destination file
	 * @param name The table name containing the properties
	 * @param p The Properties
	 */
	public static void createOutput(File dest, String name, Properties p) {
		try {
			SpreadsheetDocument doc = null;
			boolean isNew = true;
			if (dest.exists()) {
				try {
					doc = SpreadsheetDocument.loadDocument(dest);
					isNew = false;
				} catch (Exception e) {
					doc = SpreadsheetDocument.newSpreadsheetDocument();
				}
			} else {
				doc = SpreadsheetDocument.newSpreadsheetDocument();
			}
			Table table = null;
			if (isNew) {
				table = doc.getSheetByIndex(0);
			} else {
				table = doc.getTableByName(name);
				if (table == null) {
					table = doc.addTable();
				}
			}
			table.setTableName(name);
			Set<Object> keys = p.keySet();
			int row = 0;
			for (Object o : keys) {
				String k = (String) o;
				Cell cKey = table.getCellByPosition(0, row);
				cKey.setStringValue(k);
				Cell cValue = table.getCellByPosition(1, row);
				cValue.setStringValue(p.getProperty(k));
				row++;
			}
			doc.save(dest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
