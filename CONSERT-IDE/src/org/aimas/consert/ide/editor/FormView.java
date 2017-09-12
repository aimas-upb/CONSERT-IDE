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

	private void populateProjectModel() throws JsonProcessingException, IOException {	
		WorkspaceModel instance = WorkspaceModel.getInstance();
		this.projectModel = instance.getProjectModel("test"); 	
		
		IDocument doc = editor.getTextEditor().getDocumentProvider()
				.getDocument(editor.getTextEditor().getEditorInput());
		String content = doc.get();

		if (content.isEmpty()) {
			System.err.println("File is completely empty!");
			return;
		}

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(content);

		System.out.println("Form View parsed: " + rootNode.toString());
		
		this.projectModel.setRootNode(rootNode);
		/* set path to file consert.txt in ProjectModel */
		ISelectionService service = getSite().getWorkbenchWindow().getSelectionService();
		IStructuredSelection structured = (IStructuredSelection) service
				.getSelection("org.eclipse.jdt.ui.PackageExplorer");
		IFile file = (IFile) structured.getFirstElement();
		this.projectModel.setPath(file.getLocation());

		/*
		 * This code populates the view and the model with the found ENTITIES
		 * from the rootNode
		 */
		String nodeName = "ContextEntities";
		this.projectModel.getEntities().clear();
		if (rootNode.has(nodeName)) {
			JsonNode entities = (JsonNode) rootNode.get(nodeName);
			if (entities.isArray() && entities.size() > 0) {
				for (JsonNode entity : entities) {
					try {
						/* Populate model with entities */
						ContextEntityModel cem = mapper.treeToValue(entity, ContextEntityModel.class);
						this.projectModel.addEntity(cem);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.err.println("ContextEntities JsonNode has no entities");
			}
		} else {
			System.err.println("File does not have a ContextEntities JsonNode");
		}

		/*
		 * This code populates the view and the model with the found ASSERTIONS
		 * from the rootNode
		 */
		nodeName = "ContextAssertions";
		this.projectModel.getAssertions().clear();
		if (rootNode.has(nodeName)) {
			JsonNode assertions = (JsonNode) rootNode.get(nodeName);
			if (assertions.isArray() && assertions.size() > 0) {
				for (JsonNode assertion : assertions) {
					try {
						/* Populate model with assertions */
						ContextAssertionModel cam = mapper.treeToValue(assertion, ContextAssertionModel.class);
						this.projectModel.addAssertion(cam);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.err.println("ContextAssertions JsonNode has no assertions");
			}
		} else {
			System.err.println("File does not have a ContextAssertions JsonNode");
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
				} else { // an entity that belongs to an assertion
					List<ContextEntityModel> entities = projectModel.getAssertionByName(cam.getName())
							.getEntities();
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
						projectModel.getAssertionByName(cam.getName())
								.setArity(Integer.parseInt((nameText.getText())));
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
			populateProjectModel();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta rootDelta = event.getDelta();
		IResourceDelta affected[]= rootDelta.getAffectedChildren();
		for(int i=0;i<affected.length;i++){
			System.out.println(affected[i].getResource().getName());
			this.projectName = affected[i].getResource().getName();
		}
		WorkspaceModel instance = WorkspaceModel.getInstance();
		this.projectModel = instance.getProjectModel(this.projectName); 	
		
		System.out.println("Reload formView");
	}
}
