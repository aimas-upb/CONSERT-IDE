package org.aimas.consert.ide.editor.entity;

import java.io.IOException;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.JsonTextEditor;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
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

	private ContextEntityModel getNewEntityModel() throws IOException {
		IDocumentProvider prov = this.getDocumentProvider();
		String newContent = prov.getDocument(getEditorInput()).get();
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		return mapper.readValue(newContent, ContextEntityModel.class);
	}

	private void updateEntityInProjectModel() throws IOException {
		ContextEntityModel newCem = getNewEntityModel();
		EditorInputWrapper eiw = (EditorInputWrapper) getEditorInput();
		ProjectModel projectModel = eiw.getProjectModel();
		ContextEntityModel oldCem = (ContextEntityModel) eiw.getModel();
		projectModel.getEntityByName(oldCem.getName()).setName(newCem.getName());
		projectModel.getEntityByName(oldCem.getName()).setComment(newCem.getComment());
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		System.out.println("[doSave EntityTextEditor]...");
		try {
			updateEntityInProjectModel();
			/* Call formView save to save on disk and mark dirty flag */
			((EntityFormView) parentEditor.getFormView()).doSave(monitor);
			super.doSave(monitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
