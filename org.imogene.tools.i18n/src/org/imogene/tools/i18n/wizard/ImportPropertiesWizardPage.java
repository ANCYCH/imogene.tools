package org.imogene.tools.i18n.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

public class ImportPropertiesWizardPage extends WizardPage implements SelectionListener {

	private static final String NAME = "transImport";

	private static final String TITLE = "Import translation";

	private static final String DESCRIPTION = "Set the import options";

	public static final int ANDROID_FORMAT = 0;

	public static final int PROPERTIES_FORMAT = 1;

	public static final int UNKNOWN_FORMAT = -1;

	public static final int ANDROID_STRING_FORMAT = 1;

	public static final int ANDROID_AREA_FORMAT = 2;

	/* translation type options */
	private Group typeGroup;
	private String path = System.getProperty("user.home");
	private Text sourceName;
	private Button fileSelection;
	private Button androidButton;
	private Button propertiesButton;

	/* android format group */
	private Group androidGroup;
	private Button stringFormatButton;
	private Button areaFormatButton;

	/* output option */
	private Group output;
	private Combo sheetList;
	private Text fileName;

	public ImportPropertiesWizardPage() {
		super(NAME);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());

		/* destination group */
		typeGroup = new Group(main, SWT.NONE);
		typeGroup.setText("Type");
		typeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeGroup.setLayout(new GridLayout(3, false));

		Label sourceNameLabel = new Label(typeGroup, SWT.NONE);
		sourceNameLabel.setText("Source: ");
		sourceName = new Text(typeGroup, SWT.BORDER);
		sourceName.setEditable(false);
		sourceName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sourceName.setText(path);

		fileSelection = new Button(typeGroup, SWT.PUSH);
		fileSelection.setText("Select");
		fileSelection.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[] { "*.ods" });
				fd.setFilterPath(System.getProperty("user.home"));
				path = fd.open();
				sourceName.setText(path);
				populateSheetList();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		/* destination type */
		androidButton = new Button(typeGroup, SWT.RADIO);
		androidButton.setText("Android resource.");
		propertiesButton = new Button(typeGroup, SWT.RADIO);
		propertiesButton.setText("Properties file.");
		propertiesButton.setSelection(true);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		propertiesButton.setLayoutData(gridData);
		androidButton.addSelectionListener(this);
		propertiesButton.addSelectionListener(this);

		/* android format group */
		androidGroup = new Group(main, SWT.NONE);
		androidGroup.setText("Android format");
		androidGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		androidGroup.setLayout(new GridLayout(2, false));
		stringFormatButton = new Button(androidGroup, SWT.RADIO);
		stringFormatButton.setText("<string>");
		stringFormatButton.setSelection(true);
		areaFormatButton = new Button(androidGroup, SWT.RADIO);
		areaFormatButton.setText("<string-area>");
		androidGroup.setEnabled(false);

		/* file group */
		output = new Group(main, SWT.NONE);
		output.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		output.setLayout(new GridLayout(2, false));
		output.setText("File properties: ");

		Label comboLabel = new Label(output, SWT.NONE);
		comboLabel.setText("Sheet: ");
		sheetList = new Combo(output, SWT.READ_ONLY | SWT.DROP_DOWN);
		sheetList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sheetList.addSelectionListener(this);

		Label fileNameLabel = new Label(output, SWT.NONE);
		fileNameLabel.setText("File name: ");
		fileName = new Text(output, SWT.BORDER);
		fileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setControl(main);
	}

	public String getPath() {
		return path;
	}

	public int getType() {
		if (androidButton.getSelection())
			return ANDROID_FORMAT;
		if (propertiesButton.getSelection())
			return PROPERTIES_FORMAT;
		return UNKNOWN_FORMAT;
	}

	public int getAndroidFormat() {
		if (stringFormatButton.getSelection())
			return ANDROID_STRING_FORMAT;
		return ANDROID_AREA_FORMAT;
	}

	public String getSheetName() {
		int selectedIndex = sheetList.getSelectionIndex();
		if (selectedIndex > -1) {
			return sheetList.getItem(selectedIndex);
		}
		return null;
	}

	public String getFileName() {
		return fileName.getText();
	}

	private void populateSheetList() {
		sheetList.removeAll();
		String sourcePath = sourceName.getText();
		if (!"".equals(sourcePath)) {
			SpreadsheetDocument doc;
			try {
				doc = SpreadsheetDocument.loadDocument(sourceName.getText());
				for (Table table : doc.getTableList()) {
					sheetList.add(table.getTableName());
				}
				doc.close();
			} catch (OdfValidationException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Object source = e.getSource();
		if (androidButton.equals(source) || propertiesButton.equals(source)) {
			boolean enabled = androidButton.getSelection();
			androidGroup.setEnabled(enabled);
			stringFormatButton.setEnabled(enabled);
			areaFormatButton.setEnabled(enabled);
		}
		if (sheetList.equals(source)) {
			setPageComplete(sheetList.getSelectionIndex() != -1);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

}
