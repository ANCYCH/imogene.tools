package org.imogene.tools.i18n.action;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.imogene.tools.i18n.wizard.ImportPropertiesWizard;

public class ImportHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selectedFolder = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		ImportPropertiesWizard w = new ImportPropertiesWizard(selectedFolder);
		WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), w);
		dialog.open();

		// TODO check that it is a folder
		// IFile selectedFile = (IFile) (selection.getFirstElement());
		// if (selectedFile.getName().endsWith(PROPERTIES_EXTENSION)) {
		// } else {
		// MessageDialog.openWarning(getShell(), "Not supported", "Extraction is not supported for this file format");
		// }
		return null;
	}

}
