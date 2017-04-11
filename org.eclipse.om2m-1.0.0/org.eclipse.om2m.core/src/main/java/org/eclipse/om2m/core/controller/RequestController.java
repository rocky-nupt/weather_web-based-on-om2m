/*******************************************************************************
 * Copyright (c) 2013-2016 LAAS-CNRS (www.laas.fr)
 * 7 Colonel Roche 31077 Toulouse - France
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributors:
 *     Thierry Monteil : Project manager, technical co-manager
 *     Mahdi Ben Alaya : Technical co-manager
 *     Samir Medjiah : Technical co-manager
 *     Khalil Drira : Strategy expert
 *     Guillaume Garzone : Developer
 *     François Aïssaoui : Developer
 *
 * New contributors :
 *******************************************************************************/
package org.eclipse.om2m.core.controller;

import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.entities.RequestEntity;
import org.eclipse.om2m.commons.exceptions.AccessDeniedException;
import org.eclipse.om2m.commons.exceptions.OperationNotAllowed;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.Request;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.core.entitymapper.EntityMapperFactory;
import org.eclipse.om2m.core.urimapper.UriMapper;

public class RequestController extends Controller {

	@Override
	public ResponsePrimitive doCreate(RequestPrimitive request) {
		throw new OperationNotAllowed("Create on Request is not allowed");
	}

	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);
		
		RequestEntity requestEntity = dbs.getDAOFactory().getRequestEntityDAO().
				find(transaction, request.getTargetId());
		if(requestEntity == null){
			throw new ResourceNotFoundException();
		}
		
		// Check authorization
		if(!request.getFrom().equals(requestEntity.getOriginator())
				&& !request.getFrom().equals(Constants.ADMIN_REQUESTING_ENTITY)){
			throw new AccessDeniedException();
		}
		
		Request requestResource = EntityMapperFactory.getRequestMapper()
				.mapEntityToResource(requestEntity, request);
		response.setContent(requestResource);
		response.setResponseStatusCode(ResponseStatusCode.OK);
		return response;
	}

	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		throw new OperationNotAllowed("Update on Request is not allowed");
	}

	@Override
	public ResponsePrimitive doDelete(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);
		
		RequestEntity requestEntity = dbs.getDAOFactory().
				getRequestEntityDAO().find(transaction, request.getTargetId());
		if(requestEntity == null){
			throw new ResourceNotFoundException();
		}
		
		// Check authorization
		if(!request.getFrom().equals(requestEntity.getOriginator())
				&& !request.getFrom().equals(Constants.ADMIN_REQUESTING_ENTITY)){
			throw new AccessDeniedException();
		}
		
		UriMapper.deleteUri(requestEntity.getHierarchicalURI());
		
		dbs.getDAOFactory().getRequestEntityDAO().delete(transaction, requestEntity);
		transaction.commit();
		response.setResponseStatusCode(ResponseStatusCode.DELETED);
		return response;
	}

}
