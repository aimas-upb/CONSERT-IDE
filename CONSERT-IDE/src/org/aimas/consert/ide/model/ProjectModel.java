package org.aimas.consert.ide.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;

import com.fasterxml.jackson.databind.JsonNode;

public class ProjectModel {
	private IPath path;
	private JsonNode rootNode;
	private static ProjectModel instance;
	private List<ContextEntityModel> entities;
	private List<ContextAssertionModel> assertions;

	/**
	 * here to defeat instantiation
	 */
	private ProjectModel() {
		entities = new ArrayList<ContextEntityModel>();
		assertions = new ArrayList<ContextAssertionModel>();
	}

	public static ProjectModel getInstance() {
		if (instance == null) {
			instance = new ProjectModel();
		}
		return instance;
	}

	public void setRootNode(JsonNode rootNode) {
		this.rootNode = rootNode;
	}

	public JsonNode getRootNode() {
		return rootNode;
	}

	public void setPath(IPath path) {
		this.path = path;
	}

	public IPath getPath() {
		return path;
	}

	public List<ContextEntityModel> getEntities() {
		return entities;
	}

	public List<ContextAssertionModel> getAssertions() {
		return assertions;
	}

	public ContextEntityModel getEntityByName(String name) {
		for (ContextEntityModel cem : entities) {
			if (cem.getName().equals(name)) {
				return cem;
			}
		}
		return null;
	}

	public ContextAssertionModel getAssertionByName(String name) {
		for (ContextAssertionModel cam : assertions) {
			if (cam.getName().equals(name)) {
				return cam;
			}
		}
		return null;
	}

	public boolean addAssertion(ContextAssertionModel cam) {
		return assertions.add(cam);
	}

	public boolean removeAssertions(ContextAssertionModel cam) {
		return assertions.remove(cam);
	}

	public boolean addEntity(ContextEntityModel cem) {
		return entities.add(cem);
	}

	public boolean removeEntity(ContextEntityModel cem) {
		return entities.remove(cem);
	}
}
