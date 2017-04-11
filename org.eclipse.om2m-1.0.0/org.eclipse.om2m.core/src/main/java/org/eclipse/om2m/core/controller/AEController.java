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
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.ConflictException;
import org.eclipse.om2m.commons.exceptions.NotImplementedException;
import org.eclipse.om2m.commons.exceptions.NotPermittedAttrException;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.AE;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.utils.Util.DateUtil;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;
import org.eclipse.om2m.core.entitymapper.EntityMapperFactory;
import org.eclipse.om2m.core.notifier.Notifier;
import org.eclipse.om2m.core.router.Patterns;
import org.eclipse.om2m.core.urimapper.UriMapper;
import org.eclipse.om2m.core.util.ControllerUtil;
import org.eclipse.om2m.core.util.ControllerUtil.UpdateUtil;
import org.eclipse.om2m.persistence.service.DAO;

/**
 * Controller for Application Entity
 *
 */
public class AEController extends Controller {

	@Override
	public ResponsePrimitive doCreate(RequestPrimitive request) {
		/*
		 * AE Creation procedure
		 * 							Req
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
		 * appName					O
		 * app-ID					M
		 * ae-ID					NP
		 * pointOfAccess			O
		 * ontologyRef				O
		 * nodeLink					NP
		 */

		ResponsePrimitive response = new ResponsePrimitive(request);

		// Get the DAO of the parent
		DAO<ResourceEntity> dao = (DAO<ResourceEntity>) Patterns.getDAO(request.getTargetId(), dbs);
		if (dao == null){
			throw new ResourceNotFoundException("Cannot find parent resource");
		}

		// Get the parent entity
		ResourceEntity parentEntity = (ResourceEntity) dao.find(transaction, request.getTargetId());
		// Check the parent existence
		if (parentEntity == null){
			throw new ResourceNotFoundException("Cannot find parent resource");
		}

		// Get lists to change in the method corresponding to specific object
		List<AccessControlPolicyEntity> acpsToCheck = null;
		List<AeEntity> childAes = null;
		List<SubscriptionEntity> subs = null;

		// Distinguish parents
		// Case of CSEBase
		if(parentEntity.getResourceType().intValue() == (ResourceType.CSE_BASE)){
			CSEBaseEntity cseBase = (CSEBaseEntity) parentEntity;
			acpsToCheck = cseBase.getAccessControlPolicies();
			childAes = cseBase.getAes();
			subs = cseBase.getSubscriptions();
		}
		// Case of remoteCSE
		if(parentEntity.getResourceType().intValue() == (ResourceType.REMOTE_CSE)){
			RemoteCSEEntity csr = (RemoteCSEEntity) parentEntity;
			acpsToCheck = csr.getAccessControlPolicies();
			childAes = csr.getChildAes();
			subs = csr.getSubscriptions();
		}
		// Case of remoteCSEAnnc
		if(parentEntity.getResourceType().intValue() == (ResourceType.REMOTE_CSE_ANNC)){
			// TODO remoteCSEAnnc AE parent
			throw new NotImplementedException("remote cse announcer not implemented");
		}
		// criteria to generate new AE-ID
		boolean assignAeiC = true;

		if (request.getFrom() != null){
			if (request.getFrom().startsWith("S")) {
				throw new NotImplementedException("originator starting with S not implemented yet");
			} else if (request.getFrom().startsWith("C")) {
				assignAeiC = false;
			} else if(!request.getFrom().equals("")){
				// Check access control policy of the originator
				checkACP(acpsToCheck, request.getFrom(), Operation.CREATE);
			}
		}

		// Check if content is present
		if (request.getContent() == null){
			throw new BadRequestException("A content is requiered for AE creation");
		}

		// Get the java object from the representation
		AE ae = null;
		try{
			if (request.getRequestContentType().equals(MimeMediaType.OBJ)){
				ae = (AE) request.getContent();
			} else {
				ae = (AE)DataMapperSelector.getDataMapperList()
						.get(request.getRequestContentType()).stringToObj((String)request.getContent());
			}	
		} catch (ClassCastException e){
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if (ae == null){
			throw new BadRequestException("Error in provided content");
		}

		// Check attributes
		// @resourceName			NP
		// resourceType				NP
		// resourceID				NP
		// parentID					NP
		// creationTime				NP
		// lastModifiedTime			NP
		// expirationTime			O
		// labels					O
		// announceTo				O
		// announcedAttribute		O
		// Creating the corresponding entity
		AeEntity aeEntity = new AeEntity();
		ControllerUtil.CreateUtil.fillEntityFromAnnounceableResource(ae, aeEntity);

		// ae-ID					NP
		if (ae.getAEID() != null){
			throw new NotPermittedAttrException("ae-id is Not Permitted");
		}
		// nodeLink					NP
		if (ae.getNodeLink() != null){
			aeEntity.setNodeLink(ae.getNodeLink());
		}

		// app-ID					M
		if (ae.getAppID() == null){
			throw new BadRequestException("App ID is Mandatory");
		} else {
			aeEntity.setAppID(ae.getAppID());
		}

		// requestReachability		M
		if (ae.getRequestReachability() == null) {
			throw new BadRequestException("Request Reachability is Mandatory");
		} else {
			aeEntity.setRequestReachability(ae.getRequestReachability());
		}

		String generatedId = generateId();
		// setting the AE-ID
		if (assignAeiC) {
			aeEntity.setAeid("C" + ShortName.AE.toUpperCase() + generatedId);
		} else {
			aeEntity.setAeid(request.getFrom());
		}
		// Set other parameters
		aeEntity.setResourceID("/" + Constants.CSE_ID + "/" + aeEntity.getAeid());
		if (dbs.getDAOFactory().getAeDAO().find(transaction, aeEntity.getResourceID()) != null) {
			throw new ConflictException("Already registered");
		}

		// accessControlPolicyIDs	O
		if (!ae.getAccessControlPolicyIDs().isEmpty()){
			aeEntity.setAccessControlPolicies(
					ControllerUtil.buildAcpEntityList(ae.getAccessControlPolicyIDs(), transaction));
		}

		// FIXME [0001] Creation of AE with an acpi provided
		//		} else {
		// Create the acp corresponding to the AE_ID 
		AccessControlPolicyEntity acpEntity = new AccessControlPolicyEntity();
		acpEntity.setCreationTime(DateUtil.now());
		acpEntity.setLastModifiedTime(DateUtil.now());
		acpEntity.setParentID("/" + Constants.CSE_ID);
		acpEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.ACP + Constants.PREFIX_SEPERATOR + generateId());
		acpEntity.setName(ShortName.ACP + ShortName.AE + Constants.PREFIX_SEPERATOR + generatedId);
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
		ruleEntity.getAccessControlOriginators().add(new AccessControlOriginatorEntity(aeEntity.getAeid()));
		ruleEntity.getAccessControlOriginators().add(new AccessControlOriginatorEntity(Constants.ADMIN_REQUESTING_ENTITY));
		acpEntity.getPrivileges().add(ruleEntity);
		acpEntity.setHierarchicalURI("/" + Constants.CSE_ID + "/" + Constants.CSE_NAME + "/" + acpEntity.getName());
		// Add the acp in the UriMapper table
		UriMapper.addNewUri(acpEntity.getHierarchicalURI(), acpEntity.getResourceID(), ResourceType.ACCESS_CONTROL_POLICY);
		dbs.getDAOFactory().getAccessControlPolicyDAO().create(transaction, acpEntity);
		// Retrieve the acp in the database to make the link with the CSEBase resource
		AccessControlPolicyEntity acpDB = dbs.getDAOFactory().getAccessControlPolicyDAO().find(transaction, acpEntity.getResourceID());
		CSEBaseEntity cseBase = dbs.getDAOFactory().getCSEBaseDAO().find(transaction, "/" + Constants.CSE_ID);
		cseBase.getChildAccessControlPolicies().add(acpDB);
		dbs.getDAOFactory().getCSEBaseDAO().update(transaction, cseBase);
		// adding new acp to the acp list
		aeEntity.getAccessControlPolicies().add(acpDB);
		// direct link to the generated acp
		aeEntity.setGeneratedAcp(acpDB);
		//		}

		// appName					O
		if (ae.getAppName() != null){
			aeEntity.setAppName(ae.getAppName());
		}
		// pointOfAccess			O
		if (!ae.getPointOfAccess().isEmpty()){
			aeEntity.getPointOfAccess().addAll(ae.getPointOfAccess());
		}
		// ontologyRef				O
		if (ae.getOntologyRef() != null){
			aeEntity.setOntologyRef(ae.getOntologyRef());
		}

		aeEntity.setParentID(parentEntity.getResourceID());
		aeEntity.setResourceType(BigInteger.valueOf(ResourceType.AE));
		if (ae.getName() != null){
			if (!Patterns.checkResourceName(ae.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			aeEntity.setName(ae.getName());
		} else
			if (request.getName() != null){
				if (!Patterns.checkResourceName(request.getName())){
					throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
				}
				aeEntity.setName(request.getName());
			} else {
				aeEntity.setName(ShortName.AE + "_" + generatedId);
			}
		aeEntity.setHierarchicalURI(parentEntity.getHierarchicalURI() + "/" + aeEntity.getName());
		if (!UriMapper.addNewUri(aeEntity.getHierarchicalURI(), aeEntity.getResourceID(), ResourceType.AE)){
			throw new ConflictException("Name already present in the parent collection.");
		}

		// Create AE in database
		dbs.getDAOFactory().getAeDAO().create(transaction, aeEntity);

		// Get the managed object from db
		AeEntity aeDB = dbs.getDAOFactory().getAeDAO().find(transaction, aeEntity.getResourceID());

		// Add the AE to the parentEntity list
		childAes.add(aeDB);
		dao.update(transaction, parentEntity);

		// Commit the DB transaction
		transaction.commit();

		// Create the response
		response.setResponseStatusCode(ResponseStatusCode.CREATED);
		// Set the location of the resource
		setLocationAndCreationContent(request, response, aeDB);

		Notifier.notify(subs, aeDB, ResourceStatus.CHILD_CREATED);

		return response;
	}

	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		/*
		 * Generic retrieve procedure
		 */
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Check existence of the resource
		AeEntity aeEntity = dbs.getDAOFactory()
				.getAeDAO().find(transaction, request.getTargetId());
		if (aeEntity == null){
			throw new ResourceNotFoundException("Resource not found");
		}

		checkACP(aeEntity.getAccessControlPolicies(), request.getFrom(), 
				Operation.RETRIEVE);


		// Create the object used to create the representaiton of the resource
		AE ae = EntityMapperFactory.getAEMapper().mapEntityToResource(aeEntity, request);
		response.setContent(ae);

		response.setResponseStatusCode(ResponseStatusCode.OK);
		return response;
	}

	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		/*
		 * 							Req
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
		 * appName					O
		 * app-ID					NP
		 * ae-ID					NP
		 * pointOfAccess			O
		 * ontologyRef				O
		 * nodeLink					NP
		 * requestReachability		O
		 */
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Retrieve the resource from database
		AeEntity aeEntity = dbs.getDAOFactory()
				.getAeDAO().find(transaction, request.getTargetId());
		if (aeEntity == null){
			throw new ResourceNotFoundException("Resource not found");
		}

		checkACP(aeEntity.getAccessControlPolicies(), request.getFrom(), 
				Operation.UPDATE);


		// Check if content is present
		if (request.getContent() == null){
			throw new BadRequestException("A content is requiered for AE update");
		}

		// Create the java object from the resource representation
		AE ae = null;
		try{
			if (request.getRequestContentType().equals(MimeMediaType.OBJ)){
				ae = (AE) request.getContent();
			} else {
				ae = (AE)DataMapperSelector.getDataMapperList()
						.get(request.getRequestContentType()).stringToObj((String)request.getContent());
			}
		} catch (ClassCastException e){
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if (ae == null){
			throw new BadRequestException("Error in provided content");
		}

		// Check attributes

		// NP Attributes are ignored
		// @resourceName			NP
		// resourceType				NP
		// resourceID				NP
		// parentID					NP
		// creationTime				NP
		// lastModifiedTime			NP
		UpdateUtil.checkNotPermittedParameters(ae);
		// app-ID					NP
		if(ae.getAppID() != null){
			throw new BadRequestException("AppID is NP");
		}
		// ae-ID					NP
		if(ae.getAEID() != null){
			throw new BadRequestException("AE ID is NP");
		}
		// nodeLink					NP
		if(ae.getNodeLink() != null){
			throw new BadRequestException("NodeLink is NP");
		}

		AE modifiedAttributes = new AE();
		// labels					O
		if(!ae.getLabels().isEmpty()){
			aeEntity.setLabelsEntitiesFromSring(ae.getLabels());
			modifiedAttributes.getLabels().addAll(ae.getLabels());
		}
		// accessControlPolicyIDs	O
		if(!ae.getAccessControlPolicyIDs().isEmpty()){
			for(AccessControlPolicyEntity acpe : aeEntity.getAccessControlPolicies()){
				checkSelfACP(acpe, request.getFrom(), Operation.UPDATE);
			}
			aeEntity.getAccessControlPolicies().clear();
			aeEntity.setAccessControlPolicies(ControllerUtil.buildAcpEntityList(ae.getAccessControlPolicyIDs(), transaction));
			modifiedAttributes.getAccessControlPolicyIDs().addAll(ae.getAccessControlPolicyIDs());
		}
		// expirationTime			O
		if (ae.getExpirationTime() != null){
			aeEntity.setExpirationTime(ae.getExpirationTime());
			modifiedAttributes.setExpirationTime(ae.getExpirationTime());
		}
		// announceTo				O
		if(!ae.getAnnounceTo().isEmpty()){
			// TODO Announcement in AE update
			aeEntity.getAnnounceTo().clear();
			aeEntity.getAnnounceTo().addAll(ae.getAnnounceTo());
			modifiedAttributes.getAnnounceTo().addAll(ae.getAnnounceTo());
		}
		// announcedAttribute		O
		if(!ae.getAnnouncedAttribute().isEmpty()){
			aeEntity.getAnnouncedAttribute().clear();
			aeEntity.getAnnouncedAttribute().addAll(ae.getAnnouncedAttribute());
			modifiedAttributes.getAnnouncedAttribute().addAll(ae.getAnnouncedAttribute());
		}
		// appName					O
		if(ae.getAppName() != null){
			aeEntity.setAppName(ae.getAppName());
			modifiedAttributes.setAppName(ae.getAppName());
		}
		// pointOfAccess			O
		if(!ae.getPointOfAccess().isEmpty()){
			aeEntity.getPointOfAccess().clear();
			aeEntity.getPointOfAccess().addAll(ae.getPointOfAccess());
			modifiedAttributes.getPointOfAccess().addAll(ae.getPointOfAccess());
		}
		// ontologyRef				O
		if (ae.getOntologyRef() != null){
			aeEntity.setOntologyRef(ae.getOntologyRef());
			modifiedAttributes.setOntologyRef(ae.getOntologyRef());
		}
		// requestReachability		O
		if (ae.getRequestReachability() != null) {
			aeEntity.setRequestReachability(ae.getRequestReachability());
			modifiedAttributes.setRequestReachability(ae.getRequestReachability());
		}

		// Last Time Modified update
		aeEntity.setLastModifiedTime(DateUtil.now());
		modifiedAttributes.setLastModifiedTime(aeEntity.getLastModifiedTime());
		response.setContent(modifiedAttributes);
		// Update the resource in database
		dbs.getDAOFactory().getAeDAO().update(transaction, aeEntity);

		transaction.commit();

		Notifier.notify(aeEntity.getSubscriptions(), aeEntity, ResourceStatus.UPDATED);

		response.setResponseStatusCode(ResponseStatusCode.UPDATED);
		return response;
	}

	@Override
	public ResponsePrimitive doDelete(RequestPrimitive request) {
		/*
		 * Generic delete procedure
		 */
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Retrieve the resource from database
		AeEntity aeEntity = dbs.getDAOFactory()
				.getAeDAO().find(transaction, request.getTargetId());
		if (aeEntity == null){
			throw new ResourceNotFoundException("Resource not found");
		}

		checkACP(aeEntity.getAccessControlPolicies(), request.getFrom(), 
				Operation.DELETE);

		UriMapper.deleteUri(aeEntity.getHierarchicalURI());
		
		if(aeEntity.getGeneratedAcp() != null){
			UriMapper.deleteUri(aeEntity.getGeneratedAcp().getHierarchicalURI());
		}
		
		Notifier.notifyDeletion(aeEntity.getSubscriptions(), aeEntity);

		// Delete the resource
		dbs.getDAOFactory().getAeDAO().delete(transaction, aeEntity);

		// Commit the transaction
		transaction.commit();

		// Return rsc
		response.setResponseStatusCode(ResponseStatusCode.DELETED);
		return response;
	}

}
