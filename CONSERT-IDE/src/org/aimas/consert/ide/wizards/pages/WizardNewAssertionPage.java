package org.aimas.consert.ide.wizards.pages;

import org.aimas.consert.ide.model.AcquisitionType;
import org.aimas.consert.ide.util.Utils;
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

	private Text textProjectName;
	private Text textAssertionName;
	private Text textAssertionComment;

	private Combo comboAcquisitionType;
	private Combo objectEntityCombo;
	private Combo subjectEntityCombo;

	private String projectName;
	private String allEntitiesNames[];

	public WizardNewAssertionPage(String pageName, String projectName) {
		super(pageName);
		this.projectName = projectName;
		allEntitiesNames = Utils.getAllEntitiesStringNames();
	}

	public Combo getObjectCombo() {
		return objectEntityCombo;
	}

	public Combo getSubjectCombo() {
		return subjectEntityCombo;
	}

	public String getProjectName() {
		return textProjectName.getText();
	}

	public String getTextName() {
		return textAssertionName.getText();
	}

	public String getTextComment() {
		return textAssertionComment.getText();
	}

	public AcquisitionType getAcquisitionType() {
		int index = comboAcquisitionType.getSelectionIndex();
		return AcquisitionType.toValue(comboAcquisitionType.getItem(index == -1 ? 0 : index));
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));

		textProjectName = createLabelAndTextWidgets(container, "Project Name");
		textProjectName.setText(projectName);

		textAssertionName = createLabelAndTextWidgets(container, "Name");
		textAssertionComment = createLabelAndTextWidgets(container, "Comment");

		comboAcquisitionType = createLabelAndTextForAcquisitionType(container, "AcquisitionType");

		objectEntityCombo = createLabelAndComboForEntity(container, "Object Entity Name");
		subjectEntityCombo = createLabelAndComboForEntity(container, "Subject Entity Name");

		setControl(container);
		setPageComplete(false);
	}

	private Combo createLabelAndComboForEntity(Composite container, String labelText) {
		Label entityLabel = new Label(container, SWT.NONE);
		entityLabel.setText(labelText);

		Combo entityCombo = new Combo(container, SWT.READ_ONLY);
		entityCombo.setItems(allEntitiesNames);
		setSelectionListenerOnCombo(entityCombo);

		return entityCombo;
	}

	private void setSelectionListenerOnCombo(Combo entityCombo) {
		entityCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private Combo createLabelAndTextForAcquisitionType(Composite container, String labelText) {
		Label labelAcquisitionType = new Label(container, SWT.NONE);
		labelAcquisitionType.setText(labelText);

		Combo comboAcquisitionType = new Combo(container, SWT.READ_ONLY);
		comboAcquisitionType.setItems(new String[] { AcquisitionType.DERIVED.name(), AcquisitionType.PROFILED.name(),
				AcquisitionType.SENSED.name() });
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

		return comboAcquisitionType;
	}

	private Text createLabelAndTextWidgets(Composite parent, String labelText) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);

		Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		text.setText("");
		setKeyListenerOnText(text);

		return text;
	}

	private void setKeyListenerOnText(Text text) {
		text.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!text.getText().isEmpty()) {
					setPageComplete(true);
				}
			}

		});

		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

}
