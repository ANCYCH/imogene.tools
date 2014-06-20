package org.imogene.tools.gwt.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.imogene.tools.gwt.GWTToolsPlugin;

import com.google.gdt.eclipse.core.SWTUtilities;
import com.google.gdt.eclipse.core.StatusUtilities;
import com.google.gdt.eclipse.platform.shared.ui.IPixelConverter;
import com.google.gdt.eclipse.platform.ui.PixelConverterFactory;
import com.google.gwt.eclipse.core.GWTPlugin;
import com.google.gwt.eclipse.core.launch.ui.EntryPointModulesSelectionBlock.IModulesChangeListener;
import com.google.gwt.eclipse.core.modules.IModule;

public class SDMLaunchConfigurationDialog extends TitleAreaDialog {

	private class FieldListener implements ModifyListener, ISelectionChangedListener, IModulesChangeListener {
		public void modifyText(ModifyEvent e) {
			fieldChanged();
		}

		public void onModulesChanged() {
			fieldChanged();
		}

		public void selectionChanged(SelectionChangedEvent event) {
			fieldChanged();
		}
	}

	private static int convertSeverity(IStatus status) {
		switch (status.getSeverity()) {
		case IStatus.ERROR:
			return IMessageProvider.ERROR;
		case IStatus.WARNING:
			return IMessageProvider.WARNING;
		case IStatus.INFO:
			return IMessageProvider.INFORMATION;
		default:
			return IMessageProvider.NONE;
		}
	}

	private Composite advancedContainer;

	private Button buildButton;

	private IProject project;

	private String launcher;

	private Text launcherText;

	private String extraArgs;

	private Text extraArgsText;

	private String vmArgs;

	private Text vmArgsText;

	private final FieldListener listener = new FieldListener();

	private String module;

	private Text moduleText;

	private Button chooseModuleButton;

	public SDMLaunchConfigurationDialog(Shell shell, IProject project) {
		super(shell);
		this.project = project;
	}

	public IProject getProject() {
		return project;
	}

	public String getLauncher() {
		return launcher;
	}

	public String getVmArgs() {
		return vmArgs;
	}

	public void setExtraArgs(String extraArgs) {
		this.extraArgs = extraArgs;
	}

	public String getExtraArgs() {
		return extraArgs;
	}

	public String getModule() {
		return module;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		buildButton = getButton(IDialogConstants.OK_ID);

		// Re-label the OK button and set it as default
		buildButton.setText("Build");
		getShell().setDefaultButton(buildButton);

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent = (Composite) super.createDialogArea(parent);

		Composite container = new Composite(parent, SWT.NONE);
		GridData containerGridData = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1);
		container.setLayoutData(containerGridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 8;
		gridLayout.marginWidth = 8;
		container.setLayout(gridLayout);

		// Module field
		SWTFactory.createLabel(container, "Launcher:", 1);
		launcherText = new Text(container, SWT.BORDER);
		launcherText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createEntryPointModulesComponent(container);
		createAdvancedOptions(container);

		return container;
	}

	private void createEntryPointModulesComponent(Composite parent) {
		Group group = SWTFactory.createGroup(parent, "Entry Point Modules", 3, 3, GridData.FILL_BOTH);
		GridLayout groupLayout = (GridLayout) group.getLayout();
		groupLayout.marginBottom = 8;
		group.setLayout(groupLayout);

		moduleText = new Text(group, SWT.BORDER);
		moduleText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		chooseModuleButton = new Button(group, SWT.NONE);
		chooseModuleButton.setText("Browse...");
	}

