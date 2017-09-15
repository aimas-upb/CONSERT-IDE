package org.aimas.consert.ide.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

	public void saveOnDisk() {
		ObjectMapper mapper = new ObjectMapper();

		/*
		 * Saving all entities, because the formView does not track them
		 * individually, so it does not know which changed and which didn't.
		 */
		((ObjectNode) rootNode).withArray("ContextEntities").removeAll();
		for (ContextEntityModel cem : getEntities()) {
			((ObjectNode) rootNode).withArray("ContextEntities").add(mapper.valueToTree(cem));
		}
		System.out.println("[doSave] maped new entities into Json: " + getEntities());

		/* Saving all assertions as well. */
		((ObjectNode) rootNode).withArray("ContextAssertions").removeAll();
		for (ContextAssertionModel cam : getAssertions()) {
			((ObjectNode) rootNode).withArray("ContextAssertions").add(mapper.valueToTree(cam));
		}
		System.out.println("[doSave] maped new assertions into Json: " + getAssertions());
		/* Write on disk the new Json into File, replacing the old one. */
		try {
			mapper.writeValue(new File(path.toString()), rootNode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean appendToFile(String projectName, Object model) {
		ObjectMapper mapper = new ObjectMapper();

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IFolder folder = project.getFolder("origin");
		if (!project.exists()) {
			System.out.println("project does not exist");
			return false;
		}

		// Convert object to JSON string and save into file directly
		try {
			FileInputStream in = new FileInputStream(folder.getFile("consert.txt").getLocation().toFile());
			JsonNode rootNode = mapper.readTree(in);
			in.close();

			if (rootNode.has("ContextAssertions") && model instanceof ContextAssertionModel) {
				((ObjectNode) rootNode).withArray("ContextAssertions").add(mapper.valueToTree(model));
				for (JsonNode entity : rootNode.get("ContextAssertions"))
					ProjectModel.getInstance().addAssertion(mapper.treeToValue(entity, ContextAssertionModel.class));
			} else if (rootNode.has("ContextEntities") && model instanceof ContextEntityModel) {
				((ObjectNode) rootNode).withArray("ContextEntities").add(mapper.valueToTree(model));
				for (JsonNode entity : rootNode.get("ContextEntities"))
					ProjectModel.getInstance().addEntity(mapper.treeToValue(entity, ContextEntityModel.class));
			} else {
				System.out.println("RootNode does not have this node");
				return false;
			}

			mapper.writeValue(new File(folder.getFile("consert.txt").getLocation().toString()), rootNode);
			System.out.println(model);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
