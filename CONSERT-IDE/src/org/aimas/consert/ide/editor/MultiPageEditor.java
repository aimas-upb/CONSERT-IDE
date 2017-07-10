package org.aimas.consert.ide.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;

public class MultiPageEditor extends FormEditor {
	private JsonSourceViewerConfiguration viewerConfiguration;
	private IEditorInput editorInput;
	/** The text editor used in page 0. */
	private TextEditor editor;

	public TextEditor getTextEditor() {
		return editor;
	}

	/** The form editor used in page 1. */
	private FormView formView;

	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
		this.editorInput = editorInput;
	}

	@Override
	protected void addPages() {
		try {
			addPage(new FormView(this));
			editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, "SourceView");
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating nested editors! ", null, e.getStatus());
		}

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		getEditor(1).doSave(monitor);
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

}
