package org.aimas.consert.ide.wizards;

import java.util.ArrayList;
import java.util.List;

import org.aimas.consert.ide.model.ContextAssertionModel;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.aimas.consert.ide.wizards.pages.WizardNewAssertionPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class ContextAssertionWizard extends Wizard implements INewWizard {
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private WizardNewAssertionPage _pageOne;

	public ContextAssertionWizard() {
		super();
		setWindowTitle(NewWizardMessages.NewContextAssertionTitle);
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
		_pageOne = new WizardNewAssertionPage(NewWizardMessages.NewContextAssertionWizard, projectName);
		_pageOne.setDescription(NewWizardMessages.NewContextAssertionDescription);
		_pageOne.setTitle(NewWizardMessages.NewContextAssertionTitle);
		addPage(_pageOne);
	}

	@Override
	public boolean performFinish() {
		String projectName = _pageOne.getProjectName();
		ContextAssertionModel model = new ContextAssertionModel();
		model.setName(_pageOne.getTextName());
		model.setComment(_pageOne.getTextComment());
		model.setArity(Integer.parseInt(_pageOne.getTextArity()));
		model.setEntities(getEntities(_pageOne.getTextEntities()));

		/* finish means adding in the consert.txt file the required fields */
		return WorkspaceModel.getInstance().getProjectModel(projectName).saveNewModelOnDisk(projectName, model);
	}

	private List<ContextEntityModel> getEntities(String textEntities) {
		String[] entitiesName = textEntities.split(",");
		List<ContextEntityModel> entities = new ArrayList<ContextEntityModel>();
		for (String entityName : entitiesName) {
			ContextEntityModel new_entity = new ContextEntityModel();
			new_entity.setName(entityName);
			entities.add(new_entity);
		}
		return entities;
	}
}
