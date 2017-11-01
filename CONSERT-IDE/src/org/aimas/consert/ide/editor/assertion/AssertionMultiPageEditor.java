package org.aimas.consert.ide.editor.assertion;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.MultiPageEditor;
import org.aimas.consert.ide.model.ContextAssertionModel;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class AssertionMultiPageEditor extends MultiPageEditor {
	public final static String ID = "org.aimas.consert.ide.editor.assertion.AssertionMultiPageEditor";

	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		super.init(site, editorInput);
		if (!(editorInput instanceof EditorInputWrapper)) {
			throw new PartInitException("Invalid Input in AssertionMultiPageEditor: Must be EditorInputWrapper");
		}

		setEditorName((EditorInputWrapper) editorInput);

		formView = new AssertionFormView(this);
		textEditor = new AssertionTextEditor(this);
	}

	private void setEditorName(EditorInputWrapper editorInput) {
		StringBuilder name = new StringBuilder("Assertion:");
		name.append(((ContextAssertionModel) editorInput.getModel()).getName());
		setPartName(name.toString());
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload AssertionMultiPageEditor");
	}
}
