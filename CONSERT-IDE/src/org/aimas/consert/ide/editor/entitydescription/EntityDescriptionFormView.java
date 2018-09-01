package org.aimas.consert.ide.editor.entitydescription;

import java.util.List;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.entity.EntityMultiPageEditor;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.EntityDescriptionModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.aimas.consert.ide.util.Utils;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class EntityDescriptionFormView extends FormPage implements IResourceChangeListener {
	private EntityDescriptionMultiPageEditor editor;
	private ScrolledForm form;
	private boolean isDirty;
	private EntityDescriptionModel edm;
	private ProjectModel projectModel;
	public static final String ID = "org.aimas.consert.ide.editor.entitydescription.EntityDescriptionFormView";

	public EntityDescriptionFormView(EntityDescriptionMultiPageEditor editor) {
		super(editor, ID, "EntityDescriptionFormView");
		this.editor = editor;
		isDirty = false;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public void createLabelAndText(String labelName, String textName) {
		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(labelName);
		Text nameText = new Text(form.getBody(), SWT.BORDER | SWT.SINGLE);
		if (textName == null) {
			textName = "";
		}
		nameText.setText(textName);
		nameText.setLayoutData(new GridData(180, 30));
		nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				isDirty = true;
				firePropertyChange(IEditorPart.PROP_DIRTY);
				editor.editorDirtyStateChanged();

				if (labelName.equals(" Name: ")) {
					projectModel.getEntityByName(edm.getName()).setName(nameText.getText());
				} 
			}
		});
	}
	

	public ContextEntityModel getSelectedEntity(CCombo combo, List<ContextEntityModel> allEntities) {
		int index = combo.getSelectionIndex();
		String entityName = combo.getItem(index == -1 ? 0 : index);
		for (ContextEntityModel cem : allEntities) {
			if (cem.getName().equals(entityName)) {
				return cem;
			}
		}
		return null;
	}

	private void addEntityCComboBox(ContextEntityModel givenEntity) {
		CCombo comboEntities = new CCombo(form.getBody(), SWT.READ_ONLY);
		List<ContextEntityModel> allEntities = Utils.getInstance().getAllEntities();
		String items[] = Utils.getInstance().getAllEntitiesStringNames(allEntities);
		comboEntities.setItems(items);
		comboEntities.setText(givenEntity.getName());
		comboEntities.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ContextEntityModel cem = getSelectedEntity(comboEntities, allEntities);
				/*
				 * if a different entity was selected, set it and mark editor
				 * dirty
				 */
				if (!givenEntity.getName().equals(cem.getName())) {
					edm.setSubjectEntity(cem);
					isDirty = true;
					firePropertyChange(IEditorPart.PROP_DIRTY);
					editor.editorDirtyStateChanged();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}


	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		projectModel.updateEntityDescriptionsJsonNode();
		projectModel.writeJsonOnDisk();
		isDirty = false;
		firePropertyChange(PROP_DIRTY);
		editor.editorDirtyStateChanged();
	}


	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload EntityDescriptionFormView");
	}
	
	private void adddEntityGotoButton(ContextEntityModel givenEntity) {
		Button entityGotoButton = new Button(form.getBody(), SWT.NONE);
		entityGotoButton.setText("Goto");
		entityGotoButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				/*
				 * On mouse click, open the selected entity in an EntityEditor
				 */
				EditorInputWrapper eiw = new EditorInputWrapper(givenEntity);
				eiw.setProjectModel(projectModel);
				IWorkbenchPage page = getEditorSite().getWorkbenchWindow().getActivePage();
				try {
					page.openEditor(eiw, EntityMultiPageEditor.ID);
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();

		EditorInputWrapper eiw = (EditorInputWrapper) getEditorInput();
		edm = (EntityDescriptionModel) eiw.getModel();
		projectModel = eiw.getProjectModel();
		form.setText(edm.getName());
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		layout.numColumns = 3;

		System.out.println("Entity Description inside Entity Description Form View parsed: " + edm.toString());

		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(" EntityDescription: ");
		new Label(form.getBody(), SWT.NONE);
		new Label(form.getBody(), SWT.NONE);

		createLabelAndText(" Name: ", edm.getName());
		new Label(form.getBody(), SWT.NONE);

		Label entitiesNameLabel = new Label(form.getBody(), SWT.NONE);
		entitiesNameLabel.setText(" ContextEntities: ");
		entitiesNameLabel.setLayoutData(new GridData(180, 30));
		new Label(form.getBody(), SWT.NONE);
		new Label(form.getBody(), SWT.NONE);

		/* combos for entities selection */
		Label nameSubjectLabel = new Label(form.getBody(), SWT.NONE);
		nameSubjectLabel.setText(" Subject Entity: ");
		nameSubjectLabel.setLayoutData(new GridData(180, 30));
		addEntityCComboBox(edm.getSubjectEntity());
		adddEntityGotoButton(edm.getSubjectEntity());
		
		Label nameObjectLabel = new Label(form.getBody(), SWT.NONE);
		nameObjectLabel.setText(" Object Entity: ");
		nameObjectLabel.setLayoutData(new GridData(180, 30));
		addEntityCComboBox(edm.getObjectEntity());
		adddEntityGotoButton(edm.getObjectEntity());

	}
	
	
}
