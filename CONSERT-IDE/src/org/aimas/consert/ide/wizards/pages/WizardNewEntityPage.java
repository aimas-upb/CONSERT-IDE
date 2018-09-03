package org.aimas.consert.ide.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardNewEntityPage extends WizardPage {

	private Text textProjectName;
	private Text textEntityName;
	private Text textEntityComment;

	private String projectName;

	public WizardNewEntityPage(String pageName, String projectName) {
		super(pageName);
		this.projectName = projectName;
	}

	public String getProjectName() {
		return textProjectName.getText();
	}

	public String getTextName() {
		return textEntityName.getText();
	}

	public String getTextComment() {
		return textEntityComment.getText();
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));

		textProjectName = createLabelAndTextWidgets(container, "Project Name");
		textProjectName.setText(projectName);

		textEntityName = createLabelAndTextWidgets(container, "Name");
		textEntityComment = createLabelAndTextWidgets(container, "Comment");

		setControl(container);
		setPageComplete(false);
	}

	private Text createLabelAndTextWidgets(Composite parent, String labelText) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);

		Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		text.setText("");
		setKeyListnerOnText(text);

		return text;
	}

	private void setKeyListnerOnText(Text text) {
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
