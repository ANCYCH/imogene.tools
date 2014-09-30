package org.imogene.tools.i18n.importator;

import java.io.File;

public interface Importator {

	public static final int DEFAULT_STARTING_ROW = 0;
	public static final int DEFAULT_VALUE_COLUMN = 1;

	/**
	 * Handle the specified sheet
	 * 
	 * @param parent
	 * @param sheet
	 * @param fileName
	 * @param startRow
	 * @param valueColumn
	 */
	public void importProperties(File destinationFile);

}
