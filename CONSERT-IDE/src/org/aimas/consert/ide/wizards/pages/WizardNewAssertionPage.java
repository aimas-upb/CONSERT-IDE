package org.aimas.consert.ide.wizards.pages;

import org.aimas.consert.ide.model.AcquisitionType;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardNewAssertionPage extends WizardPage {
	private Text textName;
	private Text textComment;
	private Text textprojectName;
	private Text textObjectEntityName;
	private Text textSubjectEntityName;
	private Label labelName;
	private Label labelComment;
	private Label labelprojectName;
	private Label labelAcquisitionType;
	private Label labelObjectEntityName;
	private Label labelSubjectEntityName;
	private Composite container;
	private Combo comboAcquisitionType;
	private String projectName;


	public WizardNewAssertionPage(String pageName, String projectName) {
		super(pageName);
		this.projectName = projectName;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		// project name
		labelprojectName = new Label(container, SWT.NONE);
		labelprojectName.setText("Project Name");

		String projectName = this.projectName;
		this.textprojectName = new Text(container, SWT.BORDER | SWT.SINGLE);
		this.textprojectName.setText(projectName);
		textprojectName.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!textprojectName.getText().isEmpty()) {
					setPageComplete(true);
				}
			}

		});
		textprojectName.setLayoutData(gridData);

		// name of the context assertion
		labelName = new Label(container, SWT.NONE);
		labelName.setText("Name");

		textName = new Text(container, SWT.BORDER | SWT.SINGLE);
		textName.setText("");
		textName.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!textName.getText().isEmpty()) {
					setPageComplete(true);
				}
			}

		});
		textName.setLayoutData(gridData);

		// comment of the context assertion
		labelComment = new Label(container, SWT.NONE);
		labelComment.setText("Comment");

		textComment = new Text(container, SWT.BORDER | SWT.SINGLE);
		textComment.setText("");
		textComment.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!textComment.getText().isEmpty()) {
					setPageComplete(true);
				}
			}

		});
		textComment.setLayoutData(gridData);

		// AcquisitionType
		labelAcquisitionType = new Label(container, SWT.NONE);
		labelAcquisitionType.setText("AcquisitionType");

		comboAcquisitionType = new Combo(container, SWT.READ_ONLY);
		String items[] = { AcquisitionType.DERIVED.toString(), AcquisitionType.PROFILED.toString(),
				AcquisitionType.SENSED.toString() };
		comboAcquisitionType.setItems(items);
		comboAcquisitionType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(comboAcquisitionType.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println(comboAcquisitionType.getText());
			}
		});

		// objectEntityName of the context assertion
		labelObjectEntityName = new Label(container, SWT.NONE);
		labelObjectEntityName.setText("Object Entity Name");

		textObjectEntityName = new Text(container, SWT.BORDER | SWT.SINGLE);
		textObjectEntityName.setText("");
		textObjectEntityName.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!textObjectEntityName.getText().isEmpty()) {
					setPageComplete(true);
				}
			}

		});
		textObjectEntityName.setLayoutData(gridData);

		// subjectEntityName of the context assertion
		labelSubjectEntityName = new Label(container, SWT.NONE);
		labelSubjectEntityName.setText("Subject Entity Name");

		textSubjectEntityName = new Text(container, SWT.BORDER | SWT.SINGLE);
		textSubjectEntityName.setText("");
		textSubjectEntityName.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!textSubjectEntityName.getText().isEmpty()) {
					setPageComplete(true);
				}
			}

		});
		textSubjectEntityName.setLayoutData(gridData);

		setControl(container);
		setPageComplete(false);
	}

	public String getProjectName() {
		return textprojectName.getText();
	}

	public String getTextName() {
		return textName.getText();
	}

	public String getTextComment() {
		return textName.getText();
	}

	public AcquisitionType getAcquisitionType() {
		int index = comboAcquisitionType.getSelectionIndex();
		String acquisitionTypeName = comboAcquisitionType.getItem(index == -1 ? 0 : index);
		return AcquisitionType.toValue(acquisitionTypeName);
	}

	public ContextEntityModel getObjectEntity() {
		ContextEntityModel objectEntity = new ContextEntityModel();
		objectEntity.setName(textObjectEntityName.getText());
		return objectEntity;
	}

	public ContextEntityModel getSubjectEntity() {
		ContextEntityModel subjectEntity = new ContextEntityModel();
		subjectEntity.setName(textSubjectEntityName.getText());
		return subjectEntity;
	}
}
