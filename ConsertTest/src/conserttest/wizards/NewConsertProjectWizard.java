package conserttest.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import conserttest.pages.NewConsertProjectPage;
import conserttest.pages.NewContextAssertionPage;

public class NewConsertProjectWizard extends Wizard implements INewWizard {

	private NewConsertProjectPage mainPage;
	private IStructuredSelection selection;
	private IWorkbench workbench;

	public NewConsertProjectWizard() {
		super();
		setWindowTitle("New Consert Project Wizard");
		setNeedsProgressMonitor(true);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
		IWorkbenchWizard wizard = new BasicNewProjectResourceWizard();
		wizard.init(workbench, selection);
		WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
		dialog.open();
	}

	@Override
	public String getWindowTitle() {
		return "New Context Assertion Wizard";
	}

	@Override
	public void addPages() {
		mainPage = new NewConsertProjectPage(selection);
		addPage(mainPage);
	}

	@Override
	public boolean performFinish() {
		return mainPage.finish(workbench, selection);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		if (currentPage != mainPage) {
			return mainPage;
		}
		return null;
	}
}
