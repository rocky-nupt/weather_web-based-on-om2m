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

import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResourceStatus;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.commons.entities.AccessControlOriginatorEntity;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AccessControlRuleEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.exceptions.AccessDeniedException;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.ConflictException;
import org.eclipse.om2m.commons.exceptions.NotPermittedAttrException;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.RemoteCSE;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.utils.Util.DateUtil;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;
import org.eclipse.om2m.core.entitymapper.EntityMapperFactory;
import org.eclipse.om2m.core.notifier.Notifier;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.router.Patterns;
import org.eclipse.om2m.core.urimapper.UriMapper;
import org.eclipse.om2m.core.util.ControllerUtil;
import org.eclipse.om2m.core.util.ControllerUtil.UpdateUtil;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

/**
 * Controller for remote CSE
 *
 */
public class RemoteCSEController extends Controller {

	/**
	 * Create the resource in the system according to the representation
	 * @param request
	 * @return response
	 */
	@Override
	public ResponsePrimitive doCreate(RequestPrimitive request) {
		/*
		 * remoteCSE creation procedure
		 * 
		 * @resourceName			NP
		 * resourceType				NP
		 * resourceID				NP
		 * parentID					NP
		 * accessControlPolicyIDs	O
		 * creationTime				NP
		 * expirationTime			O
		 * lastModifiedTime			NP
		 * labels					O
		 * announceTo				O
		 * announcedAttribute		O
		 * 
		 * cseType					O
		 * poa						O
		 * CseBase					M
		 * CSE-ID					M
		 * M2M-EXT-ID				O
		 * Trigger-Recipient-ID		O
		 * requestReachability		M
		 * nodeLink					NP
		 * 
		 */

		ResponsePrimitive response = new ResponsePrimitive(request);

		// get the database service
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();

		// get the dao of the parent
		DAO<ResourceEntity> dao = (DAO<ResourceEntity>) Patterns.getDAO(request.getTargetId(), dbs);
		if (dao == null){
			throw new ResourceNotFoundException("Cannot find parent resource");
		}
		// get the parent entity
		ResourceEntity parentEntity = (ResourceEntity)dao.find(transaction, request.getTargetId());
		// check the parent existence
		if (parentEntity == null) {
			throw new ResourceNotFoundException("Cannot find the parent resource");
		}

		// get lists to change in the method corresponding to specific object
		List<AccessControlPolicyEntity> acpsToCheck = null;
		List<RemoteCSEEntity> remoteCSEs = null;
		List<SubscriptionEntity> subscriptions = null;

		// different cases
		// case parent is CSEBase
		if (parentEntity.getResourceType().intValue() == (ResourceType.CSE_BASE)) {
			CSEBaseEntity cseB = (CSEBaseEntity) parentEntity;
			acpsToCheck = cseB.getAccessControlPolicies();
			remoteCSEs = cseB.getRemoteCses();
			subscriptions = cseB.getSubscriptions();
		}

		// check if originator is provided
		if (request.getFrom() == null){
			throw new AccessDeniedException("No originator provided");
		}
		boolean newOriginator = false;
		if (originatorExists(request.getFrom())) {
			// check access control policy of the originator
			checkACP(acpsToCheck, request.getFrom(), Operation.CREATE);
		} else {
			newOriginator = true;
		}
		
		// check if content is present
		if (request.getContent() == null){
			throw new BadRequestException("A content is requiered for RemoteCSE creation");
		}
		// get the object from the representation
		RemoteCSE remoteCse = null;
		try{
			if(request.getRequestContentType().equals(MimeMediaType.OBJ)){
				remoteCse = (RemoteCSE) request.getContent();
			} else {
				remoteCse = (RemoteCSE)DataMapperSelector.getDataMapperList()
						.get(request.getRequestContentType()).stringToObj((String)request.getContent());				
			}

		} catch (ClassCastException e){
			throw new BadRequestException("Incorrect resource representation in content");
		}
		if (remoteCse == null){
			throw new BadRequestException("Error in provided content");
		}

		RemoteCSEEntity remoteCseEntity = new RemoteCSEEntity();
		// check attributes
		// @resourceName 		NP 
		// Resource Type 		NP
		// resourceID 			NP
		// parentID 			NP
		// lastModifiedTime 	NP
		// creationTime 		NP
		// labels				O
		ControllerUtil.CreateUtil.fillEntityFromGenericResource(remoteCse, remoteCseEntity);
		
		// nodelink				NP
		if (remoteCse.getNodeLink() != null){
			throw new NotPermittedAttrException("NodeLink is Not Permitted");
		}

		// CseBase					M
		if (remoteCse.getCSEBase() == null) {
			throw new BadRequestException("CseBase is Mandatory");
		} else {
			remoteCseEntity.setRemoteCseUri(remoteCse.getCSEBase());
		}
		// CSE-ID					M
		if (remoteCse.getCSEID() == null) {
			throw new BadRequestException("CSE-ID is mandatory");
		} else {
			remoteCseEntity.setRemoteCseId(remoteCse.getCSEID());
		}
		// requestReachability		M
		if (remoteCse.isRequestReachability() == null){
			throw new BadRequestException("Request Reachability is mandatory");
		} else {
			remoteCseEntity.setRequestReachability(remoteCse.isRequestReachability());
		}

		// accessControlPolicyIDs	O
		if (!remoteCse.getAccessControlPolicyIDs().isEmpty()){		
			remoteCseEntity.setAccessControlPolicies(
					ControllerUtil.buildAcpEntityList(remoteCse.getAccessControlPolicyIDs(), transaction));
		} else {
			remoteCseEntity.getAccessControlPolicies().addAll(acpsToCheck);
		}
		// expiration time 			O
		if (remoteCse.getExpirationTime() != null){
			remoteCseEntity.setExpirationTime(remoteCse.getExpirationTime());
		}
		// labels					O
		if (!remoteCse.getLabels().isEmpty()){
			remoteCseEntity.setLabelsEntitiesFromSring(remoteCse.getLabels());
		}
		// announceTo				O
		if (!remoteCse.getAnnounceTo().isEmpty()){
			remoteCseEntity.getAnnounceTo().addAll(remoteCse.getAnnounceTo());
		}
		// announcedAttribute		O
		if (!remoteCse.getAnnouncedAttribute().isEmpty()){
			remoteCseEntity.getAnnouncedAttribute().addAll(remoteCse.getAnnouncedAttribute());
		}
		// cseType					O
		if (remoteCse.getCseType() != null) {
			remoteCseEntity.setCseType(remoteCse.getCseType());
		}
		// poa						O
		if (!remoteCse.getPointOfAccess().isEmpty()){
			remoteCseEntity.getPointOfAccess().addAll(remoteCse.getPointOfAccess());
		}

		// M2M-EXT-ID				O
		if(remoteCse.getM2MExtID() != null) {
			remoteCseEntity.setM2mExtId(remoteCse.getM2MExtID());
		}
		// Trigger-Recipient-ID		O
		if (remoteCse.getTriggerRecipientID() != null) {
			remoteCseEntity.setTriggerRecipientID(remoteCse.getTriggerRecipientID());
		}

		// creating the corresponding entity
		String generatedId = generateId("", "");
		remoteCseEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.REMOTE_CSE + Constants.PREFIX_SEPERATOR + generatedId);
		// set name if present and without any conflict
		if (remoteCse.getName() != null){
			if (!Patterns.checkResourceName(remoteCse.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			remoteCseEntity.setName(remoteCse.getName());
		} else 
		if (request.getName() != null){
			if (!Patterns.checkResourceName(request.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			remoteCseEntity.setName(request.getName());
		} else {
			remoteCseEntity.setName(ShortName.REMOTE_CSE + "_" + generatedId);
		}
		remoteCseEntity.setHierarchicalURI(parentEntity.getHierarchicalURI() + "/" + remoteCseEntity.getName());
		if (!UriMapper.addNewUri(remoteCseEntity.getHierarchicalURI(), remoteCseEntity.getResourceID(), ResourceType.REMOTE_CSE)){
			throw new ConflictException("Name already present in the parent collection.");
		}
		remoteCseEntity.setCreationTime(DateUtil.now());
		remoteCseEntity.setLastModifiedTime(DateUtil.now());
		remoteCseEntity.setParentID(parentEntity.getResourceID());
		remoteCseEntity.setResourceType(ResourceType.REMOTE_CSE);

		if(newOriginator){
			AccessControlPolicyEntity acpEntity = new AccessControlPolicyEntity();
			acpEntity.setCreationTime(DateUtil.now());
			acpEntity.setLastModifiedTime(DateUtil.now());
			acpEntity.setParentID("/" + Constants.CSE_ID);
			acpEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.ACP + Constants.PREFIX_SEPERATOR + generateId());
			acpEntity.setName(ShortName.ACP + ShortName.REMOTE_CSE + Constants.PREFIX_SEPERATOR + generatedId);
			AccessControlRuleEntity ruleEntity = new AccessControlRuleEntity();
			AccessControlOriginatorEntity originatorEntity = new AccessControlOriginatorEntity(Constants.ADMIN_REQUESTING_ENTITY);
			ruleEntity.getAccessControlOriginators().add(originatorEntity);
			ruleEntity.setCreate(true);
			ruleEntity.setRetrieve(true);
			ruleEntity.setUpdate(true);
			ruleEntity.setDelete(true);
			ruleEntity.setNotify(true);
			ruleEntity.setDiscovery(true);
			acpEntity.getSelfPrivileges().add(ruleEntity);
			// Privileges
			ruleEntity = new AccessControlRuleEntity();
			ruleEntity.setCreate(true);
			ruleEntity.setRetrieve(true);
			ruleEntity.setUpdate(true);
			ruleEntity.setDelete(true);
			ruleEntity.setNotify(true);
			ruleEntity.setDiscovery(true);
			ruleEntity.getAccessControlOriginators().add(new AccessControlOriginatorEntity(request.getFrom()));
			ruleEntity.getAccessControlOriginators().add(new AccessControlOriginatorEntity(Constants.ADMIN_REQUESTING_ENTITY));
			acpEntity.getPrivileges().add(ruleEntity);
			acpEntity.setHierarchicalURI("/" + Constants.CSE_ID + "/" + acpEntity.getName());
			UriMapper.addNewUri(acpEntity.getHierarchicalURI(), acpEntity.getResourceID(), ResourceType.ACCESS_CONTROL_POLICY);
			dbs.getDAOFactory().getAccessControlPolicyDAO().create(transaction, acpEntity);

			AccessControlPolicyEntity acpDB = dbs.getDAOFactory().getAccessControlPolicyDAO().find(transaction, acpEntity.getResourceID());
			CSEBaseEntity cseBase = dbs.getDAOFactory().getCSEBaseDAO().find(transaction, "/" + Constants.CSE_ID);
			cseBase.getChildAccessControlPolicies().add(acpDB);
			dbs.getDAOFactory().getCSEBaseDAO().update(transaction, cseBase);

			remoteCseEntity.getAccessControlPolicies().add(acpDB);
			remoteCseEntity.setGeneratedAcp(acpDB);
		}

		// Create remoteCSE in database
		dbs.getDAOFactory().getRemoteCSEDAO().create(transaction, remoteCseEntity);

		// Get the managed object from db
		RemoteCSEEntity csrDB = dbs.getDAOFactory().getRemoteCSEDAO().find(transaction, remoteCseEntity.getResourceID());

		// Add the remoteCSE to the CSEBase list
		remoteCSEs.add(csrDB);
		dao.update(transaction, parentEntity);

		// Commit the DB transaction
		transaction.commit();

		Notifier.notify(subscriptions, csrDB, ResourceStatus.CHILD_CREATED);
		// Create the response
		response.setResponseStatusCode(ResponseStatusCode.CREATED);
		// Set the location of the resource
		setLocationAndCreationContent(request, response, csrDB);

		return response;
	}

	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		// Creating the response primitive
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Check existence of the resource
		RemoteCSEEntity csrEntity = dbs.getDAOFactory().getRemoteCSEDAO().find(transaction, request.getTargetId());
		if (csrEntity == null) {
			throw new ResourceNotFoundException();
		}

		// if resource exists, check authorization
		// retrieve 
		List<AccessControlPolicyEntity> acpList = csrEntity.getAccessControlPolicies();
		checkACP(acpList, request.getFrom(), request.getOperation());
		
		// Mapping the entity with the exchange resource
		RemoteCSE csr = EntityMapperFactory.getRemoteCseMapper().mapEntityToResource(csrEntity, request);
		response.setContent(csr);
		response.setResponseStatusCode(ResponseStatusCode.OK);
		// return the response
		return response;
	}

	/**
	 * Implement the full update method for remoteCSE resource
	 * @param request
	 * @return response
	 */
	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		/*
		 * remoteCSE update procedure
		 * 
		 * @resourceName			NP
		 * resourceType				NP
		 * resourceID				NP
		 * parentID					NP
		 * accessControlPolicyIDs	O
		 * creationTime				NP
		 * expirationTime			O
		 * lastModifiedTime			NP
		 * labels					NP
		 * announceTo				O
		 * announcedAttribute		O
		 * 
		 * cseType					NP
		 * poa						O
		 * CseBase					NP
		 * CSE-ID					NP
		 * M2M-EXT-ID				O
		 * Trigger-Recipient-ID		O
		 * requestReachability		O
		 * nodeLink					NP
		 */
		// create the response base
		ResponsePrimitive response = new ResponsePrimitive(request);
		// get the persistence service
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();

		// retrieve the resource from the DB
		RemoteCSEEntity csrEntity = dbs.getDAOFactory().getRemoteCSEDAO().find(transaction, request.getTargetId());
		if (csrEntity == null) {
			throw new ResourceNotFoundException();
		}

		// check ACP
		checkACP(csrEntity.getAccessControlPolicies(), request.getFrom(), Operation.UPDATE);
		

		// check if content is present
		if (request.getContent() == null) {
			throw new BadRequestException("A content is requiered for RemoteCSE Update");
		}

		// create the java object from the resource representation
		RemoteCSE csr = null;
		try{
			if (request.getRequestContentType().equals(MimeMediaType.OBJ)){
				csr = (RemoteCSE) request.getContent();
			} else {
				csr = (RemoteCSE)DataMapperSelector.getDataMapperList()
						.get(request.getRequestContentType()).stringToObj((String)request.getContent());				
			}

		} catch (ClassCastException e){
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if (csr == null){
			throw new BadRequestException("Error in provided content");
		}

		// check attributes, NP attributes are ignored

		// @resourceName			NP
		// resourceType				NP
		// resourceID				NP
		// parentID					NP
		// creationTime				NP
		// lastModifiedTime			NP
		UpdateUtil.checkNotPermittedParameters(csr);
		// cseType					NP
		if(csr.getCseType() != null){
			throw new BadRequestException("CseType is NP");
		}
		// CseBase					NP
		if(csr.getCSEBase() != null){
			throw new BadRequestException("CseBase is NP");
		}
		// CSE-ID					NP
		if(csr.getCSEID() != null){
			throw new BadRequestException("CseID is NP");
		}
		// nodeLink					NP
		if(csr.getNodeLink() != null){
			throw new BadRequestException("NodeLink is NP");
		}

		RemoteCSE modifiedAttributes = new RemoteCSE();
		// labels					O
		if(!csr.getLabels().isEmpty()){
			csrEntity.setLabelsEntitiesFromSring(csr.getLabels());
			modifiedAttributes.getLabels().addAll(csr.getLabels());
		}
		
		// accessControlPolicyIDs		O
		if (!csr.getAccessControlPolicyIDs().isEmpty()){		
			for(AccessControlPolicyEntity acpe : csrEntity.getAccessControlPolicies()){
				checkSelfACP(acpe, request.getFrom(), Operation.UPDATE);
			}
			csrEntity.setAccessControlPolicies(
					ControllerUtil.buildAcpEntityList(csr.getAccessControlPolicyIDs(), transaction));
			modifiedAttributes.getAccessControlPolicyIDs().addAll(csr.getAccessControlPolicyIDs());
		}
		
		// expirationTime			O
		if (csr.getExpirationTime() != null){
			csrEntity.setExpirationTime(csr.getExpirationTime());
			modifiedAttributes.setExpirationTime(csr.getExpirationTime());
		}
		// announceTo				O
		if(!csr.getAnnounceTo().isEmpty()){
			// TODO Announcement in AE update
			csrEntity.getAnnounceTo().clear();
			csrEntity.getAnnounceTo().addAll(csr.getAnnounceTo());
			modifiedAttributes.getAnnounceTo().addAll(csr.getAnnounceTo());
		}
		// announcedAttribute			O
		if(!csr.getAnnouncedAttribute().isEmpty()){
			csrEntity.getAnnouncedAttribute().clear();
			csrEntity.getAnnouncedAttribute().addAll(csr.getAnnouncedAttribute());
			modifiedAttributes.getAnnouncedAttribute().addAll(csr.getAnnouncedAttribute());
		}
		// poa						O
		if (!csr.getPointOfAccess().isEmpty()) {
			csrEntity.getPointOfAccess().clear();
			csrEntity.getPointOfAccess().addAll(csr.getPointOfAccess());
			modifiedAttributes.getPointOfAccess().addAll(csr.getPointOfAccess());
		}
		// M2M-EXT-ID				O
		if (csr.getM2MExtID() != null) {
			csrEntity.setM2mExtId(csr.getM2MExtID());
			modifiedAttributes.setM2MExtID(csr.getM2MExtID());
		}
		// Trigger-Recipient-ID		O
		if (csr.getTriggerRecipientID() != null) {
			csrEntity.setTriggerRecipientID(csr.getTriggerRecipientID());
			modifiedAttributes.setTriggerRecipientID(csr.getTriggerRecipientID());
		}
		// requestReachability		O
		if (csr.isRequestReachability() != null) {
			csrEntity.setRequestReachability(csr.isRequestReachability());
			modifiedAttributes.setRequestReachability(csr.isRequestReachability());
		}

		csrEntity.setLastModifiedTime(DateUtil.now());
		modifiedAttributes.setLastModifiedTime(csrEntity.getLastModifiedTime());
		response.setContent(modifiedAttributes);
		
		// update the resource in the database
		dbs.getDAOFactory().getRemoteCSEDAO().update(transaction, csrEntity);
		transaction.commit();
		Notifier.notify(csrEntity.getSubscriptions(), csrEntity, ResourceStatus.UPDATED);

		// set response status code
		response.setResponseStatusCode(ResponseStatusCode.UPDATED);
		return response;
	}

	@Override
	/**
	 * Delete the remoteCSE if access control policies are correct
	 */
	public ResponsePrimitive doDelete(RequestPrimitive request) {
		// Generic delete procedure
		ResponsePrimitive response = new ResponsePrimitive(request);

		// retrieve the corresponding resource from database
		RemoteCSEEntity csrEntity = dbs.getDAOFactory().getRemoteCSEDAO().find(transaction, request.getTargetId());
		if (csrEntity == null) {
			throw new ResourceNotFoundException();
		}

		// check access control policies
		checkACP(csrEntity.getAccessControlPolicies(), request.getFrom(), Operation.DELETE);

		UriMapper.deleteUri(csrEntity.getHierarchicalURI());
		Notifier.notifyDeletion(csrEntity.getSubscriptions(), csrEntity);

		// delete the resource in the database
		dbs.getDAOFactory().getRemoteCSEDAO().delete(transaction, csrEntity);
		// commit the transaction
		transaction.commit();
		// return the response
		response.setResponseStatusCode(ResponseStatusCode.DELETED);
		return response;
	}

}
