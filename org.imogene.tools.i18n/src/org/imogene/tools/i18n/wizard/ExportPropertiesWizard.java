package org.imogene.tools.i18n.wizard;

import java.io.File;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.imogene.tools.i18n.extractor.Extractor;
import org.imogene.tools.i18n.utils.SpreadsheetHelper;

public class ExportPropertiesWizard extends Wizard {

	private final IStructuredSelection structureSelection;
	private final ExportPropertiesWizardPage defaultPage;

	private Extractor extractor;

	public ExportPropertiesWizard(IStructuredSelection structureSelection) {
		super();
		this.structureSelection = structureSelection;
		this.defaultPage = new ExportPropertiesWizardPage(structureSelection);
		addPage(defaultPage);
	}

	public void setExtractor(Extractor extractor) {
		this.extractor = extractor;
	}

	@Override
	public boolean performFinish() {
		final IFile selectedFile = (IFile) structureSelection.getFirstElement();
		final File file = new File(defaultPage.getPath());
		final String sheetName = defaultPage.useDefaultSheetName() ? selectedFile.getName() : defaultPage
				.getSheetName();

		Job job = new Job("Export Job") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Extracting properties", 100);
				Properties p = extractor.extract(selectedFile);
				monitor.worked(50);
				SpreadsheetHelper.createOutput(file, sheetName, p);
				monitor.worked(50);
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
		return true;
	}

}
