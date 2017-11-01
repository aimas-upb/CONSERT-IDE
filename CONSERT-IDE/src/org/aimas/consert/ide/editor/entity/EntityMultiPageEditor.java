package org.aimas.consert.ide.editor.entity;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.MultiPageEditor;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class EntityMultiPageEditor extends MultiPageEditor {
	public final static String ID = "org.aimas.consert.ide.editor.entity.EntityMultiPageEditor";

	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		super.init(site, editorInput);
		if (!(editorInput instanceof EditorInputWrapper)) {
			throw new PartInitException("Invalid Input in EntityMultiPageEditor: Must be EditorInputWrapper");
		}

		setEditorName((EditorInputWrapper) editorInput);

		formView = new EntityFormView(this);
		textEditor = new EntityTextEditor(this);
	}

	private void setEditorName(EditorInputWrapper editorInput) {
		StringBuilder name = new StringBuilder("Entity:");
		name.append(((ContextEntityModel) editorInput.getModel()).getName());
		setPartName(name.toString());
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload EntityMultiPageEditor");
	}
}
