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
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AccessControlRuleEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.ConflictException;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.AccessControlPolicy;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.utils.AcpUtils;
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
 * Controller for Access Control policy 
 *
 */
public class AccessControlPolicyController extends Controller {

	/*
	 * Generic create procedure 
	 * 
	 * 						Req
	 * @resourceName 		NP 
	 * resourceType 		NP 
	 * resourceID 			NP 
	 * parentID 			NP 
	 * expirationTime 		O 
	 * labels 				O 
	 * creationTime 		NP 
	 * lastModifiedTime 	NP 
	 * announceTo 			O
	 * announcedAttribute 	O 
	 * privileges 			M 
	 * selfPrivileges 		M
	 */

	@Override
	public ResponsePrimitive doCreate(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Get the DAO of the parent
		DAO<ResourceEntity> dao = (DAO<ResourceEntity>) Patterns.getDAO(request.getTargetId(), dbs);
		if (dao == null){
			throw new ResourceNotFoundException("Cannot find parent resource");
		}
		// Get the parent entity
		ResourceEntity parentEntity = (ResourceEntity)dao.find(transaction, request.getTargetId());
		// Check the parent existence
		if (parentEntity == null){
			throw new ResourceNotFoundException("Cannot find parent resource");
		}

		// Get lists to change in the method corresponding to specific object
		List<AccessControlPolicyEntity> acpsToCheck = null;
		List<AccessControlPolicyEntity> childAcps = null;
		List<SubscriptionEntity> subscriptions = null;
		// Case of CSEBase parent
		if (parentEntity.getResourceType().intValue() == (ResourceType.CSE_BASE)){
			CSEBaseEntity cseBase= (CSEBaseEntity) parentEntity ;
			acpsToCheck = cseBase.getAccessControlPolicies();
			childAcps = cseBase.getChildAccessControlPolicies();
			subscriptions = cseBase.getSubscriptions();
		}
		// Case of AE parent
		if (parentEntity.getResourceType().intValue() == (ResourceType.AE)){
			AeEntity ae = (AeEntity) parentEntity;
			acpsToCheck = ae.getAccessControlPolicies();
			childAcps = ae.getChildAccessControlPolicies();
			subscriptions = ae.getSubscriptions();
		}
		// case of Remote CSE 
		if (parentEntity.getResourceType().intValue() == (ResourceType.REMOTE_CSE)) {
			RemoteCSEEntity csr = (RemoteCSEEntity) parentEntity;
			acpsToCheck = csr.getAccessControlPolicies();
			childAcps = csr.getChildAcps();
			subscriptions = csr.getSubscriptions();
		}
		// TODO complete list of parents

		// Check access control policy of the originator
		checkACP(acpsToCheck, request.getFrom(), Operation.CREATE);
		
		// Check if content is present
		if (request.getContent() == null){
			throw new BadRequestException("A content is requiered for AccessControlPolicy creation");
		}

		// Get the java object from the representation
		AccessControlPolicy acp = null;
		try{
			if (request.getRequestContentType().equals(MimeMediaType.OBJ)){
				acp = (AccessControlPolicy) request.getContent();
			} else {
				acp = (AccessControlPolicy)DataMapperSelector.getDataMapperList()
						.get(request.getRequestContentType()).stringToObj((String)request.getContent());								
			}
		} catch (ClassCastException e){
			LOGGER.debug("ClassCastException: Incorrect resource type in object conversion.",e);
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if (acp == null){
			throw new BadRequestException("Error in provided content");
		}

		AccessControlPolicyEntity acpEntity = new AccessControlPolicyEntity();
		// Check attributes
		// @resourceName 		NP 
		// Resource Type 		NP
		// resourceID 			NP
		// parentID 			NP
		// lastModifiedTime 	NP
		// creationTime 		NP
		// labels				O
		ControllerUtil.CreateUtil.fillEntityFromGenericResource(acp, acpEntity);
		
		// privileges 			M
		if (acp.getPrivileges() == null){
			throw new BadRequestException("Prilileges is Mandatory");
		}
		// selfPrivileges 		M
		if (acp.getSelfPrivileges() == null){
			throw new BadRequestException("SelfPrivileges is Mandatory");
		}
		
		String generatedId = generateId();
		// Creating the corresponding entity
		if (acp.getName() != null){
			if (!Patterns.checkResourceName(acp.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			acpEntity.setName(acp.getName());
		} else 
		if (request.getName() != null){
			if (!Patterns.checkResourceName(request.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			acpEntity.setName(request.getName());			
		} else {
			acpEntity.setName(ShortName.ACP + "_" + generatedId);			
		}
		acpEntity.setHierarchicalURI(parentEntity.getHierarchicalURI() + "/" + acpEntity.getName());
		acpEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.ACP + Constants.PREFIX_SEPERATOR + generatedId);

		if(!UriMapper.addNewUri(acpEntity.getHierarchicalURI(), acpEntity.getResourceID(), ResourceType.ACCESS_CONTROL_POLICY)){
			throw new ConflictException("Name already present in the parent collection.");
		}

		acpEntity.setCreationTime(DateUtil.now());
		acpEntity.setLastModifiedTime(DateUtil.now());
		acpEntity.setParentID(parentEntity.getResourceID());
		acpEntity.setResourceType(ResourceType.ACCESS_CONTROL_POLICY);
		// expirationTime 		O
		if (acp.getExpirationTime() != null){
			acpEntity.setExpirationTime(acp.getExpirationTime());
		}
		// announceTo 			O
		acpEntity.getAnnounceTo().addAll(acp.getAnnounceTo());
		// announcedAttribute 	O 
		acpEntity.getAnnouncedAttribute().addAll(acp.getAnnouncedAttribute());
		acpEntity.setPrivileges(AcpUtils.getACREntityFromSetOfArcs(acp.getPrivileges()));
		acpEntity.setSelfPrivileges(AcpUtils.getACREntityFromSetOfArcs(acp.getSelfPrivileges()));

		// Create ACP in database
		dbs.getDAOFactory().getAccessControlPolicyDAO().create(transaction, acpEntity);

		// Get the managed object from db
		AccessControlPolicyEntity acpDB = dbs.getDAOFactory().getAccessControlPolicyDAO().find(transaction, acpEntity.getResourceID());

		// Add the ACP to the parentEntity list
		childAcps.add(acpDB);
		dao.update(transaction, parentEntity);

		// Commit the DB transaction
		transaction.commit();

		Notifier.notify(subscriptions, acpDB, ResourceStatus.CHILD_CREATED);
		
		// Create the response 
		response.setResponseStatusCode(ResponseStatusCode.CREATED);
		// Set the location of the resource
		setLocationAndCreationContent(request, response, acpDB);

		return response;
	}

	/*
	 * Generic retrieve procedure
	 */
	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Check existence of the resource
		AccessControlPolicyEntity acpEntity = dbs.getDAOFactory()
				.getAccessControlPolicyDAO().find(transaction, request.getTargetId());
		if (acpEntity == null) {
			throw new ResourceNotFoundException("Resource " + request.getTargetId() + " not found.");
		}

		// Check authorization
		checkSelfACP(acpEntity, request.getFrom(),
				Operation.RETRIEVE);

		// Create the object used to create the representation of the resource
		AccessControlPolicy acpResource = EntityMapperFactory.getAcpMapper().mapEntityToResource(acpEntity, request);
		response.setContent(acpResource);

		response.setResponseStatusCode(ResponseStatusCode.OK);
		return response;
	}

	/*
	 * Generic update procedure 
	 * 
	 * 						Req
	 * @resourceName 		NP 
	 * resourceType 		NP 
	 * resourceID 			NP 
	 * parentID 			NP 
	 * expirationTime 		O 
	 * labels 				O 
	 * creationTime 		NP 
	 * lastModifiedTime 	NP 
	 * announceTo 			O
	 * announcedAttribute 	O 
	 * privileges 			O 
	 * selfPrivileges 		O
	 */
	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);
		
		// Retrieve the resource from DB
		AccessControlPolicyEntity acpEntity = dbs.getDAOFactory().
				getAccessControlPolicyDAO().find(transaction, request.getTargetId());

		// Check resource existence
		if (acpEntity == null){
			throw new ResourceNotFoundException("Resource " + request.getTargetId() + " not found.");
		}

		// Check self ACP
		checkSelfACP(acpEntity, request.getFrom(), Operation.UPDATE);
		

		// Check if content is present
		if (request.getContent() == null){
			throw new BadRequestException("A content is requiered for ACP update");
		}

		// Create the java object from the resource representation
		AccessControlPolicy acp = null;
		try{
			if (request.getRequestContentType().equals(MimeMediaType.OBJ)){
				acp = (AccessControlPolicy) request.getContent();
			} else {
				acp = (AccessControlPolicy)DataMapperSelector.getDataMapperList()
						.get(request.getRequestContentType()).stringToObj((String)request.getContent());
			}
		} catch (ClassCastException e){
			LOGGER.debug("ClassCastException: Incorrect resource type in object conversion.",e);
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if (acp == null){
			throw new BadRequestException("Error in provided content");
		}
		
		AccessControlPolicy modifiedAttributes = new AccessControlPolicy();

		// NP attributes 
		// @resourceName 		NP 
		// resourceType 		NP 
		// resourceID 			NP 
		// parentID 			NP
		// creationTime 		NP 
		// lastModifiedTime 	NP 
		UpdateUtil.checkNotPermittedParameters(acp);
		// expirationTime 		O
		if(acp.getExpirationTime() != null){
			acpEntity.setExpirationTime(acp.getExpirationTime());
			modifiedAttributes.setExpirationTime(acp.getExpirationTime());
		}
		// labels 				O 
		if(!acp.getLabels().isEmpty()){
			acpEntity.setLabelsEntitiesFromSring(acp.getLabels());
			modifiedAttributes.getLabels().addAll(acp.getLabels());
		}
		// announceTo 			O
		if(!acp.getAnnounceTo().isEmpty()){
			acpEntity.getAnnounceTo().clear();
			acpEntity.getAnnounceTo().addAll(acp.getAnnounceTo());
			// TODO Announcement in ACP update
			modifiedAttributes.getAnnounceTo().addAll(acp.getAnnounceTo());
		}
		// announcedAttribute 	O 
		if(!acp.getAnnouncedAttribute().isEmpty()){
			acpEntity.getAnnouncedAttribute().clear();
			acpEntity.getAnnouncedAttribute().addAll(acp.getAnnouncedAttribute());
			modifiedAttributes.getAnnouncedAttribute().addAll(acp.getAnnouncedAttribute());
		}
		// privileges 			O 
		if(acp.getPrivileges() != null){
			List<AccessControlRuleEntity> rules = AcpUtils.getACREntityFromSetOfArcs(acp.getPrivileges());
			acpEntity.getPrivileges().clear();
			acpEntity.getPrivileges().addAll(rules);
			modifiedAttributes.setPrivileges(acp.getPrivileges());
		}
		// selfPrivileges 		O
		if(acp.getSelfPrivileges() != null){
			List<AccessControlRuleEntity> rules = AcpUtils.getACREntityFromSetOfArcs(acp.getSelfPrivileges());
			acpEntity.getSelfPrivileges().clear();
			acpEntity.getSelfPrivileges().addAll(rules);
			modifiedAttributes.setSelfPrivileges(acp.getSelfPrivileges());
		}
		acpEntity.setLastModifiedTime(DateUtil.now());
		modifiedAttributes.setLastModifiedTime(acpEntity.getLastModifiedTime());
		response.setContent(modifiedAttributes);
		dbs.getDAOFactory().getAccessControlPolicyDAO().update(transaction, acpEntity);
		transaction.commit();

		Notifier.notify(acpEntity.getChildSubscriptions(), acpEntity, ResourceStatus.UPDATED);
		
		response.setResponseStatusCode(ResponseStatusCode.UPDATED);
		return response;
	}

	/*
	 * Generic Delete procedure
	 */
	@Override
	public ResponsePrimitive doDelete(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Get the database service & initialize the transaction
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();

		// Retrieve the resource from database
		AccessControlPolicyEntity acpEntity = dbs.getDAOFactory()
				.getAccessControlPolicyDAO().find(transaction, request.getTargetId());

		// Check resource existence
		if (acpEntity == null){
			throw new ResourceNotFoundException("Resource not found");
		}
		
		// Check this acp is not a generated acp for an AE to avoid inconsistency
		for(AeEntity ae : acpEntity.getLinkedAes()){
			if(ae.getGeneratedAcp().getResourceID().equals(acpEntity.getResourceID())){
				throw new BadRequestException("Delete the linked ae(s) to avoid acp inconsistency.");
			}
		}
		
		// Check self access control policy
		checkSelfACP(acpEntity, request.getFrom(), Operation.DELETE);

		// TODO Notify doDelete
		// TODO De-announce doDelete

		UriMapper.deleteUri(acpEntity.getHierarchicalURI());

		Notifier.notifyDeletion(acpEntity.getChildSubscriptions(), acpEntity);
		
		// Delete the resource
		dbs.getDAOFactory().getAccessControlPolicyDAO().delete(transaction, acpEntity);
		
		transaction.commit();
		
		// Close transaction and return
		response.setResponseStatusCode(ResponseStatusCode.DELETED);
		return response;
	}

}
