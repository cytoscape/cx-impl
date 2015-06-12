package org.cytoscape.io.internal.cxio.kit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.cytoscape.io.internal.cxio.kit.CxConstants.ATTRIBUTE_TYPE;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class EdgeAttributesFragmentReader implements AspectFragmentReader {
    private static final boolean STRICT = true;

    public static EdgeAttributesFragmentReader createInstance() {
        return new EdgeAttributesFragmentReader();
    }

    private EdgeAttributesFragmentReader() {
    }

    @Override
    public String getAspectName() {
        return CxConstants.EDGE_ATTRIBUTES;
    }

    @Override
    public List<AspectElement> readAspectFragment(final JsonParser jp) throws IOException {
        JsonToken t = jp.nextToken();
        if (t != JsonToken.START_ARRAY) {
            throw new IOException("malformed cx json in '" + CxConstants.EDGES + "'");
        }
        final List<AspectElement> ea_aspects = new ArrayList<AspectElement>();
        while (t != JsonToken.END_ARRAY) {
            if (t == JsonToken.START_OBJECT) {
                String id = null;
                List<String> edges = null;
                final SortedMap<String, AttributeValues> attributes = new TreeMap<String, AttributeValues>();
                final Map<String, String> attribute_types = new HashMap<String, String>();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    final String namefield = jp.getCurrentName();
                    jp.nextToken(); // move to value
                    if (CxConstants.ID.equals(namefield)) {
                        id = jp.getText().trim();
                    }
                    else if (CxConstants.EDGES.equals(namefield)) {
                        edges = Util.parseSimpleList(jp, t);
                    }
                    else if (CxConstants.ATTRIBUTES.equals(namefield)) {
                        while (jp.nextToken() != JsonToken.END_OBJECT) {
                            jp.nextToken(); // move to value
                            attributes.put(jp.getCurrentName(),
                                           new AttributeValues(null, Util.parseSimpleList(jp, t)));
                        }
                    }
                    else if (CxConstants.ATTRIBUTE_TYPES.equals(namefield)) {
                        while (jp.nextToken() != JsonToken.END_OBJECT) {
                            jp.nextToken(); // move to value
                            attribute_types.put(jp.getCurrentName(), jp.getText().trim());
                        }
                    }
                    else if (STRICT) {
                        throw new IOException("malformed cx json: unrecognized field '" + namefield
                                              + "'");
                    }
                }
                if (Util.isEmpty(id)) {
                    throw new IOException(
                            "malformed cx json: attribute id in edge attributes is missing");
                }
                if ((edges == null) || edges.isEmpty()) {
                    throw new IOException(
                            "malformed cx json: edge ids in edge attributes are missing");
                }

                for (final Map.Entry<String, AttributeValues> entry : attributes.entrySet()) {
                    if ( attribute_types.containsKey(entry.getKey()) ) {
                        entry.getValue().setType(attribute_types.get(entry.getKey()));
                    }
                    else {
                        entry.getValue().setType(ATTRIBUTE_TYPE.STRING);
                    }
                }
                
                ea_aspects.add(new EdgeAttributesElement(id, edges, attributes));
            }
            t = jp.nextToken();
        }

        return ea_aspects;
    }
}
