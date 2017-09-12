package org.aimas.consert.ide.editor.assertion;

import java.io.IOException;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.JsonTextEditor;
import org.aimas.consert.ide.model.ContextAssertionModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AssertionTextEditor extends JsonTextEditor {
	AssertionMultiPageEditor parentEditor;

	public AssertionTextEditor(AssertionMultiPageEditor assertionMultiPageEditor) {
		parentEditor = assertionMultiPageEditor;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload AssertionTextEditor");
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		System.out.println("[doSave AssertionTextEditor]...");
		IDocumentProvider prov = this.getDocumentProvider();
		System.out.println(prov.getDocument(getEditorInput()).get());
		String content = prov.getDocument(getEditorInput()).get();
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			ContextAssertionModel newModel = mapper.readValue(content, ContextAssertionModel.class);
			WorkspaceModel.getInstance().getProjectModel(((EditorInputWrapper) getEditorInput()).getPm().getName()).getAssertions().remove(((EditorInputWrapper) getEditorInput()).getModel());
			WorkspaceModel.getInstance().getProjectModel(((EditorInputWrapper) getEditorInput()).getPm().getName()).getAssertions().add(newModel);
			// TreeViewerNew.getInstance().getView().setInput(TreeViewerNew.getInstance().getViewSite());
			// TreeViewerNew.getInstance().getView().refresh();
			((AssertionFormView) parentEditor.getFormView()).doSave(monitor);
			super.doSave(monitor);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println(eiw.getModel());
		// try {
		// System.out.println(eiw.getStorage().toString());
		// } catch (CoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// try {
		// IStorage ss = ((EditorInputWrapper) getEditorInput()).getStorage();
		// ByteArrayInputStream bis = (ByteArrayInputStream) ss.getContents();
		// BufferedReader br = new BufferedReader(new InputStreamReader(bis));
		// System.out.println("saving this MF: " + br.readLine());
		//
		// } catch (CoreException | IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// ProjectModel.getInstance().getAssertions().remove(cam);
		// ProjectModel.getInstance().getAssertions().add(cam);
	}
}
