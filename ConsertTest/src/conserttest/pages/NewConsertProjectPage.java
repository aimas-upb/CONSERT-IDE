package conserttest.pages;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;

public class NewConsertProjectPage extends WizardPage {
	private Text text1;
	private Text text2;
	private Composite container;
	Button check;

	public NewConsertProjectPage(IStructuredSelection selection) {
		super("New Consert Project Wizard Page");
		setTitle("New Consert Project");
		setDescription("Create a new Consert Project");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		Label labelCheck = new Label(container, SWT.NONE);
		labelCheck.setText("Create a Consert Project");

		check = new Button(container, SWT.CHECK);
		check.setSelection(true);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);

		Label label1 = new Label(container, SWT.NONE);
		label1.setText("Source Folder Name:");
		Label label2 = new Label(container, SWT.NONE);
		label2.setText("Output Folder Name:");

		text1 = new Text(container, SWT.BORDER | SWT.SINGLE);
		text1.setText("src");
		text2 = new Text(container, SWT.BORDER | SWT.SINGLE);
		text2.setText("bin");

		check.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean result = ((Button) e.widget).getSelection();
				text1.setEnabled(result);
				text1.setEditable(result);
				text2.setEnabled(result);
				text2.setEditable(result);
			}

		});

		text1.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				setPageComplete(text2.isVisible() == true && !text2.getText().isEmpty() && text1.isVisible() == true
						&& !text1.getText().isEmpty());
			}

		});

		text1.setLayoutData(gd);

		text2.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				setPageComplete(text2.isVisible() == true && !text2.getText().isEmpty() && text1.isVisible() == true
						&& !text1.getText().isEmpty());
			}

		});
		text2.setLayoutData(gd2);

		setControl(container);
		setPageComplete(true);
	}

	public boolean finish(IWorkbench workbench, IStructuredSelection selection) {
		if (check.getSelection()) {
			String current = System.getProperty("user.dir");
			String path = current + "/" + text1.getText();
			File folder = new File(path);
			if (folder.mkdir())
				System.out.println("s-a creat");
				else
					System.out.println("NU s a creat");
			System.out.println(path);
//			File folder2 = new File(text2.getText());
//			folder2.mkdir();
		}
		return isPageComplete();
	}

}
