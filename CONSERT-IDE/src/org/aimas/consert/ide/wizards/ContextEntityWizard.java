package org.aimas.consert.ide.wizards;

import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.aimas.consert.ide.utils.JsonParser;
import org.aimas.consert.ide.wizards.pages.WizardNewEntityPage;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
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
		setWindowTitle(org.aimas.consert.ide.wizards.NewWizardMessages.ScratchEntity);
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
		_pageOne = new WizardNewEntityPage(org.aimas.consert.ide.wizards.NewWizardMessages.ScratchEntity, projectName);
		_pageOne.setDescription(
				org.aimas.consert.ide.wizards.NewWizardMessages.ConsertProjectWizard_ConsertProjectWizard_Create_something_custom);
		_pageOne.setTitle(org.aimas.consert.ide.wizards.NewWizardMessages.ScratchEntity);
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
