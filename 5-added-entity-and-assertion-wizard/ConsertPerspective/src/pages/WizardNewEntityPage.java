package pages;

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
	private Text textName;
	private Text textprojectName;
	private Label labelprojectName;
	private Label labelComment;
	private Label labelName;
	private Text textComment;
	private Composite container;

	public WizardNewEntityPage(String pageName) {
		super(pageName);
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

		textprojectName = new Text(container, SWT.BORDER | SWT.SINGLE);
		textprojectName.setText("");
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
		textComment.setLayoutData(gd);

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

}
