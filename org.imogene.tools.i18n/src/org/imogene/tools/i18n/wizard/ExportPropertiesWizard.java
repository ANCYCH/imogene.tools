package org.imogene.tools.i18n.wizard;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.imogene.tools.i18n.extractor.Extractor;
import org.imogene.tools.i18n.jobs.ExtractionJob;

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
		IFile selectedFile = (IFile) structureSelection.getFirstElement();
		File file = new File(defaultPage.getPath());
		boolean useDefaultSheetName = defaultPage.useDefaultSheetName();
		String sheetName = useDefaultSheetName ? selectedFile.getName() : defaultPage.getSheetName();

		ExtractionJob job = new ExtractionJob(selectedFile, file, sheetName, extractor);
		job.setUser(true);
		job.schedule();
		return true;
	}

}
