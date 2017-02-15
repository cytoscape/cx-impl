package org.cytoscape.io.cx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.cxio.core.CxReader;
import org.cxio.core.interfaces.AspectElement;
import org.cxio.metadata.MetaDataCollection;
import org.cxio.misc.AspectElementCounts;
import org.cytoscape.ding.DVisualLexicon;
import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.ding.customgraphics.CustomGraphicsManager;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.io.internal.cx_reader.CxToCy;
import org.cytoscape.io.internal.cx_writer.CxNetworkWriter;
import org.cytoscape.io.internal.cxio.AspectSet;
import org.cytoscape.io.internal.cxio.CxExporter;
import org.cytoscape.io.internal.cxio.CxImporter;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.junit.Test;

public class CxNetworkWriterTest {
	
	
	protected NetworkTestSupport nts = new NetworkTestSupport();
	protected NetworkViewTestSupport nvts = new NetworkViewTestSupport();
	protected CyNetworkManager networkManager = nts.getNetworkManager();

    private List<CyNetwork> loadNetwork(final File test_file, final boolean must_have_meta) throws Exception {
        final NetworkTestSupport nts = new NetworkTestSupport();
        final AspectSet aspects = new AspectSet();
        aspects.addAspect(Aspect.NODES);
        aspects.addAspect(Aspect.EDGES);
        aspects.addAspect(Aspect.NODE_ATTRIBUTES);
        aspects.addAspect(Aspect.EDGE_ATTRIBUTES);
        aspects.addAspect(Aspect.NETWORK_ATTRIBUTES);
        aspects.addAspect(Aspect.HIDDEN_ATTRIBUTES);
        aspects.addAspect(Aspect.VISUAL_PROPERTIES);
        aspects.addAspect(Aspect.CARTESIAN_LAYOUT);
        aspects.addAspect(Aspect.NETWORK_RELATIONS);
        aspects.addAspect(Aspect.SUBNETWORKS);
        aspects.addAspect(Aspect.GROUPS);

        final CxImporter cx_importer = CxImporter.createInstance();

        SortedMap<String, List<AspectElement>> res = null;
        final InputStream is = new FileInputStream(test_file);
        final CxReader cxr = cx_importer.obtainCxReader(aspects, is);
        res = CxReader.parseAsMap(cxr);
        final AspectElementCounts counts = cxr.getAspectElementCounts();

        final MetaDataCollection pre = cxr.getPreMetaData();
        final MetaDataCollection post = cxr.getPostMetaData();
        assertTrue(counts != null);
        if (must_have_meta) {
            assertTrue((pre != null) || ((post != null) && ((pre.size() > 0) || (post.size() > 0))));
        }

        final CxToCy cx_to_cy = new CxToCy();

        final CyNetworkFactory network_factory = nts.getNetworkFactory();

        final CyRootNetwork root_network = null;
        final List<CyNetwork> networks = cx_to_cy.createNetwork(res, root_network, network_factory, null, true);
        assertTrue((networks != null));
        return networks;
    }

