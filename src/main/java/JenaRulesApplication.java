import openllet.jena.PelletReasonerFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.exceptions.SWRLRuleException;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;

import java.io.*;
import java.net.URL;
import java.util.Iterator;

class SWRLEngine {

    public void SWRLtoModel() throws OWLOntologyCreationException, SWRLParseException, SWRLBuiltInException,
            OWLOntologyStorageException, IOException {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        URL url = getClass().getResource("people.rdf");

        OWLOntology myOnt = manager.loadOntologyFromOntologyDocument(new File(url.getPath()));


        SWRLRuleEngine swrlRuleEngine = SWRLAPIFactory.createSWRLRuleEngine(myOnt);

        swrlRuleEngine.createSWRLRule("rule1",
                " hasAge(?p, ?age)Person(?p)swrlb:greaterThan(?age, 18) -> Adult(?p)");

        SWRLRule adultRule= swrlRuleEngine.getSWRLRule("rule1").get().getAxiomWithoutAnnotations();

        manager.applyChange(new AddAxiom(myOnt, adultRule ));

        manager.saveOntology(myOnt);
        // manager.saveOntology(myOnt, out);

        //out.close();
    }
}

public class JenaRulesApplication {


    public static void main(String[] args) {

       String NS="http://example.org/example#";
        // create an empty ontology model using Pellet spec
        final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
        // read the file
        model.read( "people.rdf" );

        model.prepare();

        // Grab a resource and and property, and then set the property on that individual
        final Resource Adult = ResourceFactory.createResource(NS+"Adult");
        final Resource Person = ResourceFactory.createResource(NS+"Person");
        final Property hasAge = ResourceFactory.createProperty(NS+"hasAge");

        final Resource res = model.createResource(NS+"individual_1", Person);
        res.addLiteral(hasAge, 19);

        assert(res.hasProperty(RDF.type, Adult));

        for (Iterator i = model.listStatements(); i.hasNext();){
            Statement s = (Statement) i.next();
            System.out.println(s);
        }

        //Now we are going to insert teh following rule into the model:
        // hasAge(?p, ?age), Person(?p), swrlb:greaterThan(?age, 18) -> Adult(?p)

        try {

            SWRLEngine myswrlEngine = new SWRLEngine();

            myswrlEngine.SWRLtoModel();

            final OntModel model2 = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
            // read the file
            model2.read( "people.rdf" );

            model2.prepare();

            final Resource res2 = model2.createResource(NS+"individual_1", Person);
            res.addLiteral(hasAge, 19);

            assert(res.hasProperty(RDF.type, Adult));

            for (Iterator i = model2.listStatements(); i.hasNext();){
                Statement s = (Statement) i.next();
                System.out.println(s);
            }

        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (SWRLParseException e) {
            e.printStackTrace();
        } catch (SWRLRuleException e) {
            e.printStackTrace();
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        } catch (SWRLBuiltInException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
