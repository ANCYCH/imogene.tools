package org.imogene.tools.i18n.wizard;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.imogene.tools.i18n.importator.Importator;
import org.imogene.tools.i18n.wdgets.EnhancedComposite;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

public class ImportPropertiesWizardPage extends WizardPage {

	private static final String NAME = "transImport";

	private static final String TITLE = "Import translation";

	private static final String DESCRIPTION = "Set the import options";

	public static final int ANDROID_FORMAT = 0;

	public static final int PROPERTIES_FORMAT = 1;

	public static final int UNKNOWN_FORMAT = -1;

	public static final int ANDROID_STRING_FORMAT = 1;

	public static final int ANDROID_STRING_ARRAY_FORMAT = 2;

	// Source group
	private Text sourceName;
	private Button selectAllSheets;
	private Composite sheetListGroup;
	private Combo sheetList;
	private Composite defineValuesGroup;
	private Button defineValuesButton;
	private Text startingRow;
	private Text valueColumn;

	// Destination group
	private Button androidButton;
	private Button propertiesButton;
	private Composite fileNameGroup;
	private Text fileName;

	// Android format group
	private Composite androidFormatGroup;
	private Button stringFormatButton;
	private Button areaFormatButton;

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

		createSourceGroup(main);
		createDestinationGroup(main);

		setControl(main);

		stringFormatButton.setSelection(true);

		selectAllSheets.setSelection(true);
		sheetListGroup.setEnabled(false);
		fileNameGroup.setEnabled(false);

		defineValuesButton.setSelection(false);
		defineValuesGroup.setEnabled(false);

