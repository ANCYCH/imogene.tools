package org.imogene.tools.i18n.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.imogene.tools.i18n.importator.AndroidImportator;
import org.imogene.tools.i18n.importator.Importator;
import org.imogene.tools.i18n.importator.PropertiesImportator;
import org.imogene.tools.i18n.jobs.ImportationJob;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

public class ImportPropertiesWizard extends Wizard {

	private final IStructuredSelection structuredSelection;
	private final ImportPropertiesWizardPage defaultPage;

	public ImportPropertiesWizard(IStructuredSelection structuredSelection) {
		super();
		this.structuredSelection = structuredSelection;
		this.defaultPage = new ImportPropertiesWizardPage();
		addPage(defaultPage);
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
			IContainer selection = (IContainer) structuredSelection.getFirstElement();
			try {
				Table table = getTable(defaultPage.getSheetName(), defaultPage.getSourceName());
				ImportationJob job;
				if (defaultPage.hasDefaultValues()) {
					job = new ImportationJob(selection, table, defaultPage.getFileName(), operator,
							defaultPage.getStartingRow(), defaultPage.getValueColumn());
				} else {
					job = new ImportationJob(selection, table, defaultPage.getFileName(), operator);
				}
				job.setUser(true);
				job.schedule();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			MessageDialog.openWarning(getShell(), "Warning", "Format not supported: " + type);
		}
		return true;
	}

	public Table getTable(String tableName, String documentPath) throws Exception {
		SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(documentPath);
		return doc.getTableByName(tableName);
	}

}
