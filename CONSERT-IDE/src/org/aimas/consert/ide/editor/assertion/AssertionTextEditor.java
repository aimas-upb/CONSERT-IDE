package org.aimas.consert.ide.editor.assertion;

import org.aimas.consert.ide.editor.JsonTextEditor;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.IProgressMonitor;

public class AssertionTextEditor extends JsonTextEditor {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload AssertionTextEditor");
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
	}
}
