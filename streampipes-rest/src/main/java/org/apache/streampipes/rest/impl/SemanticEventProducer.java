/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.rest.impl;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.rest.api.IPipelineElement;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.rest.shared.util.SpMediaType;
import org.apache.streampipes.storage.couchdb.utils.Filter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v2/streams")
public class SemanticEventProducer extends AbstractAuthGuardedRestResource implements IPipelineElement {

	@GET
	@Path("/available")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getAvailable() {
		List<SpDataStream> seps = Filter.byUri(getPipelineElementRdfStorage().getAllDataStreams(),
				getUserService().getAvailableSourceUris(getAuthenticatedUsername()));
		return ok(seps);
	}
	
	@GET
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getFavorites() {
		List<SpDataStream> seps = Filter.byUri(getPipelineElementRdfStorage().getAllDataStreams(),
				getUserService().getFavoriteSourceUris(getAuthenticatedUsername()));
		return ok(seps);
	}

	@GET
	@Path("/own")
	@RequiresAuthentication
	@Produces({MediaType.APPLICATION_JSON, SpMediaType.JSONLD})
	@JacksonSerialized
	@Override
	public Response getOwn() {
		List<SpDataStream> seps = Filter.byUri(getPipelineElementRdfStorage().getAllDataStreams(),
				getUserService().getOwnSourceUris(getAuthenticatedUsername()));
		List<SpDataStream> si = seps.stream().map(s -> new Cloner().mapSequence(s)).collect(Collectors.toList());

		return ok(si);
	}

	@POST
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response addFavorite(@FormParam("uri") String elementUri) {
		getUserService().addSourceAsFavorite(getAuthenticatedUsername(), decode(elementUri));
		return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}

	@DELETE
	@Path("/favorites/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response removeFavorite(@PathParam("elementUri") String elementUri) {
		getUserService().removeSourceFromFavorites(getAuthenticatedUsername(), decode(elementUri));
		return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}
	
	@DELETE
	@Path("/own/{elementId}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response removeOwn(@PathParam("elementId") String elementId) {
		getUserService().deleteOwnSource(getAuthenticatedUsername(), elementId);
		getPipelineElementRdfStorage().deleteDataStream(getPipelineElementRdfStorage().getDataStreamById(elementId));
		return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
	}
	
	@Path("/{elementUri}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonSerialized
	@Override
	public Response getElement(@PathParam("elementUri") String elementUri) {
		// TODO Access rights
		return ok(new Cloner().mapSequence(getPipelineElementRdfStorage().getDataStreamById(elementUri)));
	}

}
