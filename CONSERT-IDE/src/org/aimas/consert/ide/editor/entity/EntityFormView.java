package org.aimas.consert.ide.editor.entity;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
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

public class EntityFormView extends FormPage implements IResourceChangeListener {
	private EntityMultiPageEditor editor;
	private ScrolledForm form;
	private boolean isDirty;
	private ContextEntityModel cem;
	private String projectName;
	private ProjectModel projectModel;
	public static final String ID = "org.aimas.consert.ide.editor.entity.EntityFormView";

	public EntityFormView(EntityMultiPageEditor editor) {
		super(editor, ID, "EntityFormView");
		this.editor = editor;
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
					projectModel.getEntityByName(cem.getName()).setName(nameText.getText());
				} else if (labelName.equals(" Comment: "))
					projectModel.getEntityByName(cem.getName()).setComment(nameText.getText());
			}
		});
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		projectModel.updateEntitiesJsonNode();
		projectModel.writeJsonOnDisk();
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
		IResourceDelta rootDelta = event.getDelta();
		IResourceDelta affected[] = rootDelta.getAffectedChildren();
		for (int i = 0; i < affected.length; i++) {
			System.out.println(affected[i].getResource().getName());
			projectName = affected[i].getResource().getName();
		}
		projectModel = WorkspaceModel.getInstance().getProjectModel(projectName);
		System.out.println("Reload EntityformView");
	}
}
