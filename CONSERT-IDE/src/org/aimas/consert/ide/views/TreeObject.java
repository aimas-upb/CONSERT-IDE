package org.aimas.consert.ide.views;

import org.aimas.consert.ide.views.TreeViewerNew.TreeParent;
import org.eclipse.core.runtime.IAdaptable;

public class TreeObject<T> implements IAdaptable {
	private String name;
	private TreeParent<T> parent;
	private T resource;
	private String projectName;

	public TreeObject(String name, String projectName) {
		this.name = name;
		this.projectName = projectName;
	}

	public String getName() {
		return name;
	}
	
	public String getProjectName() {
		return projectName;
	}

	public void setParent(TreeParent<T> parent) {
		this.parent = parent;
	}

	public TreeParent<T> getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}

	public Object getAdapter(Class key) {
		return null;
	}

	protected T getResource() {
		return resource;
	}

	protected void setResource(T resource) {
		this.resource = resource;
	}

}
