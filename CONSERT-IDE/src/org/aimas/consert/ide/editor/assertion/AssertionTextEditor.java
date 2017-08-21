package org.aimas.consert.ide.editor.assertion;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.JsonTextEditor;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class AssertionTextEditor extends JsonTextEditor {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload AssertionTextEditor");
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		try {
			IStorage ss = ((EditorInputWrapper) getEditorInput()).getStorage();
			ByteArrayInputStream bis = (ByteArrayInputStream) ss.getContents();
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));
			System.out.println("saving this MF: " + br.readLine());

		} catch (CoreException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ProjectModel.getInstance().getAssertions().remove(cam);
		// ProjectModel.getInstance().getAssertions().add(cam);
	}
}
