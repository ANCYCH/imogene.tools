package org.imogene.tools.i18n.importator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

public class PropertiesImportator implements Importator {

	@Override
	public void importProperties(Table table, int startingRow, int valueColumnIndex, File destinationFile) {
		try {
			Properties properties = new Properties();
			parseTable(table, startingRow, valueColumnIndex, properties);
			FileOutputStream fos = new FileOutputStream(destinationFile);
			properties.store(fos, "---No Comment---");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseTable(Table table, int startingRow, int valueColumnIndex, Properties properties) {
		int currentRow = startingRow;
		boolean stop = false;
		while (!stop) {
			Cell keyCell = table.getCellByPosition(0, currentRow);
			Cell valueCell = table.getCellByPosition(valueColumnIndex, currentRow);
			String key = keyCell.getStringValue();
			String value = valueCell.getStringValue();
			if (key != null && !key.equals("")) {
				properties.put(key, value);
				currentRow++;
			} else {
				stop = true;
			}
		}
	}

}
