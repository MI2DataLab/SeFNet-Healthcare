import org.openrdf.model.URI;
import slib.graph.io.conf.GDataConf;
import slib.graph.io.loader.GraphLoaderGeneric;
import slib.graph.io.loader.bio.snomedct.GraphLoaderSnomedCT_RF2;
import slib.graph.io.util.GFormat;
import slib.graph.model.graph.G;
import slib.graph.model.impl.graph.memory.GraphMemory;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;
import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Exception;


public class SingleTermSimilarity {

    static URIFactory factory = URIFactoryMemory.getSingleton();
    static URI snomedctURI;
    static SM_Engine engine;
    static SMconf sim;

    public static void main(String[] args) throws SLIB_Exception {

        // this needs to be change accordingly to the available SNOMED-CT version
        String SNOMED_VERSION = "20220301";
        String SNOMED_DIR =  "../SnomedCT_USEditionRF2_PRODUCTION_" + SNOMED_VERSION + "T120000Z/Full/Terminology/";
        String SNOMED_CONCEPT = SNOMED_DIR + "sct2_Concept_Full_US1000124_" + SNOMED_VERSION + ".txt";
        String SNOMED_RELATIONSHIPS = SNOMED_DIR + "sct2_Relationship_Full_US1000124_" + SNOMED_VERSION + ".txt";

        // configure ontology graph
        snomedctURI = factory.getURI("http://snomedct/");
        G g = new GraphMemory(snomedctURI);
        GDataConf conf = new GDataConf(GFormat.SNOMED_CT_RF2);
        conf.addParameter(GraphLoaderSnomedCT_RF2.ARG_CONCEPT_FILE, SNOMED_CONCEPT);
        conf.addParameter(GraphLoaderSnomedCT_RF2.ARG_RELATIONSHIP_FILE, SNOMED_RELATIONSHIPS);

        // populate ontology graph
        GraphLoaderGeneric.populate(conf, g);

        // define similarity
        engine = new SM_Engine(g);
        sim = new SMconf("SIM_PAIRWISE_DAG_NODE_FEATURE_TVERSKY_RATIO_MODEL");
        sim.addParam("alpha", 7.9);
        sim.addParam("beta", 3.7);

        // calculate similarity
        URI uri_1 = factory.getURI(snomedctURI.stringValue() + "118565006");
        URI uri_2 = factory.getURI(snomedctURI.stringValue() + "42798000");
        System.out.println(engine.compare(sim, uri_1, uri_2));

    }

}
