package org.imogene.tools.i18n.importator;

import java.io.File;

import org.odftoolkit.simple.table.Table;

public interface Importator {

	public static final int DEFAULT_STARTING_ROW = 0;
	public static final int DEFAULT_VALUE_COLUMN = 1;

	/**
	 * Handle the specified sheet
	 * 
	 * @param table
	 * @param startingRow
	 * @param valueColumnIndex
	 * @param destinationFile
	 */
	public void importProperties(Table table, int startingRow, int valueColumnIndex, File destinationFile);

}
