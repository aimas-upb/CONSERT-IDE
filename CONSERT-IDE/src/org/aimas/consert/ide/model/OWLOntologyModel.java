package org.aimas.consert.ide.model;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aimas.consert.ide.util.OWLUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.rdf.turtle.renderer.TurtleStorer;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.google.common.io.Files;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * 
 * @author emili
 * https://gist.github.com/stain/4118712
 * https://tutorial-academy.com/owlapi-5-read-class-restriction-axiom-visitor/
 * https://stackoverflow.com/questions/33964019/owlapi-4-1x-restrictions-example
 * https://github.com/owlcs/owlapi/blob/version3/contract/src/test/java/org/coode/owlapi/examples/Examples.java     -> USED
 * https://sourceforge.net/p/owlapi/mailman/message/30802758/   -> IMPORTS
 * https://stackoverflow.com/questions/46619937/get-subclasses-of-a-class-owlapi/46620428  -> LOAD
 */
public class OWLOntologyModel {
	private OWLOntologyManager manager;
	private IRI ontologyIRI;
	private OWLOntology ontology;
    private OWLDataFactory df;
    private DefaultPrefixManager pm;
    private TurtleStorer storer;
    private File OWLfile;
    private File TTLfile;
    private String baseURI;
    
    private HashMap<String,List<OWLAxiom>> allEntityAxiomHash;
    private HashMap<String,List<OWLAxiom>> allAssertionAxiomHash; 
    private HashMap<String,List<OWLAxiom>> allEntityDescriptionAxiomHash;
    private HashMap<String,List<OWLAxiom>> allAnnotationAxiomLHash;
    
    
    public OWLOntologyModel(File OWLfile, File TTLfile, String baseURI){
    	this.OWLfile = OWLfile;
    	this.TTLfile = TTLfile;
    	this.baseURI = baseURI;
    	manager = OWLManager.createOWLOntologyManager();
        ontologyIRI = IRI.create(baseURI);
        df = manager.getOWLDataFactory();

    	pm = new DefaultPrefixManager();
		pm.setDefaultPrefix(ontologyIRI.toString());
		pm.setPrefix("core:", OWLUtils.coreURI);

		allEntityAxiomHash = new  HashMap<String,List<OWLAxiom>>();
		allAssertionAxiomHash = new HashMap<String,List<OWLAxiom>>();
		allEntityDescriptionAxiomHash = new HashMap<String,List<OWLAxiom>>();
		allAnnotationAxiomLHash = new HashMap<String,List<OWLAxiom>>();
    }
    
    
    public void loadOWLOntologyModelFromFile() {
    	//TODO: implement
	
    }
    
    
    //TODO Add support for all types of context elements
    public void syncOWLModelWithProjectModel(List<ContextEntityModel> entities, List<ContextAssertionModel> assertions) {
    	for (ContextEntityModel entity : entities) {
    		updateEntitiesOWLModel(entity);
    	}
    	
    	for (ContextAssertionModel assertion : assertions) {
    		updateAssertionsOWLModel(assertion);
    	}
    	
    }
    
    
    public void updateEntitiesOWLModel(ContextEntityModel cem) {
    	//The OWL Model already contains the Entity
    	//We must update the axioms if necessary
    	if (allEntityAxiomHash.containsKey(cem.getName())) {
    		List<OWLAxiom> entityAxiomList = allEntityAxiomHash.get(cem.getName());
    		//Get the axiom regarding the comment
    		OWLAxiom a3 = entityAxiomList.get(2);
    		
    		Set<OWLAnnotation> commentAnnotations = a3.getAnnotations();
    		//only one comment
    		for(OWLAnnotation commentAnnotation : commentAnnotations){
    			OWLLiteral val = (OWLLiteral) commentAnnotation.getValue();
    			if(!val.getLiteral().equals(cem.getComment())){
    				//Create new axiom to replace a3
    				OWLAnnotation newCommentAnnotation = df.getOWLAnnotation(
    						df.getRDFSComment(),
    						df.getOWLLiteral(cem.getComment()));

    				OWLAxiom a3New = df.getOWLAnnotationAssertionAxiom(ontologyIRI, newCommentAnnotation);
    				entityAxiomList.remove(a3);
    				entityAxiomList.add(a3New);
    				
    				allEntityAxiomHash.replace(cem.getName(), entityAxiomList);
    			}
    		}
    		
    	} else {
    		//The OWL Model does not contains the Entity
        	//We must create another entry in the hash and add the list of axioms to the hash
    		List<OWLAxiom> entityAxiomList = createEntityOWLModel(cem);
    		allEntityAxiomHash.put(cem.getName(), entityAxiomList);
    	}
    	
    }
    
