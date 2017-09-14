package org.aimas.consert.ide.editor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.aimas.consert.ide.model.ContextAssertionModel;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.FileEditorInput;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FormView extends FormPage implements IResourceChangeListener {
	private MultiPageEditor editor;
	private ScrolledForm form;
	private boolean isDirty;
	public static final String ID = "org.aimas.consert.ide.editor.FormView";
	private String projectName;
	private ProjectModel projectModel;

	public FormView(MultiPageEditor editor) {
		super(editor, ID, "FormView");
		this.editor = editor;
		isDirty = false;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/*
	 * This method is called when the form content is created and sets
	 * the project model corresponding to the file "consert.txt" which was accessed.
	 */
	private void setProjectModel() {
		ISelectionService service = getSite().getWorkbenchWindow().getSelectionService();
		IStructuredSelection structured = (IStructuredSelection) service
				.getSelection("org.eclipse.jdt.ui.PackageExplorer");
		IFile file = (IFile) structured.getFirstElement();
		if(file != null){
			String projectName = file.getProject().getName();
			System.out.println(projectName);
			this.projectModel = WorkspaceModel.getInstance().getProjectModel(projectName);
		}
	}

	/*
	 * this method is used for both entities that do not belong to an assertion
	 * and entities that do, the difference is: those with no assertion are
	 * being called with a NULL assertion
	 */
	public void createLabelAndTextForEntity(String labelName, String textName, ContextAssertionModel cam,
			ContextEntityModel cem) {
		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(labelName);
		Text nameText = new Text(form.getBody(), SWT.BORDER | SWT.SINGLE);
		if (textName == null) {
			textName = "";
		}
		nameText.setText(textName);
		nameText.setLayoutData(new GridData(100, 10));
		nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				isDirty = true;
				firePropertyChange(IEditorPart.PROP_DIRTY);
				editor.editorDirtyStateChanged();

				/*
				 * This entity belongs to an assertion, and is not present in
				 * the getEntities() of the ProjectModel!!!
				 */
				if (cam == null) { // an entity with no assertion.
					if (labelName.equals(" Name: ")) {
						projectModel.getEntityByName(cem.getName()).setName(nameText.getText());
					} else if (labelName.equals(" Comment: ")) {
						projectModel.getEntityByName(cem.getName()).setComment(nameText.getText());
					}
				} else { /* an entity that belongs to an assertion */
					List<ContextEntityModel> entities = projectModel.getAssertionByName(cam.getName()).getEntities();
					for (ContextEntityModel entity : entities) {
						if (entity.equals(cem)) {
							if (labelName.equals(" Name: ")) {
								entity.setName(nameText.getText());
							} else if (labelName.equals(" Comment: ")) {
								entity.setComment(nameText.getText());
							}
						}
					}
				}
			}
		});
	}

	public void createLabelAndTextForAssertion(String labelName, String textName, ContextAssertionModel cam) {
		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(labelName);
		Text nameText = new Text(form.getBody(), SWT.BORDER | SWT.SINGLE);
		nameText.setText(textName);
		nameText.setLayoutData(new GridData(100, 10));
		nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				isDirty = true;
				firePropertyChange(IEditorPart.PROP_DIRTY);
				editor.editorDirtyStateChanged();

				if (labelName.equals(" Name: ")) {
					projectModel.getAssertionByName(cam.getName()).setName(nameText.getText());
				} else if (labelName.equals(" Comment: ")) {
					projectModel.getAssertionByName(cam.getName()).setComment(nameText.getText());
				} else if (labelName.equals(" Arity: ")) {
					try {
						projectModel.getAssertionByName(cam.getName()).setArity(Integer.parseInt((nameText.getText())));
					} catch (NumberFormatException exp) {
						System.err.print("Please Introduce an Integer Arity");
					}
				}
			}
		});
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		IPath path = ((FileEditorInput) editor.getEditorInput()).getPath();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = projectModel.getRootNode();

		/*
		 * Saving all entities, because the formView does not track them
		 * individually, so it does not know which changed and which didn't.
		 */
		((ObjectNode) rootNode).withArray("ContextEntities").removeAll();
		for (ContextEntityModel cem : projectModel.getEntities()) {
			((ObjectNode) rootNode).withArray("ContextEntities").add(mapper.valueToTree(cem));
		}
		System.out.println("[doSave] maped new entities into Json: " + projectModel.getEntities());

		/* Saving all assertions as well. */
		((ObjectNode) rootNode).withArray("ContextAssertions").removeAll();
		for (ContextAssertionModel cam : projectModel.getAssertions()) {
			((ObjectNode) rootNode).withArray("ContextAssertions").add(mapper.valueToTree(cam));
		}
		System.out.println("[doSave] maped new assertions into Json: " + projectModel.getAssertions());

		/* Write on disk the new Json into File, replacing the old one. */
		try {
			mapper.writeValue(new File(path.toString()), rootNode);
		} catch (IOException e) {
			e.printStackTrace();
		}

		isDirty = false;
		firePropertyChange(PROP_DIRTY);
		editor.editorDirtyStateChanged();
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		form.setText(editor.getTextEditor().getEditorInput().getName());
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		layout.numColumns = 2;

		try {
			this.setProjectModel();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (projectModel != null) {
			Label entitiesNameLabel = new Label(form.getBody(), SWT.NONE);
			entitiesNameLabel.setText(" ContextEntitities: ");
			new Label(form.getBody(), SWT.NONE);

			for (ContextEntityModel cem : projectModel.getEntities()) {
				createLabelAndTextForEntity(" Name: ", cem.getName(), null, cem);
				createLabelAndTextForEntity(" Comment: ", cem.getComment(), null, cem);
			}

			Label assertionsNameLabel = new Label(form.getBody(), SWT.NONE);
			assertionsNameLabel.setText(" ContextAssertions: ");
			new Label(form.getBody(), SWT.NONE);

			for (ContextAssertionModel cam : projectModel.getAssertions()) {
				createLabelAndTextForAssertion(" Name: ", cam.getName(), cam);
				createLabelAndTextForAssertion(" Comment: ", cam.getComment(), cam);
				createLabelAndTextForAssertion(" Arity: ", Integer.toString(cam.getArity()), cam);

				Label entitiesPerAssertionLabel = new Label(form.getBody(), SWT.NONE);
				entitiesPerAssertionLabel.setText(" ContextEntities: ");
				new Label(form.getBody(), SWT.NONE);

				for (ContextEntityModel entity : cam.getEntities()) {
					createLabelAndTextForEntity(" Name: ", entity.getName(), cam, entity);
					createLabelAndTextForEntity(" Comment: ", entity.getComment(), cam, entity);
				}
			}
		} else {
			Label emptyForm = new Label(form.getBody(), SWT.NONE);
			emptyForm.setText("Project Model not loaded yet! Please close the form and reopen.");
		}

	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta rootDelta = event.getDelta();
		IResourceDelta affected[] = rootDelta.getAffectedChildren();
		for (int i = 0; i < affected.length; i++) {
			System.out.println(affected[i].getResource().getName());
			this.projectName = affected[i].getResource().getName();
		}
		WorkspaceModel instance = WorkspaceModel.getInstance();
		this.projectModel = instance.getProjectModel(this.projectName);

		System.out.println("Reload formView");
	}
}
