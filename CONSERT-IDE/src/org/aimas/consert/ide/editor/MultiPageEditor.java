package org.aimas.consert.ide.editor;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;

public class MultiPageEditor extends FormEditor implements IResourceChangeListener {
	/** The form editor used in page 0. */
	private FormPage formView;
	/** The text editor used in page 1. */
	private JsonTextEditor textEditor;
	public final static String ID = "org.aimas.consert.ide.editor.ConsertEditor";

	public MultiPageEditor() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public TextEditor getTextEditor() {
		return textEditor;
	}

	@Override
	public boolean isDirty() {
		return formView.isDirty() || textEditor.isDirty();
	}

	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		super.init(site, editorInput);
		if ((editorInput instanceof EditorInputWrapper)) {
			formView = new EntityFormView(this);
			textEditor = new JsonTextEditor();
		} else if ((editorInput instanceof IFileEditorInput)) {
			formView = new FormView(this);
			textEditor = new JsonTextEditor();
		} else {
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		}
	}

	@Override
	protected void addPages() {
		try {
			addPage(formView);
			int index = addPage(textEditor, getEditorInput());
			setPageText(index, "SourceView");
		} catch (PartInitException e) {
			e.printStackTrace();
			ErrorDialog.openError(getSite().getShell(),
					"Boss, error creating nested editors in L:60(MultiPageEditor.java) ", null, e.getStatus());
		}

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		IFormPage page = getActivePageInstance();
		if (page == null) {
			getEditor(1).doSave(monitor);
		} else {
			((EntityFormView) page).doSave(monitor);
		}

	}

	@Override
	public void doSaveAs() {
		IEditorPart editor = getEditor(1);
		editor.doSaveAs();
		setPageText(1, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload MultiPageEditor");
	}
}
