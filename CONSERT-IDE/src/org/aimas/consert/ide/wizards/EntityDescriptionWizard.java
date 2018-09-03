package org.aimas.consert.ide.wizards;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.entitydescription.EntityDescriptionMultiPageEditor;
import org.aimas.consert.ide.model.EntityDescriptionModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.aimas.consert.ide.util.Utils;
import org.aimas.consert.ide.util.Utils.ModelType;
import org.aimas.consert.ide.wizards.pages.WizardNewEntityDescriptionPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class EntityDescriptionWizard extends Wizard implements INewWizard {
	private IStructuredSelection selection;
	private String projectName;
	private WizardNewEntityDescriptionPage _pageOne;

	public EntityDescriptionWizard() {
		this(null);
	}

	public EntityDescriptionWizard(String projectName) {
		this.projectName = projectName;
		setWindowTitle(NewWizardMessages.NewEntityDescriptionTitle);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public void addPages() {
		super.addPages();

		if (projectName == null) {
			projectName = WorkspaceModel.getInstance().getCurrentActiveProject(this.selection);
		}

		_pageOne = new WizardNewEntityDescriptionPage(NewWizardMessages.NewEntityDescriptionWizard, projectName);
		_pageOne.setDescription(NewWizardMessages.NewEntityDescriptionDescription);
		_pageOne.setTitle(NewWizardMessages.NewEntityDescriptionTitle);

		addPage(_pageOne);
	}

	@Override
	public boolean performFinish() {
		EntityDescriptionModel model = createModelFromWizard();

		/* finish means adding in the consert.txt file the required fields */
		ProjectModel projectModel = WorkspaceModel.getInstance().getProjectModel(projectName);
		projectModel.saveNewModelJSONOnDisk(model);

		openEditorOnFinish(projectModel, model);

		return true;
	}

	public void openEditorOnFinish(ProjectModel projectModel, EntityDescriptionModel model) {

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		EditorInputWrapper eiw = new EditorInputWrapper(model);
		eiw.setProjectModel(projectModel);

		try {
			page.openEditor(eiw, EntityDescriptionMultiPageEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private EntityDescriptionModel createModelFromWizard() {
		EntityDescriptionModel model = new EntityDescriptionModel();

		model.setID(Utils.generateID(projectName, ModelType.ENTITY_DESCRIPTION, _pageOne.getTextName()));
		model.setName(_pageOne.getTextName());
		model.setObjectEntity(Utils.getEntityModelFromCombo(_pageOne.getObjectEntityCombo()));
		model.setSubjectEntity(Utils.getEntityModelFromCombo(_pageOne.getSubjectEntityCombo()));

		return model;
	}

}
