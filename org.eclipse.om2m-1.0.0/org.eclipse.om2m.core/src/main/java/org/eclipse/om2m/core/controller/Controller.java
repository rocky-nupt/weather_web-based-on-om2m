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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.entities.AccessControlOriginatorEntity;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AccessControlRuleEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.exceptions.AccessDeniedException;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.Om2mException;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.Resource;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;
import org.eclipse.om2m.core.entitymapper.EntityMapper;
import org.eclipse.om2m.core.entitymapper.EntityMapperFactory;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

/**
 * Controller class contains generic and abstract Create, Retrieve, Update, Delete and Execute
 * methods to handle generic REST request that will be implemented in extended-to classes.
 *
 */
public abstract class Controller {
	/** Logger */
	protected static Log LOGGER = LogFactory.getLog(Controller.class);

	/** Pointer to Database service */
	protected DBService dbs;
	/** Transaction for the current instance of controller and request */
	protected DBTransaction transaction; 

	/**
	 * Perform the request on selected controller
	 * @param request
	 * @return
	 */
	public ResponsePrimitive doRequest(RequestPrimitive request) throws Om2mException{
		ResponsePrimitive response = new ResponsePrimitive(request);
		dbs = PersistenceService.getInstance().getDbService();
		transaction = dbs.getDbTransaction();
		try{
			transaction.open();
			if(request.getOperation().equals(Operation.CREATE)){
				response = doCreate(request);
			} else if(request.getOperation().equals(Operation.RETRIEVE)){
				response = doRetrieve(request);
			} else if(request.getOperation().equals(Operation.UPDATE)){
				response = doUpdate(request);
			} else if(request.getOperation().equals(Operation.DELETE)){
				response = doDelete(request);
			} else {
				throw new BadRequestException("Incorrect Operation value (op): " + request.getOperation());
			}
		} catch(Om2mException om2mException){
			throw om2mException;
		} catch(Exception e){
			LOGGER.error("Controller internal error", e);
			throw e;
		} finally {
			transaction.close();
		}
		return response;
	}

	/**
	 * Abstract Create method to handle generic REST request.
	 * @param request - The generic request to handle.
	 * @return The generic returned response.
	 */
	public abstract ResponsePrimitive doCreate (RequestPrimitive request);

	/**
	 * Abstract Retrieve method to handle generic REST request.
	 * @param request - The generic request to handle.
	 * @return The generic returned response.
	 */
	public abstract ResponsePrimitive doRetrieve (RequestPrimitive request);

	/**
	 * Abstract Update method to handle generic REST request.
	 * @param request - The generic request to handle.
	 * @return The generic returned response.
	 */
	public abstract ResponsePrimitive doUpdate (RequestPrimitive request);

	/**
	 * Abstract Delete method to handle generic REST request.
	 * @param request - The generic request to handle.
	 * @return The generic returned response.
	 */
	public abstract ResponsePrimitive doDelete (RequestPrimitive request);

	/**
	 * Check the access right based on acpId
	 * @param acpID
	 * @param originator
	 * @param method
	 */
	public void checkACP(String acpID, String originator, BigInteger method) throws AccessDeniedException{
		DBService db = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = db.getDbTransaction();
		transaction.open();
		if (!originatorExists(originator)) {
			throw new AccessDeniedException("Provided originator not found");
		}
		AccessControlPolicyEntity acp = db.getDAOFactory().getAccessControlPolicyDAO().find(transaction, acpID);
		if (acp == null){
			throw new ResourceNotFoundException();
		}
		List<AccessControlPolicyEntity> acpList = new ArrayList<>();
		acpList.add(acp);
		checkACP(acpList, originator, method);
		transaction.close();
	}

	/**
	 * Checks the Access Right based on ACP list (Permission)
	 * @param acp - Id of the accessRight
	 * @param originator - requesting entity used by the requester
	 * @param operation - requested method
	 * @return error with a specific status code if the requesting Entity or the method does not exist otherwise null
	 */
	public void checkACP(List<AccessControlPolicyEntity> acpList, String originator, BigInteger operation)
			throws AccessDeniedException{
		if(originator == null){
			throw new AccessDeniedException();
		}
		if (acpList == null || acpList.isEmpty()) {
			throw new ResourceNotFoundException("Current resource does not have any ACP attached");
		}
		// Check Resource accessRight existence not found
		boolean originatorFound = false;
		boolean operationAllowed = false;
		for(AccessControlPolicyEntity acp : acpList){
			for (AccessControlRuleEntity rule : acp.getPrivileges()){
				originatorFound = false ; 
				operationAllowed = false;
				for (AccessControlOriginatorEntity originatorEntity : rule.getAccessControlOriginators()){
					if (originator.matches(originatorEntity.getOriginatorID().replace("*", ".*"))){
						originatorFound = true;
						break;
					}
				}
				if (originatorFound){
					if (operation.equals(Operation.CREATE) && rule.isCreate()){
						operationAllowed = true;
					} else if (operation.equals(Operation.RETRIEVE) && rule.isRetrieve()){
						operationAllowed = true;
					} else if (operation.equals(Operation.UPDATE) && rule.isUpdate()){
						operationAllowed = true;
					} else if (operation.equals(Operation.DELETE) && rule.isDelete()){
						operationAllowed = true; 
					} else if (operation.equals(Operation.DISCOVERY) && rule.isDiscovery()){
						operationAllowed = true;
					} else if (operation.equals(Operation.NOTIFY) && rule.isNotify()){
						operationAllowed = true;
					}
				}
				if (originatorFound && operationAllowed){
					break;
				}
			}
			if (originatorFound && operationAllowed){
				break;
			}
		}

		if (!originatorFound){
			throw new AccessDeniedException();
		}
		if (!operationAllowed){
			throw new AccessDeniedException();
		}
	}

