package org.aimas.consert.ide.editor.entity;

import java.io.File;
import java.io.IOException;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EntityFormView extends FormPage implements IResourceChangeListener {
	private EntityMultiPageEditor editor;
	private ScrolledForm form;
	private boolean isDirty;
	private ContextEntityModel cem;
	public static final String ID = "org.aimas.consert.ide.editor.EntityFormView";

	public EntityFormView(EntityMultiPageEditor entityMultiPageEditor) {
		super(entityMultiPageEditor, ID, "EntityFormView");
		this.editor = entityMultiPageEditor;
		isDirty = false;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public void createLabelAndText(String labelName, String textName) {
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
					ProjectModel.getInstance().getEntityByName(cem.getName()).setName(nameText.getText());
				} else if (labelName.equals(" Comment: "))
					ProjectModel.getInstance().getEntityByName(cem.getName()).setComment(nameText.getText());
			}
		});
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		IPath path = ProjectModel.getInstance().getPath();
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = ProjectModel.getInstance().getRootNode();
			((ObjectNode) rootNode).withArray("ContextEntities").removeAll();
			for (Object cem : ProjectModel.getInstance().getEntities()) {
				System.out.println("[doSave] new map values: " + ProjectModel.getInstance().getEntities());
				((ObjectNode) rootNode).withArray("ContextEntities").add(mapper.valueToTree((ContextEntityModel) cem));
			}
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

		IEditorInput ied = getEditorInput();
		cem = (ContextEntityModel) ((EditorInputWrapper) ied).getModel();
		form.setText(cem.getName());
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		layout.numColumns = 2;

		System.out.println("Entity inside Entity Form View parsed: " + cem.toString());
		String name = cem.getName();
		String comment = cem.getComment();
		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(" ContextEntitity: ");
		new Label(form.getBody(), SWT.NONE);

		createLabelAndText(" Name: ", name);
		createLabelAndText(" Comment: ", comment);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload EntityformView");
	}
}
