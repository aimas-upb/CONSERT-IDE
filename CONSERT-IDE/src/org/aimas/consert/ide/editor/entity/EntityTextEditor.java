package org.aimas.consert.ide.editor.entity;

import org.aimas.consert.ide.editor.JsonTextEditor;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.IProgressMonitor;

public class EntityTextEditor extends JsonTextEditor {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload EntityTextEditor");
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
	}
}
