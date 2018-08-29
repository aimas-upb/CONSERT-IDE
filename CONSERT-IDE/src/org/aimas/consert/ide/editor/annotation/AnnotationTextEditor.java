package org.aimas.consert.ide.editor.annotation;

import java.io.IOException;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.JsonTextEditor;
import org.aimas.consert.ide.model.ContextAnnotationModel;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AnnotationTextEditor extends JsonTextEditor {
	AnnotationMultiPageEditor parentEditor;

	public AnnotationTextEditor(AnnotationMultiPageEditor annotationMultiPageEditor) {
		parentEditor = annotationMultiPageEditor;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload AnnotationTextEditor");
	}

	private ContextAnnotationModel getNewAnnotationModel() throws IOException {
		IDocumentProvider prov = this.getDocumentProvider();
		String newContent = prov.getDocument(getEditorInput()).get();
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		return mapper.readValue(newContent, ContextAnnotationModel.class);
	}

	private void updateAnnotationInProjectModel() throws IOException {
		ContextAnnotationModel newCem = getNewAnnotationModel();
		EditorInputWrapper eiw = (EditorInputWrapper) getEditorInput();
		ProjectModel projectModel = eiw.getProjectModel();
		ContextAnnotationModel oldCem = (ContextAnnotationModel) eiw.getModel();
		projectModel.getAnnotationsByName(oldCem.getName()).setName(newCem.getName());
		projectModel.getAnnotationsByName(oldCem.getName()).setAnnotationCategory(newCem.getAnnotationCategory());
		projectModel.getAnnotationsByName(oldCem.getName()).setAnnotationType(newCem.getAnnotationType());
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		System.out.println("[doSave EntityTextEditor]...");
		try {
			updateAnnotationInProjectModel();
			/* Call formView save to save on disk and mark dirty flag */
			((AnnotationFormView) parentEditor.getFormView()).doSave(monitor);
			super.doSave(monitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
