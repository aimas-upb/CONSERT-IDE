package org.aimas.consert.ide.wizards;

import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.aimas.consert.ide.wizards.pages.WizardNewEntityPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class ContextEntityWizard extends Wizard implements INewWizard {
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private WizardNewEntityPage _pageOne;

	public ContextEntityWizard() {
		super();
		setWindowTitle(NewWizardMessages.NewContextEntityTitle);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	@Override
	public void addPages() {
		super.addPages();
		String projectName = WorkspaceModel.getInstance().getCurrentActiveProject(this.selection);
		_pageOne = new WizardNewEntityPage(NewWizardMessages.NewContextEntityWizard, projectName);
		_pageOne.setDescription(NewWizardMessages.NewContextEntityDescription);
		_pageOne.setTitle(NewWizardMessages.NewContextEntityTitle);
		addPage(_pageOne);
	}

	@Override
	public boolean performFinish() {
		String projectName = _pageOne.getProjectName();
		ContextEntityModel model = new ContextEntityModel();
		model.setName(_pageOne.getTextName());
		model.setComment(_pageOne.getTextComment());

		/* finish means adding in the consert.txt file the required fields */
		return WorkspaceModel.getInstance().getProjectModel(projectName).saveNewModelOnDisk(projectName, model);
	}
}