		androidButton.setSelection(true);
		androidFormatGroup.setEnabled(true);
	}

	private void createSourceGroup(Composite parent) {
		final Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout());
		group.setText("Source selection");

		final Composite layout = new Composite(group, SWT.NONE);
		layout.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout.setLayout(new GridLayout(3, false));
		createSourceSelectionComponent(layout);

		createSourceSheetSelectionComponent(group);

		createStartValuesComponent(group);
	}

	private void createSourceSelectionComponent(Composite parent) {
		Label fileLabel = new Label(parent, SWT.NONE);
		fileLabel.setText("Source:");

		sourceName = new Text(parent, SWT.BORDER);
		sourceName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sourceName.addModifyListener(new ModifyListener() {

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
				fd.setFilterExtensions(new String[] { "*.ods" });
				String name = fd.open();
				if (name != null) {
					sourceName.setText(name);
					populateSheetList();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		fileSelection.setText("Select");
	}

	private void createSourceSheetSelectionComponent(Composite parent) {
		selectAllSheets = new Button(parent, SWT.CHECK);
		selectAllSheets.setText("Export all sheets");
		selectAllSheets.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sheetListGroup.setEnabled(!selectAllSheets.getSelection());
				fileNameGroup.setEnabled(!selectAllSheets.getSelection());
				validatePage();
			}
		});

		sheetListGroup = new EnhancedComposite(parent, SWT.NONE);
		sheetListGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sheetListGroup.setLayout(new GridLayout(2, false));

		createSheetListComponent(sheetListGroup);

	}

	private void createSheetListComponent(Composite parent) {
		Label comboLabel = new Label(parent, SWT.NONE);
		comboLabel.setText("Sheet:");

		sheetList = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
		sheetList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sheetList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectedIndex = sheetList.getSelectionIndex();
				if (selectedIndex > -1) {
					fileName.setText(sheetList.getItem(selectedIndex));
				}
				validatePage();
			}
		});
	}

	private void createStartValuesComponent(Composite parent) {
		defineValuesButton = new Button(parent, SWT.CHECK);
		defineValuesButton.setText("Define default values");
		defineValuesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				defineValuesGroup.setEnabled(defineValuesButton.getSelection());
			}
		});

		defineValuesGroup = new EnhancedComposite(parent, SWT.NONE);
		defineValuesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		defineValuesGroup.setLayout(new GridLayout(2, false));

		createStartRowComponent(defineValuesGroup);
		createValueColumnComponent(defineValuesGroup);
	}

	private void createStartRowComponent(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Starting row:");

		startingRow = new Text(parent, SWT.BORDER);
		startingRow.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		startingRow.setText(String.valueOf(Importator.DEFAULT_STARTING_ROW));
		startingRow.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});
		startingRow.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
	}

	private void createValueColumnComponent(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Value column:");

		valueColumn = new Text(parent, SWT.BORDER);
		valueColumn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		valueColumn.setText(String.valueOf(Importator.DEFAULT_VALUE_COLUMN));
		valueColumn.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});
		valueColumn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
	}

	private void createDestinationGroup(Composite parent) {
		final Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(1, false));
		group.setText("Destination configuration");
		createDestinationTypeComponent(group);

		fileNameGroup = new EnhancedComposite(group, SWT.NONE);
		fileNameGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileNameGroup.setLayout(new GridLayout(2, false));
		createDestinationComponent(fileNameGroup);
	}

	private void createDestinationTypeComponent(Composite parent) {
		androidButton = new Button(parent, SWT.RADIO);
		androidButton.setText("Android resource");
		androidButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				androidFormatGroup.setEnabled(androidButton.getSelection());
			}
		});

		createDestinationAndroidFormatGroup(parent);

		propertiesButton = new Button(parent, SWT.RADIO);
		propertiesButton.setText("Properties file");
	}

	private void createDestinationAndroidFormatGroup(Composite parent) {
		androidFormatGroup = new EnhancedComposite(parent, SWT.NONE);
		androidFormatGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		androidFormatGroup.setLayout(new GridLayout(3, false));
		createAndroidFormatComponent(androidFormatGroup);
	}

	private void createAndroidFormatComponent(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Android format:");

		stringFormatButton = new Button(parent, SWT.RADIO);
		stringFormatButton.setText("<string>");

		areaFormatButton = new Button(parent, SWT.RADIO);
		areaFormatButton.setText("<string-array>");
	}

	private void createDestinationComponent(Composite parent) {
		Label fileNameLabel = new Label(parent, SWT.NONE);
		fileNameLabel.setText("File name:");

		fileName = new Text(parent, SWT.BORDER);
		fileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
	}

	private void validatePage() {
		String sourceNameString = sourceName.getText();
		if (sourceNameString == null || sourceNameString.length() < 1) {
			setErrorMessage("Source file name is invalid.");
			setPageComplete(false);
			return;
		}
		if (!sourceNameString.endsWith(".ods")) {
			setErrorMessage("Source file name should end with .ods.");
			setPageComplete(false);
			return;
		}

		if (!selectAllSheets.getSelection()) {
			int sheetIndex = sheetList.getSelectionIndex();
			if (sheetIndex == -1) {
				setErrorMessage("No sheet selected.");
				setPageComplete(false);
				return;
			}
		}

		if (defineValuesButton.getSelection()) {
			String startingRowString = startingRow.getText();
			if (startingRowString == null || startingRowString.length() < 1) {
				setErrorMessage("Starting row is invalid.");
				setPageComplete(false);
				return;
			}
			String valueColumnString = valueColumn.getText();
			if (valueColumnString == null || valueColumnString.length() < 1) {
				setErrorMessage("Value column index is invalid.");
				setPageComplete(false);
				return;
			}
		}

		if (!selectAllSheets.getSelection()) {
			String fileNameString = fileName.getText();
			if (fileNameString == null || fileNameString.length() < 1) {
				setErrorMessage("Destination file name is invalid.");
				setPageComplete(false);
				return;
			}
		}

		setErrorMessage(null);
		setPageComplete(true);
	}

	private void populateSheetList() {
		sheetList.removeAll();
		String sourcePath = sourceName.getText();
		if (sourcePath != null && !sourcePath.isEmpty()) {
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

	public String getSourceName() {
		return sourceName.getText();
	}

	public int getType() {
		if (androidButton.getSelection()) {
			return ANDROID_FORMAT;
		}
		if (propertiesButton.getSelection()) {
			return PROPERTIES_FORMAT;
		}
		return UNKNOWN_FORMAT;
	}

	public int getAndroidFormat() {
		if (stringFormatButton.getSelection()) {
			return ANDROID_STRING_FORMAT;
		}
		return ANDROID_STRING_ARRAY_FORMAT;
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

	public boolean hasDefaultValues() {
		return defineValuesButton.getSelection();
	}

	public int getStartingRow() {
		return Integer.valueOf(startingRow.getText());
	}

	public int getValueColumn() {
		return Integer.valueOf(valueColumn.getText());
	}

	public boolean isSelectAllSheetsEnabled() {
		return selectAllSheets.getSelection();
	}

}
