package org.imogene.tools.i18n.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.imogene.tools.i18n.extractor.AndroidExtractor;
import org.imogene.tools.i18n.extractor.PropertiesFileExtractor;
import org.imogene.tools.i18n.wizard.ExportPropertiesWizard;

public class PropertiesToSpreadsheetAction implements IObjectActionDelegate {

	private static final String PROPERTIES_EXTENSION = ".properties";

	private static final String XML_EXTENSION = ".xml";

	private IStructuredSelection selection;

	@Override
	public void run(IAction action) {
		ExportPropertiesWizard w = new ExportPropertiesWizard(selection);
		WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), w);

		IFile selectedFile = (IFile) (selection.getFirstElement());
		if (selectedFile.getName().endsWith(PROPERTIES_EXTENSION)) {
			w.setExtractor(new PropertiesFileExtractor());
			dialog.open();
		} else if (selectedFile.getName().endsWith(XML_EXTENSION)) {
			w.setExtractor(new AndroidExtractor());
			dialog.open();
		} else {
			MessageDialog.openWarning(getShell(), "Not supported", "Extraction is not supported for this file format");
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection s) {
		selection = (IStructuredSelection) s;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

	private Shell getShell() {
		return Display.getCurrent().getActiveShell();
	}

}
