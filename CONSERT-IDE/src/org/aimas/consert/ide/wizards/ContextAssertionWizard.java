package org.aimas.consert.ide.wizards;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.assertion.AssertionMultiPageEditor;
import org.aimas.consert.ide.model.ContextAssertionModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.aimas.consert.ide.util.Utils;
import org.aimas.consert.ide.wizards.pages.WizardNewAssertionPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ContextAssertionWizard extends Wizard implements INewWizard {
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private String projectName;
	private WizardNewAssertionPage _pageOne;

	public ContextAssertionWizard() {
		this(null);
	}

	public ContextAssertionWizard(String projectName) {
		super();
		this.projectName = projectName;
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
		if (projectName == null) {
			projectName = WorkspaceModel.getInstance().getCurrentActiveProject(this.selection);
		}
		_pageOne = new WizardNewAssertionPage(NewWizardMessages.NewContextAssertionWizard, projectName);
		_pageOne.setDescription(NewWizardMessages.NewContextAssertionDescription);
		_pageOne.setTitle(NewWizardMessages.NewContextAssertionTitle);
		addPage(_pageOne);
	}

	@Override
	public boolean performFinish() {
		ContextAssertionModel model = new ContextAssertionModel();
		
		//ID for Assertion
		String ID = Utils.getInstance().generateID(projectName, "ASSERTION", _pageOne.getTextName());
		
		model.setID(ID);
		model.setName(_pageOne.getTextName());
		model.setComment(_pageOne.getTextComment());
		// model.setArity(Integer.parseInt(_pageOne.getTextArity()));
		model.setAcquisitionType(_pageOne.getAcquisitionType());
		model.setObjectEntity(_pageOne.getObjectEntity());
		model.setSubjectEntity(_pageOne.getSubjectEntity());

		/* finish means adding in the consert.txt file the required fields */
		ProjectModel projectModel = WorkspaceModel.getInstance().getProjectModel(projectName);
		projectModel.saveNewModelOntologyOnDisk(model);

		openEditorOnFinish(projectModel, model);
		return true;
	}

	private void openEditorOnFinish(ProjectModel projectModel, ContextAssertionModel model) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		EditorInputWrapper eiw = new EditorInputWrapper(model);
		eiw.setProjectModel(projectModel);
		try {
			page.openEditor(eiw, AssertionMultiPageEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	// private List<ContextEntityModel> getEntities(String textEntities) {
	// String[] entitiesName = textEntities.split(",");
	// List<ContextEntityModel> entities = new ArrayList<ContextEntityModel>();
	// for (String entityName : entitiesName) {
	// ContextEntityModel new_entity = new ContextEntityModel();
	// new_entity.setName(entityName);
	// entities.add(new_entity);
	// }
	// return entities;
	// }
}
