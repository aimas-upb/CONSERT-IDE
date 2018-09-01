package org.aimas.consert.ide.model;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.rdf.turtle.renderer.TurtleStorer;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public class ContextEntityModel {
	private String name;
	private String comment;
	private String ID;
//	private File OWLfile;
//	private File TTLfile; 
//	private String baseURI;
	
	public ContextEntityModel() {
	}

	public ContextEntityModel(String name, String comment) {
		setName(name);
		setComment(comment);
	}
	
	public ContextEntityModel(String ID, String name, String comment) {
		setName(name);
		setComment(comment);
		setID(ID);
	}
	
	public void setID(String ID){
		this.ID = ID;
	}
	
	public String getID(){
		return this.ID;
	}
	
//	public File getOWLfile(){
//		return this.OWLfile;
//	}
//	
//	public File getTTLfile(){
//		return this.TTLfile;
//	}
//	
//	public String getbaseURI(){
//		return this.baseURI;
//	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
//	public void setOWLfile(File OWLfile){
//		this.OWLfile = OWLfile;
//	}
//	
//	public void setTTLfile(File TTLfile){
//		this.TTLfile = TTLfile;
//	}
//	
//	public void setbaseURI(String baseURI){
//		this.baseURI = baseURI;
//	}

	@Override
	public String toString() {
		return "{\"name\":\"" + getName() + "\",\"comment\":\"" + getComment() + "\"}";
	}
	
	
	
	
	public void saveEntityOnDisk() throws OWLOntologyStorageException, OWLOntologyCreationException {
//		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//        IRI ontologyIRI = IRI.create(baseURI);
//
//		// OWLOntology ontology = manager.createOntology(ontologyIRI);
//		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(OWLfile);
//        OWLDataFactory df = manager.getOWLDataFactory();
//        
//        
//      //Delete any comment annotations of existing ContextEntity before saving new data
//        OWLClass toBeRemoved = manager.getOWLDataFactory().getOWLClass(IRI.create(baseURI + "#" + getName()));
//        Set<OWLAxiom> axiomsToRemove = new HashSet<OWLAxiom>();
//        for (OWLAnnotation annotation : EntitySearcher.getAnnotations(toBeRemoved.getIRI(), ontology)) {
//      	  if (annotation.getValue() instanceof OWLLiteral) {
//      	    OWLLiteral val = (OWLLiteral) annotation.getValue();
//      	    IRI propIri = annotation.getProperty().getIRI();
//      	    String fragment = propIri.getFragment();
//      	    
//      	    if (fragment.equals("comment")) {
//      	    	OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(toBeRemoved.getIRI(), annotation);
//      	    	axiomsToRemove.add(ax);
//      	    }
//      	  }
//        }
//        
//        System.out.println("Before: " + ontology.getAxiomCount());
//        manager.removeAxioms(ontology, axiomsToRemove);
//        System.out.println("After: " + ontology.getAxiomCount());
//      	    
//      	    
//
//        DefaultPrefixManager pm = new DefaultPrefixManager();
//		pm.setDefaultPrefix(ontologyIRI + "#");
//		pm.setPrefix("core:", "http://pervasive.semanticweb.org/ont/2017/07/consert/core#");
//		 OWLImportsDeclaration coreImport =
//		 factory.getOWLImportsDeclaration(coreIri);
//		 manager.applyChange(new AddImport(ontology, coreImport));
		
//		String entityName = ":" + getName();
//        
//        OWLClass newEntity = df.getOWLClass(entityName, pm);
//        OWLClass contextEntity = df.getOWLClass(IRI.create(ontologyIRI + "ContextEntity"));
//        
////		OWLDeclarationAxiom declarationAxiom = df.getOWLDeclarationAxiom(newEntity);
////        manager.addAxiom(ontology, declarationAxiom);
//        
//        manager.addAxiom(ontology, df.getOWLSubClassOfAxiom(newEntity, contextEntity));
//        
//        
//        OWLAnnotation commentAnnotation = df.getOWLAnnotation(
//        										df.getRDFSComment(),
//        										df.getOWLLiteral(this.getComment()));
//        
//        OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(newEntity.getIRI(), commentAnnotation);
//  
//        manager.applyChange(new AddAxiom(ontology, ax));
//        
//        OWLAnnotation labelAnnotation = df.getOWLAnnotation(
//        									df.getRDFSLabel(),
//        									df.getOWLLiteral(this.getName()));
//        
//        OWLAxiom ax2 = df.getOWLAnnotationAssertionAxiom(newEntity.getIRI(), labelAnnotation);
//
//        manager.applyChange(new AddAxiom(ontology, ax2));
//        manager.saveOntology(ontology, IRI.create(OWLfile.toURI()));
//        
//        TurtleStorer storer = new TurtleStorer();
//        storer.storeOntology(ontology, IRI.create(TTLfile.toURI()), new TurtleDocumentFormat());
	}
}
