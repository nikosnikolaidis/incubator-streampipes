package org.streampipes.storage.impl;

import org.streampipes.model.client.endpoint.RdfEndpoint;
import org.streampipes.storage.api.RdfEndpointStorage;
import org.streampipes.storage.util.Utils;
import org.lightcouch.CouchDbClient;

import java.util.List;

/**
 * Created by riemer on 05.10.2016.
 */
public class RdfEndpointStorageImpl extends Storage<RdfEndpoint> implements RdfEndpointStorage {

    public RdfEndpointStorageImpl() {
        super(RdfEndpoint.class);
    }


    @Override
    public void addRdfEndpoint(RdfEndpoint rdfEndpoint) {
        add(rdfEndpoint);
    }

    @Override
    public void removeRdfEndpoint(String rdfEndpointId) {
        delete(rdfEndpointId)
;    }

    @Override
    public List<RdfEndpoint> getRdfEndpoints() {
        return getAll();
    }

    @Override
    protected CouchDbClient getCouchDbClient() {
        return Utils.getCouchDbRdfEndpointClient();
    }
}