package org.aimas.consert.ide.wizards.pages;

import java.util.List;

import org.aimas.consert.ide.model.AcquisitionType;
import org.aimas.consert.ide.model.AnnotationCategory;
import org.aimas.consert.ide.model.AnnotationType;
import org.aimas.consert.ide.model.ContextEntityModel;
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

public class WizardNewAnnotationPage extends WizardPage {
	private Text textName;
	private Text textprojectName;
	private Label labelName;
	private Label labelprojectName;
	private Label labelAnnotationType;
	private Label labelAnnotationCategory;
	private Composite container;
	private Combo comboAnnotationType;
	private Combo comboAnnotationCategory;
	private String projectName;

	public WizardNewAnnotationPage(String pageName, String projectName) {
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

		
		// AnnotationType
		labelAnnotationType = new Label(container, SWT.NONE);
		labelAnnotationType.setText("AnnotationType");

		comboAnnotationType = new Combo(container, SWT.READ_ONLY);
		String itemsAnnotationType[] = { AnnotationType.TIMESTAMP.toString(), AnnotationType.TRUST.toString()};
		comboAnnotationType.setItems(itemsAnnotationType);
		comboAnnotationType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(comboAnnotationType.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println(comboAnnotationType.getText());
			}
		});
		
		// AnnotationCategory
		labelAnnotationCategory = new Label(container, SWT.NONE);
		labelAnnotationCategory.setText("AnnotationCategory");

		comboAnnotationCategory = new Combo(container, SWT.READ_ONLY);
		String itemsAnnotationCategory[] = { AnnotationCategory.SIMPLE.toString(), AnnotationCategory.STRUCTURED.toString()};
		comboAnnotationCategory.setItems(itemsAnnotationCategory);
		comboAnnotationCategory.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(comboAnnotationCategory.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println(comboAnnotationCategory.getText());
			}
		});

		setControl(container);
		setPageComplete(false);
	}

	public String getProjectName() {
		return textprojectName.getText();
	}

	public String getTextName() {
		return textName.getText();
	}

	public AnnotationType getAnnotationType() {
		int index = comboAnnotationType.getSelectionIndex();
		String annotationTypeName = comboAnnotationType.getItem(index == -1 ? 0 : index);
		return AnnotationType.toValue(annotationTypeName);
	}
	
	public AnnotationCategory getAnnotationCategory() {
		int index = comboAnnotationCategory.getSelectionIndex();
		String annotationCategoryName = comboAnnotationCategory.getItem(index == -1 ? 0 : index);
		return AnnotationCategory.toValue(annotationCategoryName);
	}

}
