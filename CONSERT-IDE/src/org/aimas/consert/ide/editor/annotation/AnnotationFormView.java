package org.aimas.consert.ide.editor.annotation;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.model.AnnotationCategory;
import org.aimas.consert.ide.model.AnnotationType;
import org.aimas.consert.ide.model.ContextAnnotationModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class AnnotationFormView extends FormPage implements IResourceChangeListener {
	private AnnotationMultiPageEditor editor;
	private ScrolledForm form;
	private boolean isDirty;
	private ContextAnnotationModel ann;
	private ProjectModel projectModel;
	public static final String ID = "org.aimas.consert.ide.editor.annotation.AnnotationFormView";

	public AnnotationFormView(AnnotationMultiPageEditor editor) {
		super(editor, ID, "AnnotationFormView");
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
					projectModel.getAnnotationsByName(ann.getName()).setName(nameText.getText());
				}
			}
		});
	}

	private void createLabelAndComboAnnotationType(String string, String annotationTypeText) {
		Label labelAnnotationType = new Label(form.getBody(), SWT.NONE);
		labelAnnotationType.setText("AnnotationType");
		labelAnnotationType.setLayoutData(new GridData(180, 30));

		CCombo comboAnnotationType = new CCombo(form.getBody(), SWT.READ_ONLY);
		String itemsAnnotationType[] = { AnnotationType.TIMESTAMP.toString(), AnnotationType.TRUST.toString() };
		comboAnnotationType.setItems(itemsAnnotationType);
		comboAnnotationType.setText(annotationTypeText);
		comboAnnotationType.setLayoutData(new GridData(180, 30));
		comboAnnotationType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = comboAnnotationType.getSelectionIndex();
				String annotationTypeName = comboAnnotationType.getItem(index == -1 ? 0 : index);
				/*
				 * if a different acquisitionType was selected, set it and mark
				 * editor dirty
				 */
				if (!annotationTypeText.equals(annotationTypeName)) {
					ann.setAnnotationType(AnnotationType.toValue(annotationTypeName));
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

	private void createLabelAndComboAnnotationCategory(String string, String annotationTypeText) {
		Label labelAnnotationCategory = new Label(form.getBody(), SWT.NONE);
		labelAnnotationCategory.setText("AnnotationCategory");
		labelAnnotationCategory.setLayoutData(new GridData(180, 30));

		CCombo comboAnnotationCategory = new CCombo(form.getBody(), SWT.READ_ONLY);
		String itemsAnnotationCategory[] = { AnnotationCategory.SIMPLE.toString(),
				AnnotationCategory.STRUCTURED.toString() };
		comboAnnotationCategory.setItems(itemsAnnotationCategory);
		comboAnnotationCategory.setText(annotationTypeText);
		comboAnnotationCategory.setLayoutData(new GridData(180, 30));
		comboAnnotationCategory.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = comboAnnotationCategory.getSelectionIndex();
				String annotationCategoryName = comboAnnotationCategory.getItem(index == -1 ? 0 : index);
				/*
				 * if a different acquisitionType was selected, set it and mark
				 * editor dirty
				 */
				if (!annotationTypeText.equals(annotationCategoryName)) {
					ann.setAnnotationCategory(AnnotationCategory.toValue(annotationCategoryName));
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
		projectModel.updateAnnotationsJsonNode();
		projectModel.writeJsonOnDisk();
		isDirty = false;
		firePropertyChange(PROP_DIRTY);
		editor.editorDirtyStateChanged();
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload EntityformView");
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();

		EditorInputWrapper eiw = (EditorInputWrapper) getEditorInput();
		ann = (ContextAnnotationModel) eiw.getModel();
		projectModel = eiw.getProjectModel();
		form.setText(ann.getName());
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		layout.numColumns = 2;

		System.out.println("Annotation inside Annotation Form View parsed: " + ann.toString());

		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(" ContextAnnotation: ");
		new Label(form.getBody(), SWT.NONE);

		createLabelAndText(" Name: ", ann.getName());
//		new Label(form.getBody(), SWT.NONE);

		createLabelAndComboAnnotationType(" Annotation Type: ", ann.getAnnotationType().toString());

		createLabelAndComboAnnotationCategory(" Annotation Category: ", ann.getAnnotationCategory().toString());

	}

}