    private List<CyNetwork> loadNetwork(final InputStream in, final boolean must_have_meta) throws Exception {
        final NetworkTestSupport nts = new NetworkTestSupport();
        final AspectSet aspects = new AspectSet();
        aspects.addAspect(Aspect.NODES);
        aspects.addAspect(Aspect.EDGES);
        aspects.addAspect(Aspect.NODE_ATTRIBUTES);
        aspects.addAspect(Aspect.EDGE_ATTRIBUTES);
        aspects.addAspect(Aspect.NETWORK_ATTRIBUTES);
        aspects.addAspect(Aspect.HIDDEN_ATTRIBUTES);
        aspects.addAspect(Aspect.VISUAL_PROPERTIES);
        aspects.addAspect(Aspect.CARTESIAN_LAYOUT);
        aspects.addAspect(Aspect.NETWORK_RELATIONS);
        aspects.addAspect(Aspect.SUBNETWORKS);
        aspects.addAspect(Aspect.GROUPS);

        final CxImporter cx_importer = CxImporter.createInstance();

        SortedMap<String, List<AspectElement>> res = null;

        final CxReader cxr = cx_importer.obtainCxReader(aspects, in);
        res = CxReader.parseAsMap(cxr);
        final AspectElementCounts counts = cxr.getAspectElementCounts();

        final MetaDataCollection pre = cxr.getPreMetaData();
        final MetaDataCollection post = cxr.getPostMetaData();
        assertTrue(counts != null);
        if (must_have_meta) {
            assertTrue((pre != null) || ((post != null) && ((pre.size() > 0) || (post.size() > 0))));
        }

        final CxToCy cx_to_cy = new CxToCy();

        final CyNetworkFactory network_factory = nts.getNetworkFactory();

        final CyRootNetwork root_network = null;
        final List<CyNetwork> networks = cx_to_cy.createNetwork(res, root_network, network_factory, null, true);
        assertTrue((networks != null));
        return networks;
    }

    private ByteArrayOutputStream writeNetwork(final CyNetwork network) throws Exception {
        final AspectSet aspects = new AspectSet();
        aspects.addAspect(Aspect.NODES);
        aspects.addAspect(Aspect.EDGES);
        aspects.addAspect(Aspect.NETWORK_ATTRIBUTES);
        aspects.addAspect(Aspect.NODE_ATTRIBUTES);
        aspects.addAspect(Aspect.EDGE_ATTRIBUTES);
        aspects.addAspect(Aspect.HIDDEN_ATTRIBUTES);
        aspects.addAspect(Aspect.CARTESIAN_LAYOUT);
        aspects.addAspect(Aspect.VISUAL_PROPERTIES);
        aspects.addAspect(Aspect.SUBNETWORKS);
        aspects.addAspect(Aspect.VIEWS);
        aspects.addAspect(Aspect.NETWORK_RELATIONS);
        aspects.addAspect(Aspect.GROUPS);

        final CxExporter exporter = CxExporter.createInstance();
        exporter.setUseDefaultPrettyPrinting(true);
        exporter.setGroupManager(null);
        exporter.setWritePreMetadata(true);
        exporter.setWritePostMetadata(true);
        exporter.setNextSuid(SUIDFactory.getNextSUID());

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        exporter.writeNetwork(network, true, aspects, out);
        return out;

    }

