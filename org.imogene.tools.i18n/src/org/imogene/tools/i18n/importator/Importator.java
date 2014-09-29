package org.imogene.tools.i18n.importator;

import java.io.File;

import org.odftoolkit.simple.table.Table;

public interface Importator {

	public static final int DEFAULT_START_ROW = 0;
	public static final int DEFAULT_VALUE_INDEX = 1;

	/**
	 * Handle the specified sheet
	 * @param parent
	 * @param sheet
	 * @param fileName
	 * @param startRow
	 * @param valueColumn
	 */
	public void importProperties(File parent, Table sheet, String fileName, int startRow, int valueColumn);

}
