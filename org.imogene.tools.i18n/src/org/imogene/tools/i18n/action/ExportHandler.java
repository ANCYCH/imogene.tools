package org.imogene.tools.i18n.action;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.imogene.tools.i18n.extractor.AndroidExtractor;
import org.imogene.tools.i18n.extractor.PropertiesFileExtractor;
import org.imogene.tools.i18n.wizard.ExportPropertiesWizard;

public class ExportHandler extends AbstractHandler {

	private static final String PROPERTIES_EXTENSION = ".properties";
	private static final String XML_EXTENSION = ".xml";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
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
		return null;
	}

	private Shell getShell() {
		return Display.getCurrent().getActiveShell();
	}

}
