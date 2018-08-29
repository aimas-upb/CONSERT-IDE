package org.aimas.consert.ide.model;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.rdf.turtle.renderer.TurtleStorer;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWLFacet;

public class POC {
	private static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	public static void createAndSaveOntology(IFolder folder)
			throws OWLOntologyCreationException, OWLOntologyStorageException {
		File OWLfile = folder.getFile("consert.owl").getLocation().toFile();

        String base = "http://org.semanticweb.datarangeexample";
        IRI ontologyIRI = IRI.create(base);
		OWLOntology ontology = manager.createOntology(ontologyIRI);
        // Create a document IRI which can be resolved to point to where our
        // ontology will be saved.
        IRI documentIRI = IRI.create(OWLfile);
        System.out.println(OWLfile.getAbsolutePath());
        // Set up a mapping, which maps the ontology to the document IRI
        SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
        manager.getIRIMappers().add(mapper);
       
        // We want to add an axiom to our ontology that states that adults have
        // an age greater than 18. To do this, we will create a restriction
        // along a hasAge property, with a filler that corresponds to the set of
        // integers greater than 18. First get a reference to our hasAge
        // property
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLDataProperty hasAge = factory.getOWLDataProperty(IRI.create(base + "hasAge"));
        OWLDatatype intDatatype = factory.getIntegerOWLDatatype();
        // Create the value "18", which is an int.
        OWLLiteral eighteenConstant = factory.getOWLLiteral(18);
        // Now create our custom datarange, which is int greater than or equal
        // to 18. To do this, we need the minInclusive facet
        OWLFacet facet = OWLFacet.MIN_INCLUSIVE;
        // Create the restricted data range by applying the facet restriction
        // with a value of 18 to int
        OWLDataRange intGreaterThan18 = factory.getOWLDatatypeRestriction(intDatatype, facet, eighteenConstant);
        // Now we can use this in our datatype restriction on hasAge
        OWLClassExpression thingsWithAgeGreaterOrEqualTo18 = factory.getOWLDataSomeValuesFrom(hasAge, intGreaterThan18);
        // Now we want to say all adults have an age that is greater or equal to
        // 18 - i.e. Adult is a subclass of hasAge some int[>= 18] Obtain a
        // reference to the Adult class
        OWLClass adult = factory.getOWLClass(IRI.create(base + "#Adult"));
        // Now make adult a subclass of the things that have an age greater to
        // or equal to 18
        OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(adult, thingsWithAgeGreaterOrEqualTo18);
        // Add our axiom to the ontology
        manager.applyChange(new AddAxiom(ontology, ax));
        manager.saveOntology(ontology, IRI.create(OWLfile.toURI()));
        
		saveOntologyInTurtle(folder, ontology);
	}

	private static void saveOntologyInTurtle(IFolder folder, OWLOntology ontology) throws OWLOntologyStorageException {
		File turtleFile = folder.getFile("consert.ttl").getLocation().toFile();
		TurtleStorer storer = new TurtleStorer();
		storer.storeOntology(ontology, IRI.create(turtleFile.toURI()), new TurtleDocumentFormat());
	}
	
}
