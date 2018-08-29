package org.aimas.consert.ide.wizards.pages;

import java.util.List;

import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
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
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class WizardNewEntityDescriptionPage extends WizardPage {
	private Text textName;
	private Text textprojectName;
	private Text textObject;
	private Label labelprojectName;
	private Label labelName;
	private Label labelObject;
	private Label labelSubjectEntityName;
	private Combo comboSubjectEntityName;
	private Composite container;
	private String projectName;
	private List<ContextEntityModel> allEntities;
	

	public WizardNewEntityDescriptionPage(String pageName, String projectName) {
		super(pageName);
		this.projectName = projectName;
		allEntities = Utils.getInstance().getAllEntities();
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

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
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		textprojectName.setLayoutData(gd);

		// name of the context entity
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
		textName.setLayoutData(gd);

		// comment of the context entity
		labelObject = new Label(container, SWT.NONE);
		labelObject.setText("Object");

		textObject = new Text(container, SWT.BORDER | SWT.SINGLE);
		textObject.setText("");
		textObject.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!textObject.getText().isEmpty()) {
					setPageComplete(true);
				}
			}

		});
		textObject.setLayoutData(gd);
		
		// SubjectEntityName of the entity description
		labelSubjectEntityName = new Label(container, SWT.NONE);
		labelSubjectEntityName.setText("Subject Entity Name");

		comboSubjectEntityName = new Combo(container, SWT.READ_ONLY);
		String items2[] = Utils.getInstance().getAllEntitiesStringNames(allEntities);
		comboSubjectEntityName.setItems(items2);
		comboSubjectEntityName.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
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

	public String getTextObject() {
		return textObject.getText();
	}
	
	public ContextEntityModel getSubjectEntity() {
		int index = comboSubjectEntityName.getSelectionIndex();
		String subjectEntityName = comboSubjectEntityName.getItem(index == -1 ? 0 : index);
		for (ContextEntityModel cem : allEntities) {
			if (cem.getName().equals(subjectEntityName)) {
				return cem;
			}
		}
		return null;
	}
}
