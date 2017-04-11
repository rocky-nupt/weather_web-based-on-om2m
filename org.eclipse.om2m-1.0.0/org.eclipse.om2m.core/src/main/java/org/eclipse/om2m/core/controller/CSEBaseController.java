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
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.exceptions.OperationNotAllowed;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.CSEBase;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.core.entitymapper.EntityMapperFactory;

/**
 * Controller for CSE base resource
 *
 */
public class CSEBaseController extends Controller {

	// OPERATION_NOT_ALLOWED
	@Override
	public ResponsePrimitive doCreate(RequestPrimitive requestIndication) {
		throw new OperationNotAllowed("Create of CSEBase is not allowed");
	}

	// Generic  retrieve operation
	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Check existence of the resource
		CSEBaseEntity cseBaseEntity = dbs.getDAOFactory().getCSEBaseDAO().find(transaction, request.getTargetId());
		if (cseBaseEntity == null){
			throw new ResourceNotFoundException("Resource " + request.getTargetId() + "not found");
		}
		// Check authorization
		List<AccessControlPolicyEntity> acpList = cseBaseEntity.getAccessControlPolicies();
		checkACP(acpList, request.getFrom(), request.getOperation());
	
		// Mapping the entity with exchange resource
		CSEBase cseBaseResource = EntityMapperFactory.getCseBaseMapper().mapEntityToResource(cseBaseEntity, request);
		response.setContent(cseBaseResource);
		response.setResponseStatusCode(ResponseStatusCode.OK);
		
		return response;
	}

	// OPERATION_NOT_ALLOWED
	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive requestIndication) {
		throw new OperationNotAllowed("Update of CSEBase is not allowed");
	}

	// OPERATION_NOT_ALLOWED
	@Override
	public ResponsePrimitive doDelete(RequestPrimitive requestIndication) {
		throw new OperationNotAllowed("Delete of CSEBase is not allowed");
	}

}