    public void updateAssertionsOWLModel(ContextAssertionModel cam) {
    	//The OWL Model already contains the Assertion
    	//We must update the axioms if necessary
    	if (allAssertionAxiomHash.containsKey(cam.getName())) {
    		List<OWLAxiom> assertionAxiomList = allAssertionAxiomHash.get(cam.getName());
    		
    		//TODO
    		
    	} else {
    		//The OWL Model does not contains the Assertion
        	//We must create another entry in the hash and add the list of axioms to the hash
    		List<OWLAxiom> assertionAxiomList = createAssertionOWLModel(cam);
    		allAssertionAxiomHash.put(cam.getName(), assertionAxiomList);
    	}
    }
    
    /**
     * Method used to save OWL model on disk
     */
	
    public void saveModelOnDisk() {
    	 try {
			ontology = manager.createOntology(ontologyIRI);
		} catch (OWLOntologyCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Could not create ontology when saving to disk");
		}
    	 /**
    	  * Add import declaration
    	  */
    	OWLImportsDeclaration importDeclaration=manager.getOWLDataFactory().getOWLImportsDeclaration(IRI.create(OWLUtils.coreURI));
 		manager.applyChange(new AddImport(ontology, importDeclaration));
 		
 	
    	 /**
    	  * Add Entities
    	  */	
    	 
    	 Iterator itEntities = allEntityAxiomHash.entrySet().iterator();
    	    while (itEntities.hasNext()) {
    	        Map.Entry<String, List<OWLAxiom>> pair = (Map.Entry)itEntities.next();
    	        
    	        List<OWLAxiom> entityAxiomList = pair.getValue();
    	        for(OWLAxiom entityAxiom : entityAxiomList) {
    	    		 manager.applyChange(new AddAxiom(ontology, entityAxiom));
    	    	 }
    	    }
    	 
    	 /**
    	  * Add Assertions
    	  */
    	 
    	 
    	 Iterator itAssertions = allAssertionAxiomHash.entrySet().iterator();
	 	    while (itAssertions.hasNext()) {
	 	        Map.Entry<String, List<OWLAxiom>> pair = (Map.Entry)itAssertions.next();
	 	        List<OWLAxiom> assertionAxiomList = pair.getValue();
	 	        for(OWLAxiom assertionAxiom : assertionAxiomList) {
	 	    		 manager.applyChange(new AddAxiom(ontology, assertionAxiom));
	 	    	}
	 	    }
    	 
    	 /**
    	  * Add Entity Description
    	  */
    	 
    	 Iterator itEntityDescription = allEntityDescriptionAxiomHash.entrySet().iterator();
	 	    while (itEntityDescription.hasNext()) {
	 	        Map.Entry<String, List<OWLAxiom>> pair = (Map.Entry)itEntityDescription.next();
	 	        
	 	        List<OWLAxiom> entityDescriptionAxiomList = pair.getValue();
	 	        for(OWLAxiom entityDescriptionAxiom : entityDescriptionAxiomList) {
	 	    		 manager.applyChange(new AddAxiom(ontology, entityDescriptionAxiom));
	 	    	 }
	 	    }
    	 
    	 /**
    	  * Add Annotations
    	  */
    	 
    	 Iterator itAnnotations = allAnnotationAxiomLHash.entrySet().iterator();
	 	    while (itAnnotations.hasNext()) {
	 	        Map.Entry<String, List<OWLAxiom>> pair = (Map.Entry)itAnnotations.next();
	 	        
	 	        List<OWLAxiom> annotationAxiomList = pair.getValue();
	 	        for(OWLAxiom annotationAxiom : annotationAxiomList) {
	 	    		 manager.applyChange(new AddAxiom(ontology, annotationAxiom));
	 	    	 }
	 	    }
	 	    
	 	    OWLDocumentFormat format = manager.getOntologyFormat(ontology);
	 		// save the merged ontology in RDF/XML format
	 		RDFXMLDocumentFormat newFormat = new RDFXMLDocumentFormat();

	 		// will copy the prefixes over so that we have nicely abbreviated IRIs
	 		// in the new ontology document
	 		if (format.isPrefixOWLOntologyFormat()) {
	 		    newFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
	 		}
	 		
    	
    	 
    	 try {
			manager.saveOntology(ontology, newFormat, IRI.create(OWLfile.toURI()));
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Could not save OWL ontology to disk");
		}
         
         storer = new TurtleStorer();
         
         try {
			storer.storeOntology(ontology, IRI.create(TTLfile.toURI()), new TurtleDocumentFormat());
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Could not save TTL ontology to disk");
		}
    }
    
