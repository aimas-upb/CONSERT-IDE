package org.aimas.consert.ide.editor.entitydescription;

import java.io.IOException;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.JsonTextEditor;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.EntityDescriptionModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EntityDescriptionTextEditor extends JsonTextEditor {
	EntityDescriptionMultiPageEditor parentEditor;

	public EntityDescriptionTextEditor(EntityDescriptionMultiPageEditor entityMultiPageEditor) {
		parentEditor = entityMultiPageEditor;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload EntityTextEditor");
	}

	private EntityDescriptionModel getNewEntityDescriptionModel() throws IOException {
		IDocumentProvider prov = this.getDocumentProvider();
		String newContent = prov.getDocument(getEditorInput()).get();
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		return mapper.readValue(newContent, EntityDescriptionModel.class);
	}

	private void updateEntityDescriptionInProjectModel() throws IOException {
		EntityDescriptionModel newCem = getNewEntityDescriptionModel();
		EditorInputWrapper eiw = (EditorInputWrapper) getEditorInput();
		ProjectModel projectModel = eiw.getProjectModel();
		EntityDescriptionModel oldCem = (EntityDescriptionModel) eiw.getModel();
		projectModel.getEntityDescriptionByName(oldCem.getName()).setName(newCem.getName());
		projectModel.getEntityDescriptionByName(oldCem.getName()).setObjectEntity(newCem.getObjectEntity());
		projectModel.getEntityDescriptionByName(oldCem.getName()).setSubjectEntity(newCem.getSubjectEntity());
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		System.out.println("[doSave EntityTextEditor]...");
		try {
			updateEntityDescriptionInProjectModel();
			/* Call formView save to save on disk and mark dirty flag */
			((EntityDescriptionFormView) parentEditor.getFormView()).doSave(monitor);
			super.doSave(monitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
