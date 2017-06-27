package org.aimas.consert.ide.wizards;

import java.util.ArrayList;
import java.util.List;

import org.aimas.consert.ide.model.ContextAssertionModel;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.utils.JsonParser;
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
		setWindowTitle(org.aimas.consert.ide.wizards.NewWizardMessages.ScratchAssertion);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	@Override
	public void addPages() {
		super.addPages();
		_pageOne = new WizardNewAssertionPage(org.aimas.consert.ide.wizards.NewWizardMessages.ScratchAssertion);
		_pageOne.setDescription(
				org.aimas.consert.ide.wizards.NewWizardMessages.ConsertProjectWizard_ConsertProjectWizard_Create_something_custom);
		_pageOne.setTitle(org.aimas.consert.ide.wizards.NewWizardMessages.ScratchEntity);
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
		return JsonParser.getInstance().appendToFile(projectName, model);
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
