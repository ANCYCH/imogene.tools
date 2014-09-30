package org.imogene.tools.i18n.jobs;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.imogene.tools.i18n.importator.Importator;

public class ImportJob extends Job {

	private static final String NAME = "Importation task";

	private final IContainer selectedFolder;
	private final String fileName;
	private final Importator importator;

	public ImportJob(IContainer selectedFolder, String fileName, Importator importator) {
		super(NAME);
		this.selectedFolder = selectedFolder;
		this.fileName = fileName;
		this.importator = importator;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Importing properties", 100);
		File destinationFile = new File(selectedFolder.getLocation().toOSString(), fileName);
		importator.importProperties(destinationFile);
		try {
			selectedFolder.refreshLocal(IResource.DEPTH_ONE, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}

}
