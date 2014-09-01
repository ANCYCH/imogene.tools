package org.imogene.tools.i18n.jobs;

import java.io.File;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.imogene.tools.i18n.extractor.Extractor;
import org.imogene.tools.i18n.utils.SpreadsheetHelper;

public class ExtractionJob extends Job {

	private static final String NAME = "Extraction task";

	private final IFile selectedFile;
	private final File destinationFile;
	private final String sheetName;
	private final Extractor extractor;

	public ExtractionJob(IFile selectedFile, File destinationFile, String sheetName, Extractor extractor) {
		super(NAME);
		this.selectedFile = selectedFile;
		this.destinationFile = destinationFile;
		this.sheetName = sheetName;
		this.extractor = extractor;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Extracting properties", 100);
		Properties p = extractor.extract(selectedFile);
		monitor.worked(50);
		SpreadsheetHelper.createOutput(destinationFile, sheetName, p);
		monitor.worked(50);
		return Status.OK_STATUS;
	}

}
