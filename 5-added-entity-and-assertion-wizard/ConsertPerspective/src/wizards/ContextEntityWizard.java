package wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import json.JsonParser;
import model.ContextEntityModel;
import pages.WizardNewEntityPage;

public class ContextEntityWizard extends Wizard implements INewWizard {
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private WizardNewEntityPage _pageOne;

	public ContextEntityWizard() {
		super();
		setWindowTitle(wizards.NewWizardMessages.ScratchEntity);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	@Override
	public void addPages() {
		super.addPages();
		_pageOne = new WizardNewEntityPage(wizards.NewWizardMessages.ScratchEntity);
		_pageOne.setDescription(
				wizards.NewWizardMessages.ConsertProjectWizard_ConsertProjectWizard_Create_something_custom);
		_pageOne.setTitle(wizards.NewWizardMessages.ScratchEntity);
		addPage(_pageOne);
	}

	@Override
	public boolean performFinish() {
		String projectName = _pageOne.getProjectName();
		ContextEntityModel model = new ContextEntityModel();
		model.setName(_pageOne.getTextName());
		model.setComment(_pageOne.getTextComment());
		/* finish means adding in the consert.txt file the required fields */
		return JsonParser.getInstance().appendToFile(projectName, model);
	}

}
