package org.aimas.consert.ide.editor.assertion;

import java.util.List;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.MultiPageEditor;
import org.aimas.consert.ide.editor.entity.EntityMultiPageEditor;
import org.aimas.consert.ide.model.AcquisitionType;
import org.aimas.consert.ide.model.ContextAssertionModel;
import org.aimas.consert.ide.model.ContextEntityModel;
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

public class AssertionFormView extends FormPage implements IResourceChangeListener {
	private MultiPageEditor editor;
	private ScrolledForm form;
	private boolean isDirty;
	private ContextAssertionModel cam;
	private ProjectModel projectModel;
	public static final String ID = "org.aimas.consert.ide.editor.assertion.AssertionFormView";

	public AssertionFormView(MultiPageEditor editor) {
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

	public void createLabelAndTextForEntity(String labelName, String textName, ContextEntityModel cem) {
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
					cam.setSubjectEntity(cem);
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
		projectModel.updateAssertionsJsonNode();
		projectModel.writeJsonOnDisk();
		isDirty = false;
		firePropertyChange(PROP_DIRTY);
		editor.editorDirtyStateChanged();
	}

	private void createLabelAndCombo(String string, String acquisitionTypeText) {
		Label labelAcquisitionType = new Label(form.getBody(), SWT.NONE);
		labelAcquisitionType.setText("AcquisitionType");

		CCombo comboAcquisitionType = new CCombo(form.getBody(), SWT.READ_ONLY);
		String items[] = { AcquisitionType.DERIVED.toString(), AcquisitionType.PROFILED.toString(),
				AcquisitionType.SENSED.toString() };
		comboAcquisitionType.setItems(items);
		comboAcquisitionType.setText(acquisitionTypeText);
		comboAcquisitionType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = comboAcquisitionType.getSelectionIndex();
				String acquisitionTypeName = comboAcquisitionType.getItem(index == -1 ? 0 : index);
				/*
				 * if a different acquisitionType was selected, set it and mark
				 * editor dirty
				 */
				if (!acquisitionTypeText.equals(acquisitionTypeName)) {
					cam.setAcquisitionType(AcquisitionType.toValue(acquisitionTypeName));
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
		cam = (ContextAssertionModel) eiw.getModel();
		projectModel = eiw.getProjectModel();
		form.setText(cam.getName());
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		layout.numColumns = 3;

		System.out.println("Assertion inside Assertion Form View parsed: " + cam.toString());

		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(" ContextAssertion: ");
		new Label(form.getBody(), SWT.NONE);
		new Label(form.getBody(), SWT.NONE);

		createLabelAndText(" Name: ", cam.getName());
		new Label(form.getBody(), SWT.NONE);
		createLabelAndText(" Comment: ", cam.getComment());
		new Label(form.getBody(), SWT.NONE);
		createLabelAndText(" Arity: ", Integer.toString(cam.getArity()));
		new Label(form.getBody(), SWT.NONE);
		createLabelAndCombo(" Acquisition Type: ", cam.getAcquisitionType().toString());
		new Label(form.getBody(), SWT.NONE);

		Label entitiesNameLabel = new Label(form.getBody(), SWT.NONE);
		entitiesNameLabel.setText(" ContextEntities: ");
		new Label(form.getBody(), SWT.NONE);
		new Label(form.getBody(), SWT.NONE);

		/* combos for entities selection */
		Label nameSubjectLabel = new Label(form.getBody(), SWT.NONE);
		nameSubjectLabel.setText(" Subject Entity: ");
		addEntityCComboBox(cam.getSubjectEntity());
		adddEntityGotoButton(cam.getSubjectEntity());

		Label nameObjectLabel = new Label(form.getBody(), SWT.NONE);
		nameObjectLabel.setText(" Object Entity: ");
		addEntityCComboBox(cam.getObjectEntity());
		adddEntityGotoButton(cam.getObjectEntity());
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		// IResourceDelta rootDelta = event.getDelta();
		// IResourceDelta affected[] = rootDelta.getAffectedChildren();
		// for (int i = 0; i < affected.length; i++) {
		// System.out.println(affected[i].getResource().getName());
		// projectName = affected[i].getResource().getName();
		// }
		// projectModel =
		// WorkspaceModel.getInstance().getProjectModel(projectName);
		System.out.println("Reload AssertionformView");
	}
}
