package org.imogene.tools.i18n.wizard;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.imogene.tools.i18n.importator.AndroidImportator;
import org.imogene.tools.i18n.importator.Importator;
import org.imogene.tools.i18n.importator.PropertiesImportator;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

public class ImportPropertiesWizard extends Wizard {

	private ImportPropertiesWizardPage defaultPage;

	private IContainer selection;

	public ImportPropertiesWizard() {
		super();
		defaultPage = new ImportPropertiesWizardPage();
		addPage(defaultPage);
	}

	public void setSelection(IContainer selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		int type = defaultPage.getType();
		Importator operator = null;
		switch (type) {
		case ImportPropertiesWizardPage.ANDROID_FORMAT:
			operator = new AndroidImportator();
			break;
		case ImportPropertiesWizardPage.PROPERTIES_FORMAT:
			operator = new PropertiesImportator();
			break;
		}
		if (operator != null) {
			try {
				Table table = getTable(defaultPage.getSheetName(), defaultPage.getPath());
				operator.importProperties(new File(selection.getLocation().toOSString()), table,
						defaultPage.getFileName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			MessageDialog.openWarning(getShell(), "Warning", "Format not supported: " + type);
		}
		try {
			selection.refreshLocal(IResource.DEPTH_ZERO, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return true;
	}

	public Table getTable(String tableName, String documentPath) throws Exception {
		SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(documentPath);
		return doc.getTableByName(tableName);
	}

}