	/**
	 * Check Access Right from Acp Self privileges for ACP modifications
	 * @param acp to check
	 * @param originator to validate
	 * @param operation
	 */
	public void checkSelfACP(AccessControlPolicyEntity acp, String originator, BigInteger operation)
			throws AccessDeniedException{
		// Check Resource accessRight existence not found
		boolean originatorFound = false;
		boolean operationAllowed = false;

		for (AccessControlRuleEntity rule : acp.getSelfPrivileges()){
			originatorFound = false ; 
			operationAllowed = false ;
			for (AccessControlOriginatorEntity originatorEntity : rule.getAccessControlOriginators()){
				if (originator.matches(originatorEntity.getOriginatorID().replace("*", ".*"))){
					originatorFound = true;
					break;
				}
			}
			if (originatorFound){
				if (operation.equals(Operation.CREATE)){
					if (rule.isCreate()){
						operationAllowed = true;
					}
				} else if (operation.equals(Operation.RETRIEVE)){
					if (rule.isRetrieve()){
						operationAllowed = true;
					}
				} else if (operation.equals(Operation.UPDATE)){
					if (rule.isUpdate()){
						operationAllowed = true;
					}
				} else if (operation.equals(Operation.DELETE)){
					if (rule.isDelete()){
						operationAllowed = true; 
					}
				}
			}
			if (originatorFound && operationAllowed){
				break;
			}
		}
		if (!originatorFound){
			throw new AccessDeniedException();
		}
		if (!operationAllowed){
			throw new AccessDeniedException();
		}
	}


	/**
	 * Generates an random ID based on SecureRandom library
	 * @param prefix - prefix of the resource ID
	 * @param postfix - postfix of the resource ID
	 * @return generated resource ID
	 */
	public static String generateId(String prefix, String postfix) {
		SecureRandom secureRandom = new SecureRandom();
		return prefix+String.valueOf(secureRandom.nextInt(999999999))+postfix;
	}

	/**
	 * Generates a random ID based on SecureRandom library
	 * @return generated resource ID
	 */
	public static String generateId(){
		return generateId("", "");
	}

	protected void setLocationAndCreationContent(RequestPrimitive request, 
			ResponsePrimitive response, ResourceEntity entity){
		setLocationAndCreationContent(request, response, entity, EntityMapperFactory.
						getMapperFromResourceType(entity.getResourceType().intValue()));
	}

	@SuppressWarnings("unchecked")
	protected void setLocationAndCreationContent(RequestPrimitive request, 
			ResponsePrimitive response, ResourceEntity entity, @SuppressWarnings("rawtypes") EntityMapper mapper){
		if(request.getResultContent()!= null){
			if (request.getResultContent().equals(ResultContent.HIERARCHICAL_ADRESS)
					|| request.getResultContent().equals(ResultContent.HIERARCHICAL_AND_ATTRIBUTES)){
				response.setLocation(entity.getHierarchicalURI());
			} else {
				response.setLocation(entity.getResourceID());
			}
			if(request.getResultContent().equals(ResultContent.HIERARCHICAL_AND_ATTRIBUTES)
					|| request.getResultContent().equals(ResultContent.ATTRIBUTES)){
				Resource res = mapper.mapEntityToResource(entity, ResultContent.ATTRIBUTES);
				if(request.getReturnContentType().equals(MimeMediaType.OBJ)){
					response.setContent(res);
				} else {
					String representation = DataMapperSelector.getDataMapperList().
							get(request.getReturnContentType()).objToString(res);
					response.setContent(representation);
				}
			}
		} else {
			response.setContent(mapper.mapEntityToResource(entity, ResultContent.ATTRIBUTES));
			response.setLocation(entity.getResourceID());			
		}
	}
	
	
	/**
	 * Allows to know if the provided originator exists in the system
	 * @param originator
	 * @return true if exists
	 */
	protected boolean originatorExists(String originator) {
		boolean isValid = false;
		DBService db = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = db.getDbTransaction();
		transaction.open();
		AccessControlOriginatorEntity originatorEntity = db.getDAOFactory().getAccessControlOriginatorDAO().find(transaction, originator);
		if (originatorEntity != null) {
			isValid = true;
		}
		transaction.close();
		return isValid;
	}

	/**
	 * Allows to store the originator
	 * @param originator
	 */
	protected void registerOriginator(String originator) {
		DBService db = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = db.getDbTransaction();
		transaction.open();
		// create the new originator
		AccessControlOriginatorEntity originatorEntity = new AccessControlOriginatorEntity();
		originatorEntity.setOriginatorID(originator);
		// persist the new access control originator
		db.getDAOFactory().getAccessControlOriginatorDAO().create(transaction, originatorEntity);
		transaction.commit();
		transaction.close();
	}

}
