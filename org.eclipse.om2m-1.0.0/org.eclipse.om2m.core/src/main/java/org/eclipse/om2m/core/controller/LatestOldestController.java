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

import java.util.List;

import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.entities.ContentInstanceEntity;
import org.eclipse.om2m.commons.exceptions.OperationNotAllowed;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.core.entitymapper.EntityMapperFactory;
import org.eclipse.om2m.core.notifier.Notifier;
import org.eclipse.om2m.core.urimapper.UriMapper;

/**
 * Controller for latest/oldest virtual resources
 *
 */
public class LatestOldestController extends Controller{

	public enum SortingPolicy{
		LATEST,
		OLDEST
	}

	private SortingPolicy policy;

	public LatestOldestController(SortingPolicy policy) {
		this.policy = policy;
	}

	@Override
	public ResponsePrimitive doCreate(RequestPrimitive request) {
		throw new OperationNotAllowed("Create on " + policy.toString() +" is not allowed");
	}

	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		// Creating the response primitive
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Check existence of the resource
		ContainerEntity containerEntity = dbs.getDAOFactory().getContainerDAO().find(transaction, request.getTargetId());
		if (containerEntity == null) {
			throw new ResourceNotFoundException("Resource not found");
		}

		// if resource exists, check authorization
		// retrieve 
		List<AccessControlPolicyEntity> acpList = containerEntity.getAccessControlPolicies();
		checkACP(acpList, request.getFrom(), request.getOperation());

		ContentInstanceEntity cinEntity = null;
		if (containerEntity.getChildContentInstances().isEmpty()) {
			throw new ResourceNotFoundException("Resource not found");
		}
		switch(this.policy){
		case LATEST:
			cinEntity = containerEntity.getChildContentInstances().get(
					containerEntity.getChildContentInstances().size()-1);
			break;
		case OLDEST:
			cinEntity = containerEntity.getChildContentInstances().get(0);
			break;
		default:
			break;
		}

		// mapping the entity with the exchange resource
		ContentInstance cin = EntityMapperFactory.getContentInstanceMapper().mapEntityToResource(cinEntity, request);		
		response.setContent(cin);
		response.setResponseStatusCode(ResponseStatusCode.OK);
		return response;
	}

	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		throw new OperationNotAllowed("Update on " + policy + " is not allowed");
	}

	@Override
	public ResponsePrimitive doDelete(RequestPrimitive request) {
		// Creating the response primitive
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Check existence of the resource
		ContainerEntity containerEntity = dbs.getDAOFactory().getContainerDAO().find(transaction, request.getTargetId());
		if (containerEntity == null) {
			throw new ResourceNotFoundException();
		}

		// if resource exists, check authorization
		// retrieve 
		List<AccessControlPolicyEntity> acpList = containerEntity.getAccessControlPolicies();
		checkACP(acpList, request.getFrom(), request.getOperation());

		if (containerEntity.getChildContentInstances().isEmpty()) {
			throw new ResourceNotFoundException();
		}
		ContentInstanceEntity cinEntity = null;
		switch(this.policy){
		case LATEST:
			cinEntity = containerEntity.getChildContentInstances().get(
					containerEntity.getChildContentInstances().size()-1);
			break;
		case OLDEST:
			cinEntity = containerEntity.getChildContentInstances().get(0);
			break;
		default:
			break;
		}
		UriMapper.deleteUri(cinEntity.getHierarchicalURI());
		
		Notifier.notifyDeletion(null, cinEntity);
		
		dbs.getDAOFactory().getContentInstanceDAO().delete(transaction, cinEntity);
		transaction.commit();
		response.setResponseStatusCode(ResponseStatusCode.DELETED);
		return response;
	}

}
