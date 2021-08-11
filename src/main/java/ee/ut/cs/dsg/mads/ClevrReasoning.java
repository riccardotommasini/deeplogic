package ee.ut.cs.dsg.mads;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Part 8: Checking Concistency with HermiT
 * <p>
 * Load the ontology of Part6
 * <p>
 * Add the classes Teacher, and PhDStudent
 * <p>
 * Assert Teacher and Student as Disjoint Classes
 * <p>
 * Make PhDStudent subclass of the intersection between Student and Teacher
 * <p>
 * <p>
 * Is the ontology consistent?
 * <p>
 * The Ontology should be consistent.
 * <p>
 * Now Add an individual to the PhDStudent class (e.g. me)
 * <p>
 * Is the ontology consistent?
 * <p>
 * Did you forget to flush or create another reasoner?
 **/
public class ClevrReasoning {

    public static IRI base = IRI.create("http://example.org#");

    public static void main(String[] args) throws OWLOntologyCreationException {


        File child = new File(args[0]);
        System.out.println(child.getName());
        //for (File child : directoryListing)
        //{
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        OWLReasonerFactory factory = new ReasonerFactory();

        OWLOntology o = manager.loadOntologyFromOntologyDocument(new File(child.getAbsolutePath()));
        //OWLOntology o = manager.loadOntologyFromOntologyDocument(ClevrReasoning.class.getClassLoader().getResourceAsStream(child.getAbsolutePath()));

        OWLReasoner reasoner = factory.createReasoner(o);

        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.CLASS_ASSERTIONS, InferenceType.DIFFERENT_INDIVIDUALS, InferenceType.DISJOINT_CLASSES, InferenceType.OBJECT_PROPERTY_ASSERTIONS, InferenceType.OBJECT_PROPERTY_HIERARCHY);


        System.out.println("Starting to add axiom generators");
        OWLDataFactory datafactory = manager.getOWLDataFactory();
        List<InferredAxiomGenerator<? extends OWLAxiom>> inferredAxioms = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
        inferredAxioms.add(new InferredSubClassAxiomGenerator());
        inferredAxioms.add(new InferredClassAssertionAxiomGenerator());
        inferredAxioms.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
        inferredAxioms.add(new InferredEquivalentClassAxiomGenerator());
        inferredAxioms.add(new InferredPropertyAssertionGenerator());
        inferredAxioms.add(new InferredInverseObjectPropertiesAxiomGenerator());
        inferredAxioms.add(new InferredSubObjectPropertyAxiomGenerator());
        inferredAxioms.add(new InferredDisjointClassesAxiomGenerator());
        inferredAxioms.add(new InferredEquivalentObjectPropertyAxiomGenerator());
        inferredAxioms.add(new InferredInverseObjectPropertiesAxiomGenerator());
        inferredAxioms.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
        inferredAxioms.add(new InferredPropertyAssertionGenerator());
        inferredAxioms.add(new InferredSubClassAxiomGenerator());
        inferredAxioms.add(new InferredSubDataPropertyAxiomGenerator());
        inferredAxioms.add(new InferredSubObjectPropertyAxiomGenerator());
        System.out.println("finished adding axiom generators");



        // for writing inferred axioms to the new ontology
        OWLOntology infOnt = manager.createOntology(IRI.create(o.getOntologyID().getOntologyIRI().get() + "_inferred"));

        // use generator and reasoner to infer some axioms
        System.out.println("Starting to infer");
        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, inferredAxioms);
        //InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner);

        System.out.println("Inference is over");

        System.out.println("Storing the results");
        iog.fillOntology(datafactory, infOnt);

        // save the ontology
        //manager.saveOntology(infOnt, IRI.create("file:///C:/Users/ontologies/NVDB4_test.rdf"));


        try {

            manager.saveOntology(infOnt, new TurtleDocumentFormat(), new FileOutputStream(new File(args[1] + child.getName())));
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //}
    }


}
