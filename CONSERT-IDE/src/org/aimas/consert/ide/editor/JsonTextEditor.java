package org.aimas.consert.ide.editor;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.editors.text.TextEditor;

public class JsonTextEditor extends TextEditor implements IResourceChangeListener {

	public JsonTextEditor() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload JsonTextEditor");
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
	}

}
