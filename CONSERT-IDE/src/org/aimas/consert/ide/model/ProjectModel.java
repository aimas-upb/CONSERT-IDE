package org.aimas.consert.ide.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.aimas.consert.ide.util.OWLUtils;
import org.aimas.consert.ide.views.TreeViewerNew;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

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
		OWLModel.loadOWLOntologyModelFromFile();
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
	
	/**
	 * This method is used to load the Context Entities (which were read in the OWLOntologyModel from the OWL file) in the ProjectModel
	 */
	public void loadEntities() {
	 HashMap<String,List<OWLAxiom>> allEntityAxiomHash = OWLModel.getallEntityAxiomHash();
   	 Iterator itEntities = allEntityAxiomHash.entrySet().iterator();
   	 
   	    while (itEntities.hasNext()) {
   	    	Map.Entry<String, List<OWLAxiom>> pair = (Map.Entry)itEntities.next();
	        List<OWLAxiom> entityAxiomList = pair.getValue();
	        
	        for(OWLAxiom ax : entityAxiomList){
	        	if(ax.isOfType(AxiomType.ANNOTATION_ASSERTION)){
	        		OWLAnnotationAssertionAxiom a2 =  (OWLAnnotationAssertionAxiom) ax;
		        	if(a2.getProperty().isComment()){
			        	OWLLiteral val = (OWLLiteral)a2.getValue();
			        	
			   	    	ContextEntityModel cem = new ContextEntityModel(pair.getKey(),val.getLiteral());
			   	    	addEntity(cem);
			        }
	        	}
	        }
   	    }
	}
	
	/**
	 * This method is used to load the Context Assertions (which were read in the OWLOntologyModel from the OWL file) in the ProjectModel
	 */
	public void loadAssertions() {
		 HashMap<String,List<OWLAxiom>> allAssertionAxiomHash = OWLModel.getallAssertionAxiomHash();
	   	 Iterator itAssertions = allAssertionAxiomHash.entrySet().iterator();
	   	 
	   	 String assertionSubject = "";
	   	 String assertionObject = "";
	   	 String comment = "";
	   	 AcquisitionType acquisitionType = AcquisitionType.DERIVED; //default value - will be overridden
	   	 
	   	while (itAssertions.hasNext()) {
   	    	Map.Entry<String, List<OWLAxiom>> pair = (Map.Entry)itAssertions.next();
	        List<OWLAxiom> assertionAxiomList = pair.getValue();
	        assertionAxiomList.remove(0);
	        for(OWLAxiom ax : assertionAxiomList){
	        	
	         //For an Assertion we can have ANNOTATION_ASSERTION or SUBCLASS_OF axioms
	           if(ax.isOfType(AxiomType.ANNOTATION_ASSERTION)){
	        	   OWLAnnotationAssertionAxiom a2 =  (OWLAnnotationAssertionAxiom) ax;
	        	   
	        	   if(a2.getProperty().isComment()){
	        		   OWLLiteral val = (OWLLiteral)a2.getValue();
	        		   comment = val.getLiteral();
			           System.out.println("COMMENT " + comment);

			        }
	           } else { //SUBCLASS_OF axioms
	        	   
	        		   //An axiom is formed by an OWLObjectProperty,
	        	   	   //an OWLClass - the subject of the Restriction
	        	       //and an OWLClass/OWLIndividual - which is the object of the Restriction
	        	   
	        	   		//Get the property name- which can be either assertionAcquisitionType, assertionSubject or assertionObject
	        		   Set<OWLObjectProperty> properties = ax.getObjectPropertiesInSignature();
		        	   Iterator<OWLObjectProperty> itProp = properties.iterator();
		               OWLObjectProperty restrictionProperty = itProp.next();
		               String propertyName = OWLUtils.getTerminologyId(restrictionProperty.getIRI());
		               
		               
		               //Get OWLClass/OWLIndividual which is the object of the Restriction 
		               Set<OWLClass> classes = ax.getClassesInSignature();
		               Iterator<OWLClass> itClasses = classes.iterator();
		               OWLClass restrictionSubject = null;
		               while(itClasses.hasNext()){
		            	   restrictionSubject =  itClasses.next();
		               }
		              
		               //If the assertion only contains one OWLClass - then we have an OWLIndividual -> we have a assertionAcquisitionType property for the Restriction
		               if(classes.size() == 1){
		            	   Set<OWLNamedIndividual> ind = ax.getIndividualsInSignature();
		            	   if(propertyName.equals(OWLUtils.assertionAcquisitionType)){
		            		   acquisitionType = AcquisitionType.toValue(OWLUtils.getTerminologyId(ind.iterator().next().getIRI()));
		            	   }
		            	  
		               } else { // We have a  assertionSubject or assertionObject property for the Restriction
			            	   if(propertyName.equals(OWLUtils.assertionSubject)){
				            	   assertionSubject = OWLUtils.getTerminologyId(restrictionSubject.getIRI());
				            	   System.out.println("SUBJECT " +assertionSubject);
				               }else{
				            	   assertionObject = OWLUtils.getTerminologyId(restrictionSubject.getIRI());
				            	   System.out.println("OBJECT " +assertionObject);
				               }
		               }

	           }
 	
	        }
	        
	        //Create new ContextAssertionModel
	        ContextAssertionModel cam = new ContextAssertionModel(pair.getKey(),comment, getEntityByName(assertionSubject), getEntityByName(assertionObject), acquisitionType);
	        addAssertion(cam);
	    }

	}

}