    //******************************************************HELPER METHODS TO CREATE CONTEXT ELEMENTS OWL MODEL******************************************************
    
    public List<OWLAxiom> createEntityOWLModel(ContextEntityModel cem) {
    	List<OWLAxiom> entityAxiomList = new ArrayList<>();
		String entityName = ":" + cem.getName();
        
        OWLClass newEntity = df.getOWLClass(entityName, pm);
     
        OWLClass contextEntity = df.getOWLClass(OWLUtils.iricontextEntity); 
        OWLAxiom a1 = df.getOWLSubClassOfAxiom(newEntity, contextEntity);
        entityAxiomList.add(a1);
       
        OWLAnnotation labelAnnotation = df.getOWLAnnotation(
				df.getRDFSLabel(),
				df.getOWLLiteral(cem.getName()));

        OWLAxiom a2 = df.getOWLAnnotationAssertionAxiom(newEntity.getIRI(), labelAnnotation);
        entityAxiomList.add(a2);
        
        OWLAnnotation commentAnnotation = df.getOWLAnnotation(
				df.getRDFSComment(),
				df.getOWLLiteral(cem.getComment()));

		OWLAxiom a3 = df.getOWLAnnotationAssertionAxiom(newEntity.getIRI(), commentAnnotation);
		entityAxiomList.add(a3);

		
		return entityAxiomList;
	}
    
    public List<OWLAxiom> createAssertionOWLModel(ContextAssertionModel cam) {
    	List<OWLAxiom> assertionAxiomList = new ArrayList<>();
		String assertionName = ":" + cam.getName();
        
        OWLClass newAssertion = df.getOWLClass(assertionName, pm);
      
        OWLClass contextAssertion = df.getOWLClass(OWLUtils.iricontextAssertion); 
        OWLAxiom a1 = df.getOWLSubClassOfAxiom(newAssertion, contextAssertion);
        assertionAxiomList.add(a1);
        
        OWLAnnotation commentAnnotation = df.getOWLAnnotation(
				df.getRDFSComment(),
				df.getOWLLiteral(cam.getComment()));

		OWLAxiom a2 = df.getOWLAnnotationAssertionAxiom(newAssertion.getIRI(), commentAnnotation);
		assertionAxiomList.add(a2);
		
		OWLObjectProperty hasObjectEntity = df.getOWLObjectProperty(":assertionObject", pm);
		OWLClass entityObject = df.getOWLClass(cam.getObjectEntity().getName(), pm);
        // Now create a restriction to describe the class of assertions that
        // have an Object Entity of type entityObject
        OWLClassExpression assertionObject = df.getOWLObjectSomeValuesFrom(hasObjectEntity, entityObject);
	
		OWLAxiom a3 = df.getOWLSubClassOfAxiom(newAssertion, assertionObject);
		assertionAxiomList.add(a3);
       
		OWLObjectProperty hasSubjectEntity = df.getOWLObjectProperty(":assertionSubject", pm);
		OWLClass entitySubject = df.getOWLClass(cam.getSubjectEntity().getName(), pm);
        // Now create a restriction to describe the class of assertions that
        // have an Object Entity of type entitySubject
        OWLClassExpression assertionSubject = df.getOWLObjectSomeValuesFrom(hasSubjectEntity, entitySubject);
		OWLAxiom a4 = df.getOWLSubClassOfAxiom(newAssertion, assertionSubject);
		assertionAxiomList.add(a4);
		
		OWLObjectProperty hasAssertionAcquisitionType  = df.getOWLObjectProperty(":assertionAcquisitionType", pm);
		OWLClass acquisitionType = df.getOWLClass(cam.getAcquisitionType().toString(), pm);
        // Now create a restriction for assertionAcquisitionType
        OWLClassExpression assertionAcquisitionType = df.getOWLObjectSomeValuesFrom(hasAssertionAcquisitionType, acquisitionType);
		OWLAxiom a5 = df.getOWLSubClassOfAxiom(newAssertion, assertionAcquisitionType);
		assertionAxiomList.add(a5);
		
		return assertionAxiomList;
	}
	

}
