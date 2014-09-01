package org.imogene.tools.i18n.popup.actions;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.imogene.tools.i18n.wizard.ImportPropertiesWizard;

public class SpreadsheetToPropertiesAction implements IObjectActionDelegate {

	private IStructuredSelection selection;

	private IContainer selectedFolder;

	@Override
	public void run(IAction action) {
		ImportPropertiesWizard w = new ImportPropertiesWizard();
		w.setSelection(selectedFolder);
		WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), w);
		dialog.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection s) {
		selection = (IStructuredSelection) s;
		selectedFolder = (IContainer) selection.getFirstElement();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

}