	private void createAdvancedOptions(Composite parent) {
		IPixelConverter converter = PixelConverterFactory.createPixelConverter(JFaceResources.getDialogFont());

		// Expandable panel for advanced options
		final ExpandableComposite expandPanel = new ExpandableComposite(parent, SWT.NONE, ExpandableComposite.TWISTIE
				| ExpandableComposite.CLIENT_INDENT);
		expandPanel.setText("Advanced");
		expandPanel.setExpanded(false);
		expandPanel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		GridData expandPanelGridData = new GridData(GridData.FILL, GridData.FILL, true, false, 3, 1);
		expandPanelGridData.verticalIndent = converter.convertHeightInCharsToPixels(1);
		expandPanel.setLayoutData(expandPanelGridData);
		expandPanel.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				Shell shell = getShell();
				shell.setLayoutDeferred(true); // Suppress redraw flickering

				Point size = shell.getSize();
				int shellHeightDeltaOnExpand = advancedContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
				if (expandPanel.isExpanded()) {
					shell.setSize(size.x, size.y + shellHeightDeltaOnExpand);
				} else {
					shell.setSize(size.x, size.y - shellHeightDeltaOnExpand);
				}
				shell.layout(true, true);

				shell.setLayoutDeferred(false);
			}
		});
		advancedContainer = new Composite(expandPanel, SWT.NONE);
		advancedContainer.setLayoutData(new GridData());
		advancedContainer.setFont(parent.getFont());
		advancedContainer.setLayout(new GridLayout(1, false));
		expandPanel.setClient(advancedContainer);

		// Additional compiler parameters field
		SWTFactory.createLabel(advancedContainer, "Additional compiler arguments:", 1);
		extraArgsText = SWTUtilities.createMultilineTextbox(advancedContainer, SWT.BORDER, false);
		GridData extraArgsGridData = new GridData(GridData.FILL_HORIZONTAL);
		extraArgsGridData.heightHint = converter.convertHeightInCharsToPixels(5);
		extraArgsText.setLayoutData(extraArgsGridData);

		// Additional VM args field
		SWTFactory.createLabel(advancedContainer, "VM arguments:", 1);
		vmArgsText = SWTUtilities.createMultilineTextbox(advancedContainer, SWT.BORDER, false);
		GridData vmArgsGridData = new GridData(GridData.FILL_HORIZONTAL);
		vmArgsGridData.heightHint = converter.convertHeightInCharsToPixels(5);
		vmArgsText.setLayoutData(vmArgsGridData);
	}

	private void addEventHandlers() {
		launcherText.addModifyListener(listener);
		moduleText.addModifyListener(listener);
		extraArgsText.addModifyListener(listener);
		vmArgsText.addModifyListener(listener);

		chooseModuleButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IJavaProject javaProject = JavaCore.create(project);
				IModule module = ModuleSelectionDialog.show(Display.getDefault().getActiveShell(), javaProject, false);
				if (module != null) {
					moduleText.setText(module.getQualifiedName());
				}
			}
		});
	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		setTitle("Build");
		initializeControls();
		addEventHandlers();
		fieldChanged();

		return contents;
	}

	private void initializeControls() {
		initializeExtraArgs(extraArgs);
		initializeVmArgs(vmArgs);
	}

	private void initializeExtraArgs(String args) {
		extraArgsText.setText(args != null ? args : "");
	}

	private void initializeVmArgs(String vmArgs) {
		vmArgsText.setText(vmArgs != null ? vmArgs : "");
	}

	private void fieldChanged() {
		IStatus status = updateFields();

		boolean valid = (status.getSeverity() != IStatus.ERROR);
		buildButton.setEnabled(valid);
	}

	private IStatus updateFields() {
		IStatus launcherNameStatus = updateLauncherName();
		IStatus moduleStatus = updateModule();
		IStatus extraArgsStatus = updateExtraArgs();
		IStatus vmArgsStatus = updateVmArgs();

		return updateStatus(new IStatus[] { launcherNameStatus, moduleStatus, extraArgsStatus, vmArgsStatus });
	}

	private IStatus updateLauncherName() {
		launcher = launcherText.getText();
		if (launcher == null || launcher.length() == 0) {
			return StatusUtilities.newErrorStatus("Enter the launcher name", GWTToolsPlugin.PLUGIN_ID);
		}
		return Status.OK_STATUS;
	}

	private IStatus updateModule() {
		module = moduleText.getText();
		if (module == null || module.length() == 0) {
			return StatusUtilities.newErrorStatus("Enter module name or select one", GWTToolsPlugin.PLUGIN_ID);
		}
		return Status.OK_STATUS;
	}

	private IStatus updateExtraArgs() {
		extraArgs = extraArgsText.getText();
		return Status.OK_STATUS;
	}

	private IStatus updateVmArgs() {
		vmArgs = vmArgsText.getText();
		return Status.OK_STATUS;
	}

	private IStatus updateStatus(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			status = StatusUtilities.newOkStatus("Build the SuperDevMode launcher", GWTPlugin.PLUGIN_ID);
		}

		this.setMessage(status.getMessage(), convertSeverity(status));

		return status;
	}

	private IStatus updateStatus(IStatus[] status) {
		return updateStatus(StatusUtilities.getMostImportantStatusWithMessage(status));
	}

}
