package org.aimas.consert.ide.wizards.pages;

import org.aimas.consert.ide.model.AnnotationCategory;
import org.aimas.consert.ide.model.AnnotationType;
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

public class WizardNewAnnotationPage extends WizardPage {

	private Text textAssertionName;
	private Text textprojectName;

	private Combo comboAnnotationType;
	private Combo comboAnnotationCategory;

	private String projectName;

	public WizardNewAnnotationPage(String pageName, String projectName) {
		super(pageName);
		this.projectName = projectName;
	}

	public Combo getComboAnnotationCategory() {
		return comboAnnotationCategory;
	}

	public Combo getComboAnnotationType() {
		return comboAnnotationType;
	}

	public String getProjectName() {
		return textprojectName.getText();
	}

	public String getTextName() {
		return textAssertionName.getText();
	}

	public AnnotationType getAnnotationType() {
		int index = comboAnnotationType.getSelectionIndex();
		return AnnotationType.toValue(comboAnnotationType.getItem(index == -1 ? 0 : index));
	}

	public AnnotationCategory getAnnotationCategory() {
		int index = comboAnnotationCategory.getSelectionIndex();
		return AnnotationCategory.toValue(comboAnnotationCategory.getItem(index == -1 ? 0 : index));
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));

		textprojectName = createLabelAndTextWidgets(container, "Project Name");
		textprojectName.setText(projectName);

		textAssertionName = createLabelAndTextWidgets(container, "Name");

		comboAnnotationType = createLabelAndComboForAnnotation(container, "AnnotationType");
		comboAnnotationType.setItems(new String[] { AnnotationType.TIMESTAMP.name(), AnnotationType.TRUST.name() });

		comboAnnotationCategory = createLabelAndComboForAnnotation(container, "AnnotationCategory");
		comboAnnotationCategory
				.setItems(new String[] { AnnotationCategory.SIMPLE.name(), AnnotationCategory.STRUCTURED.name() });

		setControl(container);
		setPageComplete(false);
	}

	private Combo createLabelAndComboForAnnotation(Composite parent, String labelText) {
		Label labelAnnotationType = new Label(parent, SWT.NONE);
		labelAnnotationType.setText(labelText);

		Combo annotationCombo = new Combo(parent, SWT.READ_ONLY);
		setSelectionListenerOnCombo(annotationCombo);

		return annotationCombo;
	}

	private Text createLabelAndTextWidgets(Composite parent, String labelText) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);

		Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		text.setText("");
		setKeyListenerOnText(text);

		return text;
	}

	private void setSelectionListenerOnCombo(Combo annotationCombo) {
		annotationCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(annotationCombo.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println(annotationCombo.getText());
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
