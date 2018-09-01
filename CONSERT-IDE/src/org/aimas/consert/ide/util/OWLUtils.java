package org.aimas.consert.ide.util;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * 
 * @author emili
 * https://github.com/owlcollab/owltools/blob/master/OWLTools-Core/src/main/java/owltools/util/OwlHelper.java
 *
 */

public class OWLUtils {
	
	 
	 public static String coreURI = "http://pervasive.semanticweb.org/ont/2017/07/consert/core/";
	 public static String contextEntity = "ContextEntity";
	 public static String contextAssertion = "ContextAssertion";
	 public static IRI iricontextEntity= IRI.create(OWLUtils.coreURI + OWLUtils.contextEntity);
	 public static IRI iricontextAssertion= IRI.create(OWLUtils.coreURI + OWLUtils.contextAssertion);
	 
	 public static Set<OWLClassExpression> getSuperClasses(OWLClass subCls, OWLOntology ont) {
			Set<OWLClassExpression> result;
			if (subCls != null && ont != null) {
				result = new HashSet<>();
				Set<OWLSubClassOfAxiom> axioms = ont.getSubClassAxiomsForSubClass(subCls);
				for (OWLSubClassOfAxiom axiom : axioms) {
					result.add(axiom.getSuperClass());
				}
			}
			else {
				result = Collections.emptySet();
			}
			return result;
		}
	 
	 public static Set<OWLClassExpression> getSubClasses(OWLClass superCls, OWLOntology ont) {
			Set<OWLClassExpression> result;
			if (superCls != null && ont != null) {
				result = new HashSet<>();
				Set<OWLSubClassOfAxiom> axioms = ont.getSubClassAxiomsForSuperClass(superCls);
				for (OWLSubClassOfAxiom axiom : axioms) {
					result.add(axiom.getSubClass());
				}
			}
			else {
				result = Collections.emptySet();
			}
			return result;
		}
	    
	    public static Set<OWLAnnotation> getAnnotations(OWLEntity e, OWLAnnotationProperty property, OWLOntology ont) {
			Set<OWLAnnotation> annotations;
			if (e != null && property != null && ont != null) {
				annotations = new HashSet<>();
				for (OWLAnnotationAssertionAxiom ax : ont.getAnnotationAssertionAxioms(e.getIRI())) {
					if (property.equals(ax.getProperty())) {
						annotations.add(ax.getAnnotation());
					}
				}
			}
			else {
				annotations = Collections.emptySet();
			}
			return annotations;
		}

}
