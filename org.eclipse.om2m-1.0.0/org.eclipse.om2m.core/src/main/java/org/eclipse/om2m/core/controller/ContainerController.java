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
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.ConflictException;
import org.eclipse.om2m.commons.exceptions.NotPermittedAttrException;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.Container;
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
 * Controller for the Container Resource
 *
 */
public class ContainerController extends Controller {

	/**
	 * Create the resource in the system according to the representation
	 */
	@Override
	public ResponsePrimitive doCreate(RequestPrimitive request) {
		/*
		 * Container creation procedure
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
		 * creator					O
		 * maxNrOfInstances			O
		 * maxByteSize				O
		 * maxInstanceAge			O
		 * currentNrOfInstances		NP
		 * currentByteSize			NP
		 * locationID				O
		 * ontologyRef				O
		 * 
		 */
		ResponsePrimitive response = new ResponsePrimitive(request);

		// get the dao of the parent
		DAO<ResourceEntity> dao = (DAO<ResourceEntity>) Patterns.getDAO(request.getTargetId(), dbs);
		if (dao == null){
			throw new ResourceNotFoundException("Cannot find parent resource");
		}

		// get the parent entity
		ResourceEntity parentEntity = (ResourceEntity)dao.find(transaction, request.getTargetId());
		// check the parent existence
		if (parentEntity == null) {
			throw new ResourceNotFoundException("Cannot find parent resource");
		}

		// get lists to change in the method corresponding to specific object
		List<AccessControlPolicyEntity> acpsToCheck = null;
		List<ContainerEntity> childContainers = null;
		List<SubscriptionEntity> subscriptions = null;

		// different cases
		// case parent is CSEBase
		if (parentEntity.getResourceType().intValue() == (ResourceType.CSE_BASE)) {
			CSEBaseEntity cseB = (CSEBaseEntity) parentEntity;
			acpsToCheck = cseB.getAccessControlPolicies();
			childContainers = cseB.getChildContainers();
			subscriptions = cseB.getSubscriptions();
		}
		// case parent is AE
		if (parentEntity.getResourceType().intValue() == (ResourceType.AE)) {
			AeEntity ae = (AeEntity) parentEntity;
			acpsToCheck = ae.getAccessControlPolicies();
			childContainers = ae.getChildContainers();
			subscriptions = ae.getSubscriptions();
		}
		// case parent is a Container
		if (parentEntity.getResourceType().intValue() == (ResourceType.CONTAINER)) {
			ContainerEntity parentContainer = (ContainerEntity) parentEntity;
			acpsToCheck = parentContainer.getAccessControlPolicies();
			childContainers = parentContainer.getChildContainers();
			subscriptions = parentContainer.getSubscriptions();
		}
		// case parent is a RemoteCSE
		if(parentEntity.getResourceType().intValue() == ResourceType.REMOTE_CSE){
			RemoteCSEEntity csr = (RemoteCSEEntity) parentEntity;
			acpsToCheck = csr.getAccessControlPolicies();
			childContainers = csr.getChildCnt();
			subscriptions = csr.getSubscriptions();
		}

		// check access control policy of the originator
		checkACP(acpsToCheck, request.getFrom(), Operation.CREATE);

		// check if content is present
		if (request.getContent() == null){
			throw new BadRequestException("A content is requiered for Container creation");
		}
		// get the object from the representation
		Container container = null;
		try{
			if (request.getRequestContentType().equals(MimeMediaType.OBJ)){
				container = (Container) request.getContent();
			} else {
				container = (Container)DataMapperSelector.getDataMapperList()
						.get(request.getRequestContentType()).stringToObj((String)request.getContent());				
			}

		} catch (ClassCastException e){
			LOGGER.debug("ClassCastException: Incorrect resource type in object conversion.",e);
			throw new BadRequestException("Incorrect resource representation in content", e);

		}
		if (container == null){			
			throw new BadRequestException("Error in provided content");
		}

		// creating the corresponding entity
		ContainerEntity containerEntity = new ContainerEntity();
		// check attributes
		// @resourceName 		NP 
		// Resource Type 		NP
		// resourceID 			NP
		// parentID 			NP
		// lastModifiedTime 	NP
		// creationTime 		NP
		// expiration time 			O
		// labels					O
		// announceTo				O
		// announcedAttribute		O

		ControllerUtil.CreateUtil.fillEntityFromAnnounceableResource(container, containerEntity);		

		// creator					O
		if (container.getCreator() != null) {
			containerEntity.setCreator(container.getCreator());
		}
		// currentNrOfInstances		NP
		if (container.getCurrentNrOfInstances() != null) {
			throw new NotPermittedAttrException("Current number of instances is Not Permitted");
		}
		// currentByteSize			NP
		if (container.getCurrentByteSize() != null) {
			throw new NotPermittedAttrException("Current byte size is Not Permitted");
		}

		String generatedId = generateId("", "");
		// set name if present and without any conflict
		if (container.getName() != null){
			if (!Patterns.checkResourceName(container.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			containerEntity.setName(container.getName());
		} else 
		if (request.getName() != null) {
			if(!Patterns.checkResourceName(request.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			containerEntity.setName(request.getName());
		} else {
			containerEntity.setName(ShortName.CNT + "_" + generatedId);
		}
		containerEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.CNT + Constants.PREFIX_SEPERATOR + generatedId);
		containerEntity.setHierarchicalURI(parentEntity.getHierarchicalURI() + "/" + containerEntity.getName());
		containerEntity.setParentID(parentEntity.getResourceID());
		containerEntity.setResourceType(ResourceType.CONTAINER);

		// accessControlPolicyIDs	O
		if (!container.getAccessControlPolicyIDs().isEmpty()){
			containerEntity.setAccessControlPolicies(
					ControllerUtil.buildAcpEntityList(container.getAccessControlPolicyIDs(), transaction));
		} else {
			containerEntity.getAccessControlPolicies().addAll(acpsToCheck);
		}

		if(!UriMapper.addNewUri(containerEntity.getHierarchicalURI(), containerEntity.getResourceID(), ResourceType.CONTAINER)){
			throw new ConflictException("Name already present in the parent collection.");
		}

		// ontologyRef				O
		if (container.getOntologyRef() != null){
			containerEntity.setOntologyRef(container.getOntologyRef());
		}
		// maxNrOfInstances			O
		if (container.getMaxNrOfInstances() != null) {
			containerEntity.setMaxNrOfInstances(container.getMaxNrOfInstances());
		} else {
			containerEntity.setMaxNrOfInstances(Constants.MAX_NBR_OF_INSTANCES);
		}
		// maxByteSize				O
		if (container.getMaxByteSize() != null) {
			containerEntity.setMaxByteSize(container.getMaxByteSize());
		} else {
			containerEntity.setMaxByteSize(Constants.MAX_BYTE_SIZE);
		}
		// maxInstanceAge			O
		if (container.getMaxInstanceAge() != null) {
			containerEntity.setMaxInstanceAge(container.getMaxInstanceAge());
		} else {
			containerEntity.setMaxInstanceAge(BigInteger.valueOf(0));
		}
		// locationID				O
		if (container.getLocationID() != null) {
			containerEntity.setLocationID(container.getLocationID());
		}
		
		// stateTag init
		containerEntity.setStateTag(BigInteger.valueOf(0));

		// create the container in the DB
		dbs.getDAOFactory().getContainerDAO().create(transaction, containerEntity);
		// retrieve the managed object from DB
		ContainerEntity containerFromDB = dbs.getDAOFactory().getContainerDAO().find(transaction, containerEntity.getResourceID());
		// add the container to the parentEntity child list
		childContainers.add(containerFromDB);
		dao.update(transaction, parentEntity);
		// commit the transaction
		transaction.commit();

		Notifier.notify(subscriptions, containerFromDB, ResourceStatus.CHILD_CREATED);

		// create the response
		response.setResponseStatusCode(ResponseStatusCode.CREATED);
		// set the location of the resource
		setLocationAndCreationContent(request, response, containerFromDB);
		return response;
	}


	/**
	 * Return the container resource with the normalized representation
	 * @param request primitive routed
	 * @return response primitive
	 */
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
		
		// Mapping the entity with the exchange resource
		Container containerResource = EntityMapperFactory.getContainerMapper().mapEntityToResource(containerEntity, request);
		response.setContent(containerResource);

		response.setResponseStatusCode(ResponseStatusCode.OK);
		// return the response
		return response;
	}

	/**
	 * Implement the full update method for container entity
	 * @param request
	 * @return
	 */
	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		/*
		 * Container update procedure
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
		 * creator					NP
		 * maxNrOfInstances			O
		 * maxByteSize				O
		 * maxInstanceAge			O
		 * currentNrOfInstances		NP
		 * currentByteSize			NP
		 * locationID				O
		 * ontologyRef				O
		 * 
		 */
		// create the response base
		ResponsePrimitive response = new ResponsePrimitive(request);

		// retrieve the resource from database
		ContainerEntity containerEntity = dbs.getDAOFactory().getContainerDAO().find(transaction, request.getTargetId());
		if (containerEntity == null) {
			throw new ResourceNotFoundException("Resource not found");
		}
		// check ACP
		checkACP(containerEntity.getAccessControlPolicies(), request.getFrom(), Operation.UPDATE);

		// check if content is present
		if (request.getContent() == null) {			
			throw new BadRequestException("A content is requiered for Container update");
		}

		// create the java object from the resource representation
		// get the object from the representation
		Container container = null;
		try{
			if (request.getRequestContentType().equals(MimeMediaType.OBJ)){
				container = (Container) request.getContent();
			} else {
				container = (Container)DataMapperSelector.getDataMapperList()
						.get(request.getRequestContentType()).stringToObj((String)request.getContent());				
			}

		} catch (ClassCastException e){
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if (container == null){
			throw new BadRequestException("Error in provided content");
		}

		// check attributes, NP attributes are ignored
		// @resourceName				NP
		// resourceType					NP
		// resourceID					NP
		// parentID						NP
		// creationTime					NP
		// creator						NP
		// lastModifiedTime				NP
		UpdateUtil.checkNotPermittedParameters(container);
		// currentNrOfInstances			NP
		if(container.getCurrentNrOfInstances() != null){
			throw new BadRequestException("CurrentNrOfInstances is NP");
		}
		// currentByteSize				NP
		if(container.getCurrentByteSize() != null){
			throw new BadRequestException("CurrentByteSize is NP");
		}
		
		Container modifiedAttributes = new Container();
		// accessControlPolicyIDs		O
		if(!container.getAccessControlPolicyIDs().isEmpty()){
			for(AccessControlPolicyEntity acpe : containerEntity.getAccessControlPolicies()){
				checkSelfACP(acpe, request.getFrom(), Operation.UPDATE);
			}
			containerEntity.getAccessControlPolicies().clear();
			containerEntity.setAccessControlPolicies(ControllerUtil.
					buildAcpEntityList(container.getAccessControlPolicyIDs(), transaction));
			modifiedAttributes.getAccessControlPolicyIDs().addAll(container.getAccessControlPolicyIDs());
		}
		// labels					O
		if (!container.getLabels().isEmpty()) {
			containerEntity.setLabelsEntitiesFromSring(container.getLabels());
			modifiedAttributes.getLabels().addAll(container.getLabels());
		}
		// expirationTime			O
		if (container.getExpirationTime() != null){
			containerEntity.setExpirationTime(container.getExpirationTime());
			modifiedAttributes.setExpirationTime(container.getExpirationTime());
		}
		// announceTo				O
		if(!container.getAnnounceTo().isEmpty()){
			// TODO Announcement in AE update
			containerEntity.getAnnounceTo().clear();
			containerEntity.getAnnounceTo().addAll(container.getAnnounceTo());
			modifiedAttributes.getAnnounceTo().addAll(container.getAnnounceTo());
		}
		// announcedAttribute			O
		if(!container.getAnnouncedAttribute().isEmpty()){
			containerEntity.getAnnouncedAttribute().clear();
			containerEntity.getAnnouncedAttribute().addAll(container.getAnnouncedAttribute());
			modifiedAttributes.getAnnouncedAttribute().addAll(container.getAnnouncedAttribute());
		}
		// maxNrOfInstances				O
		if(container.getMaxNrOfInstances() != null){
			containerEntity.setMaxNrOfInstances(container.getMaxNrOfInstances());
			modifiedAttributes.setMaxNrOfInstances(container.getMaxNrOfInstances());
		}
		// maxByteSize					O
		if(container.getMaxByteSize() != null){
			containerEntity.setMaxByteSize(container.getMaxByteSize());
			modifiedAttributes.setMaxByteSize(container.getMaxByteSize());
		}
		// maxInstanceAge				O
		if(container.getMaxInstanceAge() != null){
			containerEntity.setMaxInstanceAge(container.getMaxInstanceAge());
			modifiedAttributes.setMaxInstanceAge(container.getMaxInstanceAge());
		}
		// locationID					O
		if(container.getLocationID() != null){
			containerEntity.setLocationID(container.getLocationID());
			modifiedAttributes.setLocationID(container.getLocationID());
		}
		// ontologyRef					O
		if(container.getOntologyRef() != null){
			containerEntity.setOntologyRef(container.getOntologyRef());
			modifiedAttributes.setOntologyRef(container.getOntologyRef());
		}
		
		// Update state tag
		if(containerEntity.getStateTag() != null){
			containerEntity.setStateTag(BigInteger.valueOf(containerEntity.getStateTag().intValue() + 1));
			modifiedAttributes.setStateTag(containerEntity.getStateTag());			
		}
		
		containerEntity.setLastModifiedTime(DateUtil.now());
		modifiedAttributes.setLastModifiedTime(containerEntity.getLastModifiedTime());
		response.setContent(modifiedAttributes);
		// update the resource in the database
		dbs.getDAOFactory().getContainerDAO().update(transaction, containerEntity);
		transaction.commit();

		Notifier.notify(containerEntity.getSubscriptions(), containerEntity, ResourceStatus.UPDATED);

		// set response status code
		response.setResponseStatusCode(ResponseStatusCode.UPDATED);
		return response;
	}

	/**
	 * Delete the container if access control policies are correct
	 */
	@Override
	public ResponsePrimitive doDelete(RequestPrimitive request) {
		// Generic delete procedure
		ResponsePrimitive response = new ResponsePrimitive(request);

		// retrieve the corresponding resource from database
		ContainerEntity containerEntity = dbs.getDAOFactory().getContainerDAO().find(transaction, request.getTargetId());
		if (containerEntity == null) {
			throw new ResourceNotFoundException("Resource not found");
		}

		// check access control policies
		checkACP(containerEntity.getAccessControlPolicies(), request.getFrom(), Operation.DELETE);

		UriMapper.deleteUri(containerEntity.getHierarchicalURI());
		Notifier.notifyDeletion(containerEntity.getSubscriptions(), containerEntity);

		// delete the resource in the database
		dbs.getDAOFactory().getContainerDAO().delete(transaction, containerEntity);
		// commit the transaction
		transaction.commit();
		// return the response
		response.setResponseStatusCode(ResponseStatusCode.DELETED);
		return response;
	}

}
