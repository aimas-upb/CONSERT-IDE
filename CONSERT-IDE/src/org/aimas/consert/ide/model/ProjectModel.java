package org.aimas.consert.ide.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.aimas.consert.ide.views.TreeViewerNew;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ProjectModel extends Observable {
	private IPath path;
	private JsonNode rootNode;
	private String projectName;
	private List<ContextEntityModel> entities;
	private List<ContextAssertionModel> assertions;

	private static ObjectMapper mapper = new ObjectMapper();
	private final static String BASE_URI = "org/aimas/consert/ide";

	public ProjectModel(String projectName) {
		this.addObserver(TreeViewerNew.getInstance());
		this.projectName = projectName;
		entities = new ArrayList<ContextEntityModel>();
		assertions = new ArrayList<ContextAssertionModel>();
	}

	public String getBaseURI() {
		return BASE_URI + ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getFullPath().toString();
	}

	public String getName() {
		return projectName;
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

	public void saveJsonOnDisk() {
		updateEntitiesJsonNode();
		updateAssertionsJsonNode();
		writeJsonOnDisk();
	}

	/** Write the new Json into File on disk, replacing the old one. */
	public void writeJsonOnDisk() {
		try {
			mapper.writeValue(new File(getPath().toString()), getRootNode());
			/*
			 * Notify TreeViewer the project has been modified on disk so
			 * TreeViewer can refresh too
			 */
			this.setChanged();
			this.notifyObservers();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save all entities since the formView does not track them individually, so
	 * it does not know which changed and which didn't.
	 */
	public void updateEntitiesJsonNode() {
		((ObjectNode) getRootNode()).withArray("ContextEntities").removeAll();
		for (ContextEntityModel cem : getEntities()) {
			((ObjectNode) getRootNode()).withArray("ContextEntities").add(mapper.valueToTree(cem));
		}
		System.out.println("Updated new entities into Json: " + getEntities());
	}

	/** Saving all assertions as well. */
	public void updateAssertionsJsonNode() {
		((ObjectNode) getRootNode()).withArray("ContextAssertions").removeAll();
		for (ContextAssertionModel cam : getAssertions()) {
			((ObjectNode) getRootNode()).withArray("ContextAssertions").add(mapper.valueToTree(cam));
		}
		System.out.println("Updated new assertions into Json: " + getAssertions());
	}

	/** Save newly created Context Model Element on empty Json File */
	public boolean saveNewModelOnDisk(Object model) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IFolder folder = project.getFolder("origin");
		if (!project.exists()) {
			System.out.println("project does not exist");
			return false;
		}
		saveOntologyOnDisk();
		/* Convert object to JSON string and save into file directly */
		try {
			FileInputStream in = new FileInputStream(folder.getFile("consert.txt").getLocation().toFile());
			JsonNode rootNode = mapper.readTree(in);
			in.close();

			if (rootNode.has("ContextAssertions") && model instanceof ContextAssertionModel) {
				((ObjectNode) rootNode).withArray("ContextAssertions").add(mapper.valueToTree(model));
				for (JsonNode assertion : rootNode.get("ContextAssertions")) {
					addAssertion(mapper.treeToValue(assertion, ContextAssertionModel.class));
				}
			} else if (rootNode.has("ContextEntities") && model instanceof ContextEntityModel) {
				((ObjectNode) rootNode).withArray("ContextEntities").add(mapper.valueToTree(model));
				for (JsonNode entity : rootNode.get("ContextEntities")) {
					addEntity(mapper.treeToValue(entity, ContextEntityModel.class));
				}
			} else {
				System.out.println("RootNode does not have this node");
				return false;
			}

			mapper.writeValue(new File(folder.getFile("consert.txt").getLocation().toString()), rootNode);
			/*
			 * Notify TreeViewer the project has been modified on disk so
			 * TreeViewer can refresh too
			 */
			this.setChanged();
			this.notifyObservers();
			System.out.println(model);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean saveOntologyOnDisk() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IFolder folder = project.getFolder("origin");
		if (!project.exists()) {
			System.out.println("project does not exist");
			return false;
		}
		try {
			POC.createAndSaveOntology(folder);
		} catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
			System.err.println("Ontology could not be created or stored.");
		}
		
		return true;
	}
}
