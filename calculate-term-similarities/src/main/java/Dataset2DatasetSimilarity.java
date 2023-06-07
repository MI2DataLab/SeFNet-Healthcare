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
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.ex.SLIB_Exception;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class Dataset2DatasetSimilarity {

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

        // select appropriate datasets and calculate similarity
        Table ds1 = Table.read().csv("../annotations/1_annotation.csv");
        Table ds2 = Table.read().csv("../annotations/2_annotation.csv");
        calcSims(ds1, ds2, "ds1_ds2.csv");

    }

    static void calcSims(Table tableA, Table tableB, String fileName) throws SLIB_Ex_Critic {

        Table sim_tables = Table.create("Table of similarities");
        sim_tables.addColumns(tableA.column("column_name"));
        double[][] sims = new double[tableB.column("term_id").size()][tableA.column("term_id").size()];

        StringColumn idxsA = StringColumn.create("scA");
        StringColumn idxsB = StringColumn.create("scB");

        for(int i=0;i<tableA.column("term_id").size();++i){
            if (tableA.column("term_id").get(i) instanceof String)
                idxsA.append((String) tableA.column("term_id").get(i));
            else{
                idxsA.append(Long.toString(((Number) tableA.column("term_id").get(i)).longValue()));
            }
        }

        for(int i=0;i<tableB.column("term_id").size();++i){
            if (tableB.column("term_id").get(i) instanceof String)
                idxsB.append((String) tableB.column("term_id").get(i));
            else{
                idxsB.append(Long.toString(((Number) tableB.column("term_id").get(i)).longValue()));
            }
        }

        for(int i=0;i<idxsB.size();++i){
            for (int j=0;j<idxsA.size();++j){
                if (idxsB.get(i).equals("None") || idxsA.get(j).equals("None")){
                    sims[i][j] = 0;
                } else{
                    URI uri_1 = factory.getURI(snomedctURI.stringValue() + idxsB.get(i));
                    URI uri_2 = factory.getURI(snomedctURI.stringValue() + idxsA.get(j));
                    sims[i][j] = engine.compare(sim, uri_1, uri_2);
                }
            }

            DoubleColumn col = DoubleColumn.create((String) tableB.column("column_name").get(i), sims[i]);
            sim_tables.addColumns(col);
        }

        sim_tables.write().csv("../" + fileName);

    }

}
