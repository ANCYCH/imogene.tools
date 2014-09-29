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
import org.odftoolkit.simple.table.Table;

public class ImportationJob extends Job {

	private static final String NAME = "Importation task";

	private final IContainer selectedFolder;
	private final Table table;
	private final String fileName;
	private final Importator importator;
	private final int startingRow;
	private final int valueColumn;

	public ImportationJob(IContainer selectedFolder, Table table, String fileName, Importator importator,
			int startingRow, int valueColumn) {
		super(NAME);
		this.selectedFolder = selectedFolder;
		this.table = table;
		this.fileName = fileName;
		this.importator = importator;
		this.startingRow = startingRow;
		this.valueColumn = valueColumn;
	}

	public ImportationJob(IContainer selectedFolder, Table table, String fileName, Importator importator) {
		this(selectedFolder, table, fileName, importator, Importator.DEFAULT_START_ROW, Importator.DEFAULT_VALUE_INDEX);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Importing properties", 100);
		File destFolder = new File(selectedFolder.getLocation().toOSString());
		importator.importProperties(destFolder, table, fileName, startingRow, valueColumn);
		try {
			selectedFolder.refreshLocal(IResource.DEPTH_ONE, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}

}
