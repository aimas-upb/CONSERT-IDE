package org.aimas.consert.ide.model;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.rdf.turtle.renderer.TurtleStorer;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.File;

public class ContextEntityModel {
	private String name;
	private String comment;
	private String ID;
	
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

	public String toString() {
		return "{\"name\":\"" + getName() + "\",\"comment\":\"" + getComment() + "\"}";
	}
	
	public void saveEntityOnDisk(File OWLfile, File TTLfile, String baseURI) throws OWLOntologyStorageException, OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        IRI ontologyIRI = IRI.create(baseURI);
        IRI documentIRI = IRI.create(OWLfile);
        
        OWLOntology ontology = manager.createOntology(ontologyIRI);
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLDataFactory factory = manager.getOWLDataFactory();
        
        IRI coreIri = IRI.create("http://pervasive.semanticweb.org/ont/2017/07/consert/core#");
        DefaultPrefixManager pm = new DefaultPrefixManager();
		pm.setDefaultPrefix(ontologyIRI + "#");
		pm.setPrefix("core:", "http://pervasive.semanticweb.org/ont/2017/07/consert/core#");
		OWLImportsDeclaration coreImport = factory
				.getOWLImportsDeclaration(coreIri);
		manager.applyChange(new AddImport(ontology, coreImport));
        
        OWLClass newEntity = factory.getOWLClass(":Person", pm);
        OWLClass contextEntity = factory.getOWLClass(IRI.create(ontologyIRI + "ContextEntity"));
        
        OWLDeclarationAxiom declarationAxiom = factory
                .getOWLDeclarationAxiom(newEntity);
        manager.addAxiom(ontology, declarationAxiom);
        
        manager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(newEntity, contextEntity));
        
        OWLAnnotation commentAnnotation = df.getOWLAnnotation(
        										df.getRDFSComment(),
        										df.getOWLLiteral(this.getComment()));
        
        OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(newEntity.getIRI(), commentAnnotation);
  
        manager.applyChange(new AddAxiom(ontology, ax));
        
        OWLAnnotation labelAnnotation = df.getOWLAnnotation(
        									df.getRDFSLabel(),
        									df.getOWLLiteral(this.getName()));
        
        OWLAxiom ax2 = df.getOWLAnnotationAssertionAxiom(newEntity.getIRI(), labelAnnotation);

        manager.applyChange(new AddAxiom(ontology, ax2));
        manager.saveOntology(ontology, IRI.create(OWLfile.toURI()));
        
        TurtleStorer storer = new TurtleStorer();
        storer.storeOntology(ontology, IRI.create(TTLfile.toURI()), new TurtleDocumentFormat());
	}
}
