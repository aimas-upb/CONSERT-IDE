package org.aimas.consert.ide.editor.assertion;

import java.io.IOException;
import java.util.List;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.JsonTextEditor;
import org.aimas.consert.ide.model.ContextAssertionModel;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
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

	private ContextAssertionModel getNewAssertionModel() throws IOException {
		IDocumentProvider prov = this.getDocumentProvider();
		System.out.println(prov.getDocument(getEditorInput()).get());
		String content = prov.getDocument(getEditorInput()).get();
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		return mapper.readValue(content, ContextAssertionModel.class);
	}

	private void updateAssertionInProjectModel() throws IOException {
		ContextAssertionModel newCam = getNewAssertionModel();
		EditorInputWrapper eiw = (EditorInputWrapper) getEditorInput();
		ProjectModel projectModel = eiw.getProjectModel();
		ContextAssertionModel oldCam = (ContextAssertionModel) eiw.getModel();
		projectModel.getAssertionByName(oldCam.getName()).setName(newCam.getName());
		projectModel.getAssertionByName(oldCam.getName()).setComment(newCam.getComment());
		projectModel.getAssertionByName(oldCam.getName()).setArity(newCam.getArity());

		List<ContextEntityModel> entities = projectModel.getAssertionByName(oldCam.getName()).getEntities();
		entities.clear();
		entities.addAll(newCam.getEntities());
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		System.out.println("[doSave AssertionTextEditor]...");
		try {
			// TreeViewerNew.getInstance().getView().setInput(TreeViewerNew.getInstance().getViewSite());
			// TreeViewerNew.getInstance().getView().refresh();
			updateAssertionInProjectModel();
			/* Call formView save to save on disk and mark dirty flag */
			((AssertionFormView) parentEditor.getFormView()).doSave(monitor);
			super.doSave(monitor);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println(eiw.getModel());
		// try {
		// System.out.println(eiw.getStorage().toString());
		// } catch (CoreException e) {
		// e.printStackTrace();
		// }
		// try {
		// IStorage ss = ((EditorInputWrapper) getEditorInput()).getStorage();
		// ByteArrayInputStream bis = (ByteArrayInputStream) ss.getContents();
		// BufferedReader br = new BufferedReader(new InputStreamReader(bis));
		// System.out.println("saving this MF: " + br.readLine());
		//
		// } catch (CoreException | IOException e) {
		// e.printStackTrace();
		// }
		// ProjectModel.getInstance().getAssertions().remove(cam);
		// ProjectModel.getInstance().getAssertions().add(cam);
	}
}