    @Test
    public void test1() throws Exception {
        final File test_file = new File("src/test/resources/testData/gal_filtered_1.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, true);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 331);
        assertTrue(n.getEdgeCount() == 362);

        final ByteArrayOutputStream out = writeNetwork(n);
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
        final List<CyNetwork> networks_2 = loadNetwork(in, true);
        assertTrue((networks_2.size() == 1));
        final CyNetwork n2 = networks_2.get(0);
        assertTrue(n2.getNodeCount() == 331);
        assertTrue(n2.getEdgeCount() == 362);

    }

    @Test
    public void test2() throws Exception {
        final File test_file = new File("src/test/resources/testData/gal_filtered_2.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, true);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 331);
        assertTrue(n.getEdgeCount() == 362);

        final ByteArrayOutputStream out = writeNetwork(n);
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
        final List<CyNetwork> networks_2 = loadNetwork(in, true);
        assertTrue((networks_2.size() == 1));
        final CyNetwork n2 = networks_2.get(0);
        assertTrue(n2.getNodeCount() == 331);
        assertTrue(n2.getEdgeCount() == 362);
    }

    @Test
    public void test3() throws Exception {
        final File test_file = new File("src/test/resources/testData/various_mappings_gradients.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, true);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 6);
        assertTrue(n.getEdgeCount() == 5);
    }

    @Test
    public void test4() throws Exception {
        final File test_file = new File("src/test/resources/testData/collection_1.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, true);
        assertTrue((networks.size() == 3));
        final CyNetwork n1 = networks.get(0);
        assertTrue(n1.getNodeCount() == 2);
        assertTrue(n1.getEdgeCount() == 1);
        final CyNetwork n2 = networks.get(1);
        assertTrue(n2.getNodeCount() == 3);
        assertTrue(n2.getEdgeCount() == 3);
        final CyNetwork n3 = networks.get(2);
        assertTrue(n3.getNodeCount() == 2);
        assertTrue(n3.getEdgeCount() == 1);

        final ByteArrayOutputStream out = writeNetwork(n1);
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
        final List<CyNetwork> networks_2 = loadNetwork(in, true);

    }

    @Test
    public void test5() throws Exception {
        final File test_file = new File("src/test/resources/testData/collection_2.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, true);
        assertTrue((networks.size() == 4));
        final CyNetwork n1 = networks.get(0);
        assertTrue(n1.getNodeCount() == 2);
        assertTrue(n1.getEdgeCount() == 1);
        final CyNetwork n2 = networks.get(1);
        assertTrue(n2.getNodeCount() == 5);
        assertTrue(n2.getEdgeCount() == 10);
        final CyNetwork n3 = networks.get(2);
        assertTrue(n3.getNodeCount() == 8);
        assertTrue(n3.getEdgeCount() == 28);
        final CyNetwork n4 = networks.get(3);
        assertTrue(n4.getNodeCount() == 4);
        assertTrue(n4.getEdgeCount() == 6);
    }

    @Test
    public void test6() throws Exception {
        final File test_file = new File("src/test/resources/testData/nodes_edges_only.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, false);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 4);
        assertTrue(n.getEdgeCount() == 3);

        final ByteArrayOutputStream out = writeNetwork(n);
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
        final List<CyNetwork> networks_2 = loadNetwork(in, true);
        assertTrue((networks_2.size() == 1));
        final CyNetwork n2 = networks_2.get(0);
        assertTrue(n2.getNodeCount() == 4);
        assertTrue(n2.getEdgeCount() == 3);
    }

    @Test
    public void test7() throws Exception {
        final File test_file = new File("src/test/resources/testData/nodes_edges_network_atts.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, false);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 4);
        assertTrue(n.getEdgeCount() == 3);

        final ByteArrayOutputStream out = writeNetwork(n);
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
        final List<CyNetwork> networks_2 = loadNetwork(in, true);
        assertTrue((networks_2.size() == 1));
        final CyNetwork n2 = networks_2.get(0);
        assertTrue(n2.getNodeCount() == 4);
        assertTrue(n2.getEdgeCount() == 3);
    }

    @Test
    public void test8() throws Exception {
        final File test_file = new File("src/test/resources/testData/nodes_edges_netw_node_edge_atts.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, false);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 4);
        assertTrue(n.getEdgeCount() == 3);

        final ByteArrayOutputStream out = writeNetwork(n);
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
        final List<CyNetwork> networks_2 = loadNetwork(in, true);
        assertTrue((networks_2.size() == 1));
        final CyNetwork n2 = networks_2.get(0);
        assertTrue(n2.getNodeCount() == 4);
        assertTrue(n2.getEdgeCount() == 3);
    }

    @Test
    public void test9() throws Exception {
        final File test_file = new File("src/test/resources/testData/nodes_edges_netw_node_edge_atts_coords.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, false);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 4);
        assertTrue(n.getEdgeCount() == 3);

        final ByteArrayOutputStream out = writeNetwork(n);
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
        final List<CyNetwork> networks_2 = loadNetwork(in, true);
        assertTrue((networks_2.size() == 1));
        final CyNetwork n2 = networks_2.get(0);
        assertTrue(n2.getNodeCount() == 4);
        assertTrue(n2.getEdgeCount() == 3);
    }

    @Test
    public void test10() throws Exception {
        final File test_file = new File("src/test/resources/testData/nodes_edges_netw_node_edge_atts_coords_vis_prop_1.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, false);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 4);
        assertTrue(n.getEdgeCount() == 3);
    }

    @Test
    public void test11() throws Exception {
        final File test_file = new File("src/test/resources/testData/nodes_edges_netw_node_edge_atts_coords_vis_prop_2.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, false);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 4);
        assertTrue(n.getEdgeCount() == 3);
    }

    @Test
    public void test12() throws Exception {
        final File test_file = new File("src/test/resources/testData/nodes_edges_netw_node_edge_atts_coords_vis_prop_3.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, false);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 4);
        assertTrue(n.getEdgeCount() == 3);
    }

    @Test
    public void test13() throws Exception {
        final File test_file = new File("src/test/resources/testData/groups_1.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, false);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 5);
        assertTrue(n.getEdgeCount() == 5);

        final ByteArrayOutputStream out = writeNetwork(n);
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
        final List<CyNetwork> networks_2 = loadNetwork(in, true);
        assertTrue((networks_2.size() == 1));
        final CyNetwork n2 = networks_2.get(0);
        assertTrue(n2.getNodeCount() == 5);
        assertTrue(n2.getEdgeCount() == 5);
    }

    @Test
    public void test14() throws Exception {
        final File test_file = new File("src/test/resources/testData/mapping_types.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, false);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 2);
        assertTrue(n.getEdgeCount() == 0);

        final ByteArrayOutputStream out = writeNetwork(n);
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
        final List<CyNetwork> networks_2 = loadNetwork(in, true);
        assertTrue((networks_2.size() == 1));
        final CyNetwork n2 = networks_2.get(0);
        assertTrue(n2.getNodeCount() == 2);
        assertTrue(n2.getEdgeCount() == 0);
    }
    
    @Test
    public void testRootNetworkAttr() throws Exception {
        final File test_file = new File("src/test/resources/testData/mapping_types.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, false);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 2);
        assertTrue(n.getEdgeCount() == 0);
        
        final CyRootNetwork root = ((CySubNetwork)n).getRootNetwork();
        
        final String subName = "Subnetwork 1";
        final String collectionName = "Collection 1";
        
        // New Sub Network Name
        n.getRow(n).set(CyNetwork.NAME, subName);
        // New Collection name
        root.getRow(root).set(CyNetwork.NAME, collectionName);
        
        final String attr1 = "description";
        final String attr2 = "double1";
        final String attr3 = "int1";
        final String attr4 = "bool1";
        
        final String val1 = "test description";
        final Double val2 = 22.11;
        final Integer val3 = 7;
        final Boolean val4 = true;
        
        root.getDefaultNetworkTable().createColumn(attr1, String.class, false);
        root.getDefaultNetworkTable().createColumn(attr2, Double.class, false);
        root.getDefaultNetworkTable().createColumn(attr3, Integer.class, false);
        root.getDefaultNetworkTable().createColumn(attr4, Boolean.class, false);
        
        root.getRow(root).set(attr1, val1);
        root.getRow(root).set(attr2, val2);
        root.getRow(root).set(attr3, val3);
        root.getRow(root).set(attr4, val4);
        
        System.out.println(root.getDefaultNetworkTable().getColumns());
        
        // name, shared_name, selected, SUID, and extra 4 columns
        assertEquals(root.getDefaultNetworkTable().getColumns().size(), 8);
        
        final ByteArrayOutputStream out = writeNetwork(n);
        String outStr = out.toString();
        System.out.println(outStr);
        
        final ByteArrayInputStream in = new ByteArrayInputStream(outStr.getBytes(StandardCharsets.UTF_8));
        final List<CyNetwork> networks_2 = loadNetwork(in, true);
        assertTrue((networks_2.size() == 1));
        final CyNetwork n2 = networks_2.get(0);
        assertTrue(n2.getNodeCount() == 2);
        assertTrue(n2.getEdgeCount() == 0);
        
        final CyRootNetwork root2 = ((CySubNetwork)n2).getRootNetwork();
        System.out.println("======= Resulting root network ==========");
        System.out.println(root2.getDefaultNetworkTable().getColumns());
        assertEquals(root2.getDefaultNetworkTable().getColumns().size(), 8);
        
        System.out.println(root2.getRow(root2));
        
        final CyRow r1 = n2.getRow(n2);
        final CyRow row2 = root2.getRow(root2);
        n2.getDefaultNetworkTable().getColumns().stream()
        		.forEach(col-> System.out.println(col.getName() + " = " + r1.get(col.getName(), col.getType())));
        System.out.println("====================");
        root2.getDefaultNetworkTable().getColumns().stream()
        		.forEach(col-> System.out.println(col.getName() + " = " + row2.get(col.getName(), col.getType())));
        
        assertEquals(n2.getRow(n2).get(CyNetwork.NAME, String.class), subName);
        assertEquals(root2.getRow(root2).get(CyNetwork.NAME, String.class), collectionName);
        
        assertEquals(root2.getRow(root2).get(attr1, String.class), val1);
        assertEquals(root2.getRow(root2).get(attr2, Double.class), val2);
        assertEquals(root2.getRow(root2).get(attr3, Integer.class), val3);
        assertEquals(root2.getRow(root2).get(attr4, Boolean.class), val4);
    }
    
    @Test
    public void aspectFilterTest1() throws Exception {
        final File test_file = new File("src/test/resources/testData/gal_filtered_1.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, true);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        assertTrue(n.getNodeCount() == 331);
        assertTrue(n.getEdgeCount() == 362);
        
        // Create writer
        final File outFile = new File("target/gal_filtered_1_filtered.cx");

        final CxNetworkWriter writer = this.buildWriter(n, outFile);
        
        // Specify aspect name to be written in the CX
		final List<String> aspects = new ArrayList<>();
		aspects.add(Aspect.NODES.toString());
		aspects.add(Aspect.NODE_ATTRIBUTES.toString());
		
		final List<String> nodeFilter = new ArrayList<>();
		nodeFilter.add(CyNetwork.NAME);
		writer.aspectFilter.setSelectedValues(aspects);
		writer.nodeColFilter.setSelectedValues(nodeFilter);
		
		writer.run(null);
    }
    
    @Test
    public void aspectFilterTest2() throws Exception {
    		// Diffusion service example
    		final String diffuseFile = "src/test/resources/testData/diffuse1.cx";
        final File df = new File(diffuseFile);
        final List<CyNetwork> networks = loadNetwork(df, true);
        assertTrue((networks.size() == 1));
        final CyNetwork n = networks.get(0);
        
        // Create writer
        final File outFile = new File("target/diffuse1-filtered.cx");
        final CxNetworkWriter writer = this.buildWriter(n, outFile);
        
        // Specify aspect name to be written in the CX
		final List<String> aspects = new ArrayList<>();
		aspects.add(Aspect.NODES.toString());
		aspects.add(Aspect.EDGES.toString());
		aspects.add(Aspect.NODE_ATTRIBUTES.toString());
		
		final List<String> nodeFilter = new ArrayList<>();
		nodeFilter.add(CyNetwork.NAME);
		nodeFilter.add("diffusion_input");
		
		writer.aspectFilter.setSelectedValues(aspects);
		writer.nodeColFilter.setSelectedValues(nodeFilter);
		
		writer.run(null);
    }
    
    @Test
    public void aspectFilterTest3() throws Exception {
    		// Diffusion service example
    		final String diffuseFile = "src/test/resources/testData/galFiltered_2subnets.cx";
        final File df = new File(diffuseFile);
        final List<CyNetwork> networks = loadNetwork(df, true);
        assertTrue((networks.size() == 2));
        final CyNetwork n1 = networks.get(0);
        final CyNetwork n2 = networks.get(1);
        
        // Create writer
        final File outFile = new File("target/sub2.cx");
        final CxNetworkWriter writer = this.buildWriter(n2, outFile);
        
        // Specify aspect name to be written in the CX
		final List<String> aspects = new ArrayList<>();
		aspects.add(Aspect.NODES.toString());
		aspects.add(Aspect.EDGES.toString());
		aspects.add(Aspect.NODE_ATTRIBUTES.toString());
		aspects.add(Aspect.EDGE_ATTRIBUTES.toString());
		
		final List<String> nodeFilter = new ArrayList<>();
		nodeFilter.add(CyNetwork.NAME);
		
		final List<String> edgeFilter = new ArrayList<>();
		edgeFilter.add(CyNetwork.SELECTED);
		
		writer.aspectFilter.setSelectedValues(aspects);
		writer.nodeColFilter.setSelectedValues(nodeFilter);
		writer.edgeColFilter.setSelectedValues(edgeFilter);

		writer.writeSiblings = false;
		
		writer.run(null);
		
		// Check the contents
        final List<CyNetwork> result = loadNetwork(outFile, true);
        assertTrue((result.size() == 1));
        final CyNetwork resultNet = result.get(0);
        assertTrue(resultNet.getNodeCount() == 19);
        assertTrue(resultNet.getEdgeCount() == 27);
        
        final Collection<CyColumn> edgeCols = resultNet.getDefaultEdgeTable().getColumns();
        System.out.println(edgeCols);
        assertEquals(6, edgeCols.size());
        
        
    }
    
    @Test
    public void aspectFilterTestFull() throws Exception {
        final File test_file = new File("src/test/resources/testData/gal_filtered_1.cx");
        final List<CyNetwork> networks = loadNetwork(test_file, true);
        final CyNetwork n = networks.get(0);
        
        // Create writer
        final String outFileName = "target/gal_filtered_1_full.cx";
        final File outFile = new File(outFileName);

        // By default, it writes all of the standard aspects
        final CxNetworkWriter writer = this.buildWriter(n, outFile);
		writer.run(null);
		
        final List<CyNetwork> networks_2 = loadNetwork(new File(outFileName), true);
        assertTrue((networks_2.size() == 1));
        final CyNetwork n2 = networks_2.get(0);
        assertTrue(n2.getNodeCount() == 331);
        assertTrue(n2.getEdgeCount() == 362);
    }
    
    private final CxNetworkWriter buildWriter(final CyNetwork n, final File outFile) throws Exception {
        
    		final FileOutputStream out = new FileOutputStream(outFile);
        
		VisualMappingManager vmm = mock(VisualMappingManager.class);
		Set<VisualStyle> styles = new HashSet<VisualStyle>();
		VisualStyle mockStyle = mock(VisualStyle.class);
		when(mockStyle.getTitle()).thenReturn("mock1");
		styles.add(mockStyle);
		when(vmm.getAllVisualStyles()).thenReturn(styles);
		
		CyNetworkViewManager viewManager = mock(CyNetworkViewManager.class);
		Collection<CyNetworkView> views = new HashSet<>();
		when(viewManager.getNetworkViews(n)).thenReturn(views);
        
		CyGroupManager groupManager = mock(CyGroupManager.class);
		CyNetworkTableManager tblManager = mock(CyNetworkTableManager.class);
        
		final CustomGraphicsManager cgManager = mock(CustomGraphicsManager.class);
		DVisualLexicon lexicon = new DVisualLexicon(cgManager);
		Set<VisualLexicon> lex = new HashSet<>();
		lex.add(lexicon);
		when(vmm.getAllVisualLexicon()).thenReturn(lex);
				
		final CxNetworkWriter writer = new CxNetworkWriter(
        		out, n, vmm, viewManager, networkManager, groupManager, tblManager, null);
    	
		return writer;
    }
}
