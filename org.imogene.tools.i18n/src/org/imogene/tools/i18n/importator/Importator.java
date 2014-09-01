package org.imogene.tools.i18n.importator;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

public interface Importator {

	/* handle the specified sheet */
	public void importProperties(File parent, Table sheet, String fileName);

	/* handle the specified sheet */
	public void importProperties(File parent, Table sheet, String fileName, int startRow, int valueColumn);

	/* handle all the document */
	public void importProperties(IPath path, SpreadsheetDocument docuemnt);
}
