package org.cytoscape.io.internal.cxio;

public final class CxUtil {

    public final static String makeId(final long id) {
        return ("_:" + id);
    }

    public final static String makeId(final String id) {
        return ("_:" + id);
    }

    public final static String SELECTED            = "selected";
    public final static String REPRESENTS          = "represents";
    public final static String SHARED_NAME         = "shared name";
    public final static String SHARED_INTERACTION  = "shared interaction";
    public final static String CONTINUOUS_MAPPING  = "CONTINUOUS_MAPPING_";
    public final static String DISCRETE_MAPPING    = "DISCRETE_MAPPING_";
    public final static String PASSTHROUGH_MAPPING = "PASSTHROUGH_MAPPING_";
    public final static String VM_COL              = "COL";
    public final static String VM_TYPE             = "T";

}
