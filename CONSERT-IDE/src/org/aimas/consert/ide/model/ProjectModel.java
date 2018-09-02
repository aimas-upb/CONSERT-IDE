package org.aimas.consert.ide.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import org.aimas.consert.ide.views.TreeViewerNew;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class ProjectModel extends Observable {
	private IPath path;
	private JsonNode rootNode;
	private String name;
	private List<ContextEntityModel> entities = new ArrayList<>();
	private List<ContextAssertionModel> assertions = new ArrayList<>();
	private List<EntityDescriptionModel> entityDescriptions = new ArrayList<>();
	private List<ContextAnnotationModel> annotations = new ArrayList<>();
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final String BASE_URI = "http://example.org/org/aimas/consert/ide/";
	private OWLOntologyModel OWLModel;

	public ProjectModel(String name) {
		addObserver(TreeViewerNew.getInstance());
		this.name = name;
	}
	
	public String getBaseURI() {
		return BASE_URI + getName() + "/";
	}

	public String getName() {
		return name;
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
	
	public List<EntityDescriptionModel> getEntityDescriptions() {
		return entityDescriptions;
	}
	
	public List<ContextAnnotationModel> getAnnotations() {
		return annotations;
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
	
	public EntityDescriptionModel getEntityDescriptionByName(String name) {
		for (EntityDescriptionModel edm : entityDescriptions) {
			if (edm.getName().equals(name)) {
				return edm;
			}
		}
		return null;
	}
	
	public ContextAnnotationModel getAnnotationsByName(String name) {
		for (ContextAnnotationModel ann : annotations) {
			if (ann.getName().equals(name)) {
				return ann;
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
	
	public boolean addEntityDescription(EntityDescriptionModel edm) {
		return entityDescriptions.add(edm);
	}
	
	public boolean removeEntityDescription(EntityDescriptionModel edm) {
		return entityDescriptions.remove(edm);
	}

	public boolean removeEntity(ContextEntityModel cem) {
		return entities.remove(cem);
	}
	
	public boolean addAnnotation(ContextAnnotationModel ann) {
		return annotations.add(ann);
	}

	public boolean removeAnnotation(ContextAnnotationModel ann) {
		return annotations.remove(ann);
	}
	
	public void initializeOWLModel(File OWLfile, File TTLfile) {
		OWLModel = new OWLOntologyModel(OWLfile, TTLfile, getBaseURI());
		//TODO decomenteaza dupa ce este implementata metoda
//		OWLModel.loadOWLOntologyModelFromFile();
	}
	
	public void syncOWLModelWithProjectModel() {
		OWLModel.syncOWLModelWithProjectModel(entities, assertions, annotations, entityDescriptions);
	}
	
	public void saveOWLModelToDisk() {
		OWLModel.saveModelOnDisk();
	}

	public void saveJsonOnDisk() {
		updateEntitiesJsonNode();
		updateAssertionsJsonNode();
		updateEntityDescriptionsJsonNode();
		updateAnnotationsJsonNode();
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
			setChanged();
			notifyObservers();
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
	public boolean saveNewModelJSONOnDisk(IFolder folder, Object model) {
		
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
			} else if (rootNode.has("EntityDescriptions") && model instanceof EntityDescriptionModel) {
				((ObjectNode) rootNode).withArray("EntityDescriptions").add(mapper.valueToTree(model));
				for (JsonNode entityDscription : rootNode.get("EntityDescriptions")) {
					addEntityDescription(mapper.treeToValue(entityDscription, EntityDescriptionModel.class));
				}
			} else if (rootNode.has("ContextAnnotations") && model instanceof ContextAnnotationModel) {
				((ObjectNode) rootNode).withArray("ContextAnnotations").add(mapper.valueToTree(model));
				for (JsonNode annotation : rootNode.get("ContextAnnotations")) {
					addAnnotation(mapper.treeToValue(annotation, ContextAnnotationModel.class));
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
			setChanged();
			notifyObservers();
			System.out.println(model);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean saveNewModelJSONOnDisk(Object model) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		IFolder folder = project.getFolder("origin");
		if (!project.exists()) {
			System.out.println("project does not exist");
			return false;
		}
		
		//Save JSON
		saveNewModelJSONOnDisk(folder,model);
		
		setChanged();
		notifyObservers();
		
		return true;
	}
	
	
	/**
	 * Save all entity descriptions since the formView does not track them individually, so
	 * it does not know which changed and which didn't.
	 */
	public void updateEntityDescriptionsJsonNode() {
		((ObjectNode) getRootNode()).withArray("EntityDescriptions").removeAll();
		for (EntityDescriptionModel edm : getEntityDescriptions()) {
			((ObjectNode) getRootNode()).withArray("EntityDescriptions").add(mapper.valueToTree(edm));
		}
		System.out.println("Updated new entity descriptions into Json: " + getEntityDescriptions());
	}
	
	/**
	 * Save all annotations since the formView does not track them individually, so
	 * it does not know which changed and which didn't.
	 */
	public void updateAnnotationsJsonNode() {
		((ObjectNode) getRootNode()).withArray("ContextAnnotations").removeAll();
		for (ContextAnnotationModel ann : getAnnotations()) {
			((ObjectNode) getRootNode()).withArray("ContextAnnotations").add(mapper.valueToTree(ann));
		}
		System.out.println("Updated new annotations into Json: " + getAnnotations());
	}
	

}
