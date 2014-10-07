package org.imogene.tools.i18n.wizard;

import java.io.File;
import java.util.List;

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
		final IContainer selection = (IContainer) structuredSelection.getFirstElement();
		final String fileName = defaultPage.getFileName();

		final List<Table> tables;
		final Table table;
		final boolean importAllSheets = defaultPage.isSelectAllSheetsEnabled();
		if (importAllSheets) {
			try {
				table = null;
				tables = getTableList(defaultPage.getSourceName());
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), "Error", "Error reading spreadsheet " + e.getMessage());
				return true;
			}
		} else {
			try {
				tables = null;
				table = getTable(defaultPage.getSourceName(), defaultPage.getSheetName());
			} catch (Exception e) {
				MessageDialog.openError(getShell(), "Error", "Error reading spreadsheet " + e.getMessage());
				return true;
			}
		}

		boolean hasDefault = defaultPage.hasDefaultValues();
		final int startingRow = hasDefault ? defaultPage.getStartingRow() : Importator.DEFAULT_STARTING_ROW;
		final int valueColumnIndex = hasDefault ? defaultPage.getValueColumn() : Importator.DEFAULT_VALUE_COLUMN;

		Job job = new Job("Import Job") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (importAllSheets) {
					monitor.beginTask("Importing properties", tables.size());
					for (Table table : tables) {
						File destFile = new File(selection.getLocation().toOSString(), table.getTableName());
						operator.importProperties(table, startingRow, valueColumnIndex, destFile);
						monitor.worked(1);
					}
				} else {
					monitor.beginTask("Importing properties", 1);
					File destFile = new File(selection.getLocation().toOSString(), fileName);
					operator.importProperties(table, startingRow, valueColumnIndex, destFile);
					monitor.worked(1);
				}
				try {
					selection.refreshLocal(IResource.DEPTH_ONE, monitor);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
		return true;
	}

	private Importator getImportator() {
		switch (defaultPage.getType()) {
		case ImportPropertiesWizardPage.ANDROID_FORMAT:
			boolean array = defaultPage.getAndroidFormat() == ImportPropertiesWizardPage.ANDROID_STRING_ARRAY_FORMAT;
			return new AndroidImportator(array);
		case ImportPropertiesWizardPage.PROPERTIES_FORMAT:
			return new PropertiesImportator();
		}
		return null;
	}

	public Table getTable(String documentPath, String tableName) throws Exception {
		SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(documentPath);
		return doc.getTableByName(tableName);
	}

	public List<Table> getTableList(String documentPath) throws Exception {
		SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(documentPath);
		return doc.getTableList();
	}

}
