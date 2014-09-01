package org.imogene.tools.i18n.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExportPropertiesWizardPage extends WizardPage {

	private static final String NAME = "output";

	private static final String TITLE = "Extract translations";

	private static final String DESCRIPTION = "Set the output options";

	private final IStructuredSelection structureSelection;

	private Composite sheetNameComponent;
	private Button useDefaultName;

	private Text fileName;
	private Text sheetName;

	public ExportPropertiesWizardPage(IStructuredSelection structureSelection) {
		super(NAME);
		this.structureSelection = structureSelection;
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());

		createDestinationGroup(main);
		createDestinationSheetGroup(main);

		setControl(main);
	}

	private void createDestinationGroup(Composite parent) {
		Composite layout = new Composite(parent, SWT.NULL);
		layout.setLayout(new GridLayout(3, false));
		layout.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createExportComponentGroup(layout);
	}

	private void createExportComponentGroup(Composite parent) {
		Label fileLabel = new Label(parent, SWT.NONE);
		fileLabel.setText("Destination:");

		fileName = new Text(parent, SWT.BORDER);
		fileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});

		Button fileSelection = new Button(parent, SWT.PUSH);
		fileSelection.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
				fd.setFileName("export.ods");
				String name = fd.open();
				if (name != null) {
					fileName.setText(name);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		fileSelection.setText("Select");
	}

	private void createDestinationSheetGroup(Composite parent) {
		final Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(1, false));
		group.setText("Destination sheet");
		createDestinatonSheetComponentGroup(group);
	}

	private void createDestinatonSheetComponentGroup(Composite parent) {
		useDefaultName = new Button(parent, SWT.RADIO);
		useDefaultName.setText("Use default file name as name for the sheet");
		useDefaultName.setSelection(true);
		useDefaultName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sheetNameComponent.setEnabled(!useDefaultName.getSelection());
				validatePage();
			}
		});

		Button useDifferentName = new Button(parent, SWT.RADIO);
		useDifferentName.setText("Use a different name");

		sheetNameComponent = new EnhancedComposite(parent, SWT.NULL);
		sheetNameComponent.setLayout(new GridLayout(2, false));
		sheetNameComponent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createDestinationSheetNameComponentGroup(sheetNameComponent);

		sheetNameComponent.setEnabled(false);
	}

	private void createDestinationSheetNameComponentGroup(Composite parent) {
		Label sheetLabel = new Label(parent, SWT.NONE);
		sheetLabel.setText("Destination sheet:");

		IFile selectedFile = (IFile) structureSelection.getFirstElement();
		sheetName = new Text(parent, SWT.BORDER);
		sheetName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sheetName.setText(selectedFile.getName());
		sheetName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
	}

	private void validatePage() {
		String fileNameString = fileName.getText();
		if (fileNameString == null || fileNameString.length() < 1) {
			setErrorMessage("Destination file name is invalid.");
			setPageComplete(false);
			return;
		}
		if (!fileNameString.endsWith(".ods")) {
			setErrorMessage("Destination file name should end with .ods.");
			setPageComplete(false);
			return;
		}
		if (!useDefaultName.getSelection()) {
			String sheetNameString = sheetName.getText();
			if (sheetNameString == null || sheetNameString.length() < 1) {
				setErrorMessage("Destination sheet name is invalid.");
				setPageComplete(false);
				return;
			}
		}
		setErrorMessage(null);
		setPageComplete(true);
	}

	public boolean useDefaultSheetName() {
		return useDefaultName.getSelection();
	}

	public String getSheetName() {
		return sheetName.getText();
	}

	public String getPath() {
		return fileName.getText();
	}

	private static class EnhancedComposite extends Composite {

		public EnhancedComposite(final Composite parent, final int style) {
			super(parent, style);
		}

		@Override
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			for (Control child : getChildren()) {
				child.setEnabled(enabled);
			}
		}
	}

}
