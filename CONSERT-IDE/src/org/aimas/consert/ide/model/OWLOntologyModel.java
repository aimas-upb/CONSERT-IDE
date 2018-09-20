package org.aimas.consert.ide.model;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.rdf.turtle.renderer.TurtleStorer;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

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
    
    public HashMap<String,List<OWLAxiom>> getallEntityAxiomHash() {
    	return allEntityAxiomHash;
    }
    
    public HashMap<String,List<OWLAxiom>> getallAssertionAxiomHash() {
    	return allAssertionAxiomHash;
    }
    
    
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

		try {
			ontology = manager.loadOntologyFromOntologyDocument(OWLfile);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		loadEntities();
		loadAssertions();
		
	
    }
    
    public void loadEntities() {
    	OWLClass entityClass = df.getOWLClass(OWLUtils.iricontextEntity);
    	
    	Set<OWLSubClassOfAxiom> axiomsSubClassOfContextEntity = ontology.getSubClassAxiomsForSuperClass(entityClass);
		
		for(OWLSubClassOfAxiom entityAxiom1 : axiomsSubClassOfContextEntity){
			//Find the entity name
			OWLClass ourEntityClass = entityAxiom1.getSubClass().asOWLClass();
			String entityName = ourEntityClass.getIRI().getShortForm();
			
			List<OWLAxiom> entityAxiomList = new ArrayList<>();
			
			entityAxiomList.add(entityAxiom1);
    		
    		Collection<OWLAnnotation> annotations = EntitySearcher.getAnnotations(ourEntityClass.getIRI(), ontology);
    		for (OWLAnnotation annotation : annotations) 
    		{
    			OWLLiteral val = (OWLLiteral) annotation.getValue();
    			String name = OWLUtils.getName(annotation);
//    		    System.out.println("\nannotation property->value: "+annotation.getProperty()+" -> "+ val.getLiteral());
    		    
    		    
    		    if (name.equals(OWLUtils.label)) {
    		    	OWLAnnotation labelAnnotation = df.getOWLAnnotation(
    						df.getRDFSLabel(),
    						val);

    		        OWLAxiom entityAxiom2 = df.getOWLAnnotationAssertionAxiom(ourEntityClass.getIRI(), labelAnnotation);
    		        entityAxiomList.add(entityAxiom2);
    		    }
    		    
    		    if (name.equals(OWLUtils.comment)) {
    		    	OWLAnnotation commentAnnotation = df.getOWLAnnotation(
    						df.getRDFSComment(),
    						val);

    		        OWLAxiom entityAxiom3 = df.getOWLAnnotationAssertionAxiom(ourEntityClass.getIRI(), commentAnnotation);
    		        entityAxiomList.add(entityAxiom3);
    		    }

    		}

    		allEntityAxiomHash.put(entityName, entityAxiomList);
 		   
		}
    }
    
    public void loadAssertions() {
    	 OWLClass contextAssertion = df.getOWLClass(OWLUtils.iriBinaryContextAssertion); 
    	 Set<OWLSubClassOfAxiom> axiomsSubClassOfContextAssertion = ontology.getSubClassAxiomsForSuperClass(contextAssertion);
 		 System.out.println(axiomsSubClassOfContextAssertion);
 		 
 		for(OWLSubClassOfAxiom assertionAxiom1 : axiomsSubClassOfContextAssertion){
			//Find the entity name
			OWLClass ourAssertionClass = assertionAxiom1.getSubClass().asOWLClass();
			String assertionName = ourAssertionClass.getIRI().getShortForm();
			
			List<OWLAxiom> assertionAxiomList = new ArrayList<>();
			
			assertionAxiomList.add(assertionAxiom1);
    		
    		Collection<OWLAnnotation> annotations = EntitySearcher.getAnnotations(ourAssertionClass.getIRI(), ontology);
    		for (OWLAnnotation annotation : annotations) 
    		{
    			OWLLiteral val = (OWLLiteral) annotation.getValue();
    			String name = OWLUtils.getName(annotation);
//    		    System.out.println("\nannotation property->value: "+annotation.getProperty()+" -> "+ val.getLiteral());
    			
    			if (name.equals(OWLUtils.comment)) {
    		    	OWLAnnotation commentAnnotation = df.getOWLAnnotation(
    						df.getRDFSComment(),
    						val);

    		        OWLAxiom assertionAxiom2 = df.getOWLAnnotationAssertionAxiom(ourAssertionClass.getIRI(), commentAnnotation);
    		        assertionAxiomList.add(assertionAxiom2);
    		    } 
    		}
    		
    		 Collection<OWLClassExpression> axiomSuperclasses = EntitySearcher.getSuperClasses(ourAssertionClass, ontology);
    		 
    		 for (OWLClassExpression superClass: axiomSuperclasses) {
    			 if(superClass.getClassExpressionType()==ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
    	                System.out.println("\t\t\tDATA_ALL_VALUES_FROM:" + superClass.getNNF());
    	                Set<OWLClass> classes = superClass.getNNF().getClassesInSignature();
    	                Set<OWLObjectProperty> properties = superClass.getNNF().getObjectPropertiesInSignature();
    	                
    	                Iterator<OWLClass> itClasses = classes.iterator();
    	                Iterator<OWLObjectProperty> itProp = properties.iterator();
    	                
    	                OWLObjectProperty restrictionProperty = itProp.next();
    	                OWLClass restrictionSubject =  itClasses.next();
    	                OWLClassExpression assertionSubject = df.getOWLObjectAllValuesFrom(restrictionProperty, restrictionSubject);
    	                OWLAxiom assertionAxiom3 = df.getOWLSubClassOfAxiom(ourAssertionClass, assertionSubject);
    	                assertionAxiomList.add(assertionAxiom3);
    	         }
    			 
    			 if(superClass.getClassExpressionType()==ClassExpressionType.OBJECT_HAS_VALUE) {
 	                System.out.println("\t\t\tOBJECT_HAS_VALUE:" + superClass.getNNF());
 	                Set<OWLNamedIndividual> classes = superClass.getNNF().getIndividualsInSignature();
	                Set<OWLObjectProperty> properties = superClass.getNNF().getObjectPropertiesInSignature();
	                
	                Iterator<OWLNamedIndividual> itClasses = classes.iterator();
	                Iterator<OWLObjectProperty> itProp = properties.iterator();
	                
	                OWLObjectProperty restrictionProperty = itProp.next();
	                OWLNamedIndividual restrictionSubject =  itClasses.next();
	                OWLClassExpression assertionSubject = df.getOWLObjectHasValue(restrictionProperty, restrictionSubject);
	        		OWLAxiom assertionAxiom4 = df.getOWLSubClassOfAxiom(ourAssertionClass, assertionSubject);
	        		assertionAxiomList.add(assertionAxiom4);
    			 }
    			 
    		 }
    		
    		 allAssertionAxiomHash.put(assertionName, assertionAxiomList);
 		}

    }
    
    
    public void syncOWLModelWithProjectModel(
    		List<ContextEntityModel> entities, 
    		List<ContextAssertionModel> assertions, 
    		List<ContextAnnotationModel> annotations, 
    		List<EntityDescriptionModel> entityDescriptions) {
    	
    	for (ContextEntityModel entity : entities) {
    		updateEntitiesOWLModel(entity);
    	}
    	
    	for (ContextAssertionModel assertion : assertions) {
    		updateAssertionsOWLModel(assertion);
    	}
    	
    	for (ContextAnnotationModel annotation : annotations) {
    		updateAnnotationsOWLModel(annotation);
    	}
    	
    	for (EntityDescriptionModel entityDescription : entityDescriptions) {
    		updateEntityDescriptionsOWLModel(entityDescription);
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
    
    public void updateAnnotationsOWLModel(ContextAnnotationModel cam) {
    	//The OWL Model already contains the Annotation
    	//We must update the axioms if necessary
    	if (allAnnotationAxiomLHash.containsKey(cam.getName())) {
    		List<OWLAxiom> annotationAxiomList = allAnnotationAxiomLHash.get(cam.getName());
    		
    		//TODO
    		
    	} else {
    		//The OWL Model does not contains the Assertion
        	//We must create another entry in the hash and add the list of axioms to the hash
    		List<OWLAxiom> annotationAxiomList = createAnnotationOWLModel(cam);
    		allAnnotationAxiomLHash.put(cam.getName(), annotationAxiomList);
    	}
    }
    
    public void updateEntityDescriptionsOWLModel(EntityDescriptionModel edm) {
    	//The OWL Model already contains the EntityDescription
    	//We must update the axioms if necessary
    	if (allEntityDescriptionAxiomHash.containsKey(edm.getName())) {
    		List<OWLAxiom> annotationAxiomList = allEntityDescriptionAxiomHash.get(edm.getName());
    		
    		//TODO
    		
    	} else {
    		//The OWL Model does not contains the Assertion
        	//We must create another entry in the hash and add the list of axioms to the hash
    		List<OWLAxiom> annotationAxiomList = createEntityDescriptionOWLModel(edm);
    		allEntityDescriptionAxiomHash.put(edm.getName(), annotationAxiomList);
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
//    	OWLImportsDeclaration importDeclaration=manager.getOWLDataFactory().getOWLImportsDeclaration(IRI.create(OWLUtils.coreURI));
// 		manager.applyChange(new AddImport(ontology, importDeclaration));
 		
 	
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
      
        OWLClass contextAssertion = df.getOWLClass(OWLUtils.iriBinaryContextAssertion); 
        OWLAxiom a1 = df.getOWLSubClassOfAxiom(newAssertion, contextAssertion);
        assertionAxiomList.add(a1);
        
        OWLAnnotation commentAnnotation = df.getOWLAnnotation(
				df.getRDFSComment(),
				df.getOWLLiteral(cam.getComment()));

		OWLAxiom a2 = df.getOWLAnnotationAssertionAxiom(newAssertion.getIRI(), commentAnnotation);
		assertionAxiomList.add(a2);
		
		OWLObjectProperty hasObjectEntity = df.getOWLObjectProperty("core:assertionObject", pm);
		OWLClass entityObject = df.getOWLClass(cam.getObjectEntity().getName(), pm);
        // Now create a restriction to describe the class of assertions that
        // have an Object Entity of type entityObject
        OWLClassExpression assertionObject = df.getOWLObjectAllValuesFrom(hasObjectEntity, entityObject);
	
		OWLAxiom a3 = df.getOWLSubClassOfAxiom(newAssertion, assertionObject);
		assertionAxiomList.add(a3);
       
		OWLObjectProperty hasSubjectEntity = df.getOWLObjectProperty("core:assertionSubject", pm);
		OWLClass entitySubject = df.getOWLClass(cam.getSubjectEntity().getName(), pm);
        // Now create a restriction to describe the class of assertions that
        // have an Object Entity of type entitySubject
        OWLClassExpression assertionSubject = df.getOWLObjectAllValuesFrom(hasSubjectEntity, entitySubject);
		OWLAxiom a4 = df.getOWLSubClassOfAxiom(newAssertion, assertionSubject);
		assertionAxiomList.add(a4);
		
		OWLObjectProperty hasAssertionAcquisitionType  = df.getOWLObjectProperty("core:assertionAcquisitionType", pm);
		OWLIndividual acquisitionType = df.getOWLNamedIndividual(cam.getAcquisitionType().toString(), pm);
        // Now create a restriction for assertionAcquisitionType
        OWLClassExpression assertionAcquisitionType = df.getOWLObjectHasValue(hasAssertionAcquisitionType, acquisitionType);
		OWLAxiom a5 = df.getOWLSubClassOfAxiom(newAssertion, assertionAcquisitionType);
		assertionAxiomList.add(a5);
		
		return assertionAxiomList;
	}
    
    public List<OWLAxiom> createEntityDescriptionOWLModel(EntityDescriptionModel edm) {
    	List<OWLAxiom> entityDescriptionAxiomList = new ArrayList<>();
		String entityDescriptionName = ":" + edm.getName();
		
		OWLClass newEntityDescription = df.getOWLClass(entityDescriptionName, pm);
		
		OWLClass entityDescription = df.getOWLClass(OWLUtils.iriEntityDescription); 
        OWLAxiom a1 = df.getOWLSubClassOfAxiom(newEntityDescription, entityDescription);
        entityDescriptionAxiomList.add(a1);
        
        OWLObjectProperty hasEntityDescriptionObject = df.getOWLObjectProperty("core:entityDescriptionObject", pm);
		OWLIndividual entityObject = df.getOWLNamedIndividual(edm.getObjectEntity().getName(), pm);
        // Now create a restriction to describe the class of assertions that
        // have an Object Entity of type entityObject
        OWLClassExpression assertionObject = df.getOWLObjectHasValue(hasEntityDescriptionObject, entityObject);
	
		OWLAxiom a2 = df.getOWLSubClassOfAxiom(newEntityDescription, assertionObject);
		entityDescriptionAxiomList.add(a2);
       
		OWLObjectProperty hasEntityDescriptionSubject = df.getOWLObjectProperty("core:entityDescriptionSubject", pm);
		OWLClass entitySubject = df.getOWLClass(edm.getSubjectEntity().getName(), pm);
        // Now create a restriction to describe the class of assertions that
        // have an Object Entity of type entitySubject
        OWLClassExpression assertionSubject = df.getOWLObjectAllValuesFrom(hasEntityDescriptionSubject, entitySubject.asOWLClass());
		OWLAxiom a3 = df.getOWLSubClassOfAxiom(newEntityDescription, assertionSubject);
		entityDescriptionAxiomList.add(a3);
        
		
		return entityDescriptionAxiomList;
    }
    
    public List<OWLAxiom> createAnnotationOWLModel(ContextAnnotationModel cam) {
    	List<OWLAxiom> annotationAxiomList = new ArrayList<>();
		String annotationName = ":" + cam.getName();
		
		OWLClass newAnnotation = df.getOWLClass(annotationName, pm);
		
		OWLClass contextAnnotation = df.getOWLClass(OWLUtils.iriContextAnnotation); 
        OWLAxiom a1 = df.getOWLSubClassOfAxiom(newAnnotation, contextAnnotation);
        annotationAxiomList.add(a1);
		
		OWLObjectProperty hasAnnotationType = df.getOWLObjectProperty("core:annotationType", pm);
        // Now create a restriction to describe the class of assertions that
        // have an Object Entity of type entitySubject
		OWLIndividual annotationType = df.getOWLNamedIndividual(cam.getAnnotationType().toString(), pm);
        OWLClassExpression assertionSubject = df.getOWLObjectHasValue(hasAnnotationType, annotationType);
		OWLAxiom a2 = df.getOWLSubClassOfAxiom(newAnnotation, assertionSubject);
		annotationAxiomList.add(a2);
		
		OWLObjectProperty hasAnnotationCategory  = df.getOWLObjectProperty("core:annotationCategory", pm);
        // Now create a restriction for assertionAcquisitionType
		OWLIndividual acquisitionType = df.getOWLNamedIndividual(cam.getAnnotationCategory().toString(), pm);
        OWLClassExpression assertionAcquisitionType = df.getOWLObjectHasValue(hasAnnotationCategory, acquisitionType);
		OWLAxiom a3 = df.getOWLSubClassOfAxiom(newAnnotation, assertionAcquisitionType);
		annotationAxiomList.add(a3);
		
		return annotationAxiomList;
    }
	

}
