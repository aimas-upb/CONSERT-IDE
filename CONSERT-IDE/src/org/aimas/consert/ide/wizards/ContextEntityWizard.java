package org.aimas.consert.ide.wizards;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.entity.EntityMultiPageEditor;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.aimas.consert.ide.util.Utils;
import org.aimas.consert.ide.util.Utils.ModelType;
import org.aimas.consert.ide.wizards.pages.WizardNewEntityPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ContextEntityWizard extends Wizard implements INewWizard {
	private IStructuredSelection selection;
	private String projectName;
	private WizardNewEntityPage _pageOne;

	public ContextEntityWizard() {
		this(null);
	}

	public ContextEntityWizard(String projectName) {
		this.projectName = projectName;
		setWindowTitle(NewWizardMessages.NewContextEntityTitle);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public void addPages() {
		super.addPages();

		if (projectName == null) {
			projectName = WorkspaceModel.getInstance().getCurrentActiveProject(selection);
		}

		_pageOne = new WizardNewEntityPage(NewWizardMessages.NewContextEntityWizard, projectName);
		_pageOne.setDescription(NewWizardMessages.NewContextEntityDescription);
		_pageOne.setTitle(NewWizardMessages.NewContextEntityTitle);

		addPage(_pageOne);
	}

	@Override
	public boolean performFinish() {
		ContextEntityModel model = createModelFromWizard();

		/* finish means adding in the consert.txt file the required fields */
		ProjectModel projectModel = WorkspaceModel.getInstance().getProjectModel(projectName);
		projectModel.saveNewModelJSONOnDisk(model);

		openEditorOnFinish(projectModel, model);

		return true;
	}

	public void openEditorOnFinish(ProjectModel projectModel, ContextEntityModel model) {

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		EditorInputWrapper eiw = new EditorInputWrapper(model);
		eiw.setProjectModel(projectModel);

		try {
			page.openEditor(eiw, EntityMultiPageEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private ContextEntityModel createModelFromWizard() {
		ContextEntityModel model = new ContextEntityModel();

		model.setID(Utils.generateID(projectName, ModelType.ENTITY, _pageOne.getTextName()));
		model.setName(_pageOne.getTextName());
		model.setComment(_pageOne.getTextComment());

		return model;
	}

}
