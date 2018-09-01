package org.aimas.consert.ide.wizards;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.annotation.AnnotationMultiPageEditor;
import org.aimas.consert.ide.editor.assertion.AssertionMultiPageEditor;
import org.aimas.consert.ide.model.ContextAnnotationModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.aimas.consert.ide.util.Utils;
import org.aimas.consert.ide.wizards.pages.WizardNewAnnotationPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ContextAnnotationWizard extends Wizard implements INewWizard {
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private String projectName;
	private WizardNewAnnotationPage _pageOne;

	public ContextAnnotationWizard() {
		this(null);
	}

	public ContextAnnotationWizard(String projectName) {
		super();
		this.projectName = projectName;
		setWindowTitle(NewWizardMessages.NewContextAnnotationTitle);
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
		_pageOne = new WizardNewAnnotationPage(NewWizardMessages.NewContextAnnotationWizard, projectName);
		_pageOne.setDescription(NewWizardMessages.NewContextAnnotationDescription);
		_pageOne.setTitle(NewWizardMessages.NewContextAnnotationTitle);
		addPage(_pageOne);
	}

	@Override
	public boolean performFinish() {
		ContextAnnotationModel model = new ContextAnnotationModel();
		
		//ID for Annotation
		String ID = Utils.getInstance().generateID(projectName, "ANNOTATION", _pageOne.getTextName());
		
		model.setID(ID);
		model.setName(_pageOne.getTextName());
		
		model.setAnnotationType(_pageOne.getAnnotationType());
		model.setAnnotationCategory(_pageOne.getAnnotationCategory());
		
		/* finish means adding in the consert.txt file the required fields */
		ProjectModel projectModel = WorkspaceModel.getInstance().getProjectModel(projectName);
		projectModel.saveNewModelJSONOnDisk(model);

		openEditorOnFinish(projectModel, model);
		return true;
	}

	private void openEditorOnFinish(ProjectModel projectModel, ContextAnnotationModel model) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		EditorInputWrapper eiw = new EditorInputWrapper(model);
		eiw.setProjectModel(projectModel);
		try {
			page.openEditor(eiw, AnnotationMultiPageEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}
