package org.cytoscape.io.cx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.cxio.aspects.readers.CartesianLayoutFragmentReader;
import org.cxio.aspects.readers.EdgeAttributesFragmentReader;
import org.cxio.aspects.readers.EdgesFragmentReader;
import org.cxio.aspects.readers.NodeAttributesFragmentReader;
import org.cxio.aspects.readers.NodesFragmentReader;
import org.cxio.aspects.writers.CartesianLayoutFragmentWriter;
import org.cxio.aspects.writers.EdgeAttributesFragmentWriter;
import org.cxio.aspects.writers.EdgesFragmentWriter;
import org.cxio.aspects.writers.NodeAttributesFragmentWriter;
import org.cxio.aspects.writers.NodesFragmentWriter;
import org.cxio.core.CxElementReader2;
import org.cxio.core.CxWriter;
import org.cxio.core.interfaces.AspectElement;
import org.cxio.core.interfaces.AspectFragmentReader;

final class TestUtil {

    final static String cyCxRoundTrip(final String input_cx) throws IOException {
    	ByteArrayInputStream inputStream = new ByteArrayInputStream(input_cx.getBytes());
    	final CxElementReader2 p = new CxElementReader2(inputStream, getCytoscapeAspectFragmentReaders(), true);
        
//    	final CxReader p = CxReader.createInstance(input_cx, getCytoscapeAspectFragmentReaders());
        
    
        final OutputStream out = new ByteArrayOutputStream();

        final CxWriter w = CxWriter.createInstance(out, true);
        w.addAspectFragmentWriter(NodesFragmentWriter.createInstance());
        w.addAspectFragmentWriter(EdgesFragmentWriter.createInstance());
        w.addAspectFragmentWriter(CartesianLayoutFragmentWriter.createInstance());
        w.addAspectFragmentWriter(NodeAttributesFragmentWriter.createInstance());
        w.addAspectFragmentWriter(EdgeAttributesFragmentWriter.createInstance());

        w.start();
        Iterator<AspectElement> iter = p.iterator();
        while(iter.hasNext()) {
        	AspectElement e = iter.next();
        	w.start();
            w.writeAspectElement(e);
        }
        w.end(true, null);

        return out.toString();
    }

    final static Set<AspectFragmentReader> getCytoscapeAspectFragmentReaders() {
        final Set<AspectFragmentReader> readers = new HashSet<AspectFragmentReader>();
        readers.add(NodesFragmentReader.createInstance());
        readers.add(EdgesFragmentReader.createInstance());
        readers.add(CartesianLayoutFragmentReader.createInstance());
        readers.add(NodeAttributesFragmentReader.createInstance());
        readers.add(EdgeAttributesFragmentReader.createInstance());
        return readers;
    }
}