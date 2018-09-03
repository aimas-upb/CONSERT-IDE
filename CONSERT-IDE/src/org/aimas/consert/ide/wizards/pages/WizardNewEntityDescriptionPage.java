package org.aimas.consert.ide.wizards.pages;

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

public class WizardNewEntityDescriptionPage extends WizardPage {

	private Text textName;
	private Text textprojectName;

	private Combo objectEntityCombo;
	private Combo subjectEntityCombo;

	private String projectName;

	public WizardNewEntityDescriptionPage(String pageName, String projectName) {
		super(pageName);
		this.projectName = projectName;
	}

	public Combo getObjectEntityCombo() {
		return objectEntityCombo;
	}

	public Combo getSubjectEntityCombo() {
		return subjectEntityCombo;
	}

	public String getProjectName() {
		return textprojectName.getText();
	}

	public String getTextName() {
		return textName.getText();
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));

		textprojectName = createLabelAndTextWidgets(container, "Project Name");
		textprojectName.setText(projectName);

		textName = createLabelAndTextWidgets(container, "Name");

		objectEntityCombo = createLabelAndComboForEntity(container, "Object Entity Name");
		subjectEntityCombo = createLabelAndComboForEntity(container, "Subject Entity Name");

		setControl(container);
		setPageComplete(false);
	}

	private Text createLabelAndTextWidgets(Composite parent, String labelText) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);

		Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		text.setText("");
		setKeyListenerOnText(text);

		return text;
	}

	private Combo createLabelAndComboForEntity(Composite parent, String labelText) {
		Label labelSubjectEntityName = new Label(parent, SWT.NONE);
		labelSubjectEntityName.setText(labelText);

		Combo entityCombo = new Combo(parent, SWT.READ_ONLY);
		entityCombo.setItems(Utils.getAllEntitiesStringNames());
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
