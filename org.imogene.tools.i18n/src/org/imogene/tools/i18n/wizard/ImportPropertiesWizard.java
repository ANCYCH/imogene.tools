package org.imogene.tools.i18n.wizard;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.imogene.tools.i18n.importator.AndroidImportator;
import org.imogene.tools.i18n.importator.Importator;
import org.imogene.tools.i18n.importator.PropertiesImportator;
import org.imogene.tools.i18n.jobs.ImportJob;
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
		final Importator operator = getImportator();
		if (operator == null) {
			MessageDialog.openWarning(getShell(), "Warning", "Format not supported");
			return true;
		}
		IContainer selection = (IContainer) structuredSelection.getFirstElement();
		ImportJob job = new ImportJob(selection, defaultPage.getFileName(), operator);
		job.setUser(true);
		job.schedule();
		return true;
	}

	private Importator getImportator() {
		switch (defaultPage.getType()) {
		case ImportPropertiesWizardPage.ANDROID_FORMAT:
			try {
				AndroidImportator importator = new AndroidImportator();
				importator
						.setArray(defaultPage.getAndroidFormat() == ImportPropertiesWizardPage.ANDROID_STRING_ARRAY_FORMAT);
				importator.setSheet(getTable(defaultPage.getSheetName(), defaultPage.getSourceName()));
				if (defaultPage.hasDefaultValues()) {
					importator.setStartingRow(defaultPage.getStartingRow());
					importator.setValueColumn(defaultPage.getValueColumn());
				}
				return importator;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		case ImportPropertiesWizardPage.PROPERTIES_FORMAT:
			return new PropertiesImportator();
		}
		return null;
	}

	public Table getTable(String tableName, String documentPath) throws Exception {
		SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(documentPath);
		return doc.getTableByName(tableName);
	}

}
