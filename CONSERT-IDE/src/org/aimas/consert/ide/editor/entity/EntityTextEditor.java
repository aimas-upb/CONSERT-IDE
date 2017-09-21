package org.aimas.consert.ide.editor.entity;

import java.io.IOException;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.JsonTextEditor;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EntityTextEditor extends JsonTextEditor {
	EntityMultiPageEditor parentEditor;

	public EntityTextEditor(EntityMultiPageEditor entityMultiPageEditor) {
		parentEditor = entityMultiPageEditor;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload EntityTextEditor");
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		System.out.println("[doSave EntityTextEditor]...");
		IDocumentProvider prov = this.getDocumentProvider();
		String newContent = prov.getDocument(getEditorInput()).get();
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			ContextEntityModel newModel = mapper.readValue(newContent, ContextEntityModel.class);
			WorkspaceModel.getInstance()
					.getProjectModel(((EditorInputWrapper) getEditorInput()).getProjectModel().getName()).getEntities()
					.remove(((EditorInputWrapper) getEditorInput()).getModel());
			WorkspaceModel.getInstance()
					.getProjectModel(((EditorInputWrapper) getEditorInput()).getProjectModel().getName()).getEntities()
					.add(newModel);
			((EntityFormView) parentEditor.getFormView()).doSave(monitor);
			super.doSave(monitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
