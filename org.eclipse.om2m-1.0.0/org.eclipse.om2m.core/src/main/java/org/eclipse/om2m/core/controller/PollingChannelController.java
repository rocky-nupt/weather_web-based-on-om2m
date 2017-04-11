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
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.PollingChannelEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.ConflictException;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.PollingChannel;
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
 * Controller for polling channel
 *
 */
public class PollingChannelController extends Controller {

	@Override
	public ResponsePrimitive doCreate(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);

		DAO<ResourceEntity> dao = (DAO<ResourceEntity>) Patterns.getDAO(request.getTargetId(), dbs);
		if (dao == null){
			throw new ResourceNotFoundException("Cannot find parent resource");
		}

		// Get the parent entity
		LOGGER.info("Target ID in controller: " + request.getTargetId());
		ResourceEntity parentEntity = (ResourceEntity) dao.find(transaction, request.getTargetId());
		// Check the parent existence
		if (parentEntity == null){
			throw new ResourceNotFoundException("Cannot find parent resource");
		}

		List<PollingChannelEntity> childPollings = null;
		List<AccessControlPolicyEntity> acpsToCheck = null;
		String originatorToCheck = null;
		List<SubscriptionEntity> subscriptions = null;

		if(parentEntity.getResourceType().intValue() == ResourceType.AE){
			AeEntity ae = (AeEntity) parentEntity ;
			childPollings = ae.getPollingChannels();
			originatorToCheck = ae.getAeid();
			acpsToCheck = ae.getAccessControlPolicies();
			subscriptions = ae.getSubscriptions();
		}

		if(parentEntity.getResourceType().intValue() == ResourceType.REMOTE_CSE){
			RemoteCSEEntity remoteCse = (RemoteCSEEntity) parentEntity;
			childPollings = remoteCse.getPollingChannels();
			originatorToCheck = remoteCse.getRemoteCseId();
			acpsToCheck = remoteCse.getAccessControlPolicies();
			subscriptions = remoteCse.getSubscriptions();
		}

		if(request.getFrom() == null){
			response.setResponseStatusCode(ResponseStatusCode.ACCESS_DENIED);
			return response;
		}
		
		checkACP(acpsToCheck, request.getFrom(), Operation.CREATE);
		
		// Check if content is present
		if (request.getContent() == null){
			throw new BadRequestException("A content is requiered for PollingChannel creation");
		}

		// Get the java object from the representation
		PollingChannel pollingChannel = null;
		try{
			if (request.getRequestContentType().equals(MimeMediaType.OBJ)){
				pollingChannel = (PollingChannel) request.getContent();
			} else {
				pollingChannel = (PollingChannel)DataMapperSelector.getDataMapperList()
						.get(request.getRequestContentType()).stringToObj((String)request.getContent());
			}	
		} catch (ClassCastException e){
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if (pollingChannel == null){
			throw new BadRequestException("Error in provided content");
		}

		// Check attributes
		// @resourceName			NP
		// resourceType				NP
		// resourceID				NP
		// parentID					NP
		// creationTime				NP
		// lastModifiedTime			NP
		// labels					O
		PollingChannelEntity pollingChannelEntity = new PollingChannelEntity();
		ControllerUtil.CreateUtil.fillEntityFromGenericResource(pollingChannel, pollingChannelEntity);

		// expirationTime			O
		if(pollingChannel.getExpirationTime() != null){
			pollingChannelEntity.setExpirationTime(pollingChannel.getExpirationTime());
		}

		// acpIDs					O
		if (!pollingChannel.getAccessControlPolicyIDs().isEmpty()){
			pollingChannelEntity.setLinkedAcps(
					ControllerUtil.buildAcpEntityList(pollingChannel.getAccessControlPolicyIDs(), transaction));
		} else {
			pollingChannelEntity.getLinkedAcps().addAll(acpsToCheck);
		}
		
		String generatedId = generateId();
		pollingChannelEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.PCH + Constants.PREFIX_SEPERATOR + generatedId);
		if (pollingChannel.getName() != null){
			if (!Patterns.checkResourceName(pollingChannel.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			pollingChannelEntity.setName(pollingChannel.getName());
		} else 
		if(request.getName() != null){
			if(!Patterns.checkResourceName(request.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			pollingChannelEntity.setName(request.getName());
		} else {
			pollingChannelEntity.setName(ShortName.PCH + "_" + generatedId);
		}
		pollingChannelEntity.setHierarchicalURI(parentEntity.getHierarchicalURI() + "/" + pollingChannelEntity.getName());
		pollingChannelEntity.setParentID(parentEntity.getResourceID());
		pollingChannelEntity.setResourceType(ResourceType.POLLING_CHANNEL);
		pollingChannelEntity.setLastModifiedTime(DateUtil.now());
		pollingChannelEntity.setCreationTime(DateUtil.now());
		
		if(!UriMapper.addNewUri(pollingChannelEntity.getHierarchicalURI(), pollingChannelEntity.getResourceID(), ResourceType.POLLING_CHANNEL)){
			throw new ConflictException("Name already present in the parent collection.");
		}
		
		pollingChannelEntity.setPollingChannelUri("/" + Constants.CSE_ID + "/" + ShortName.POLLING_CHANNEL_URI + Constants.PREFIX_SEPERATOR + generatedId);
		// create the entity in the database
		dbs.getDAOFactory().getPollingChannelDAO().create(transaction, pollingChannelEntity);
		// get the entity from the db for the link with the parent
		PollingChannelEntity pollingChannelFromDB = dbs.getDAOFactory().getPollingChannelDAO().find(transaction, pollingChannelEntity.getResourceID());
		childPollings.add(pollingChannelFromDB);
		// Update the parent entity
		dao.update(transaction, parentEntity);
		// commit the db transaction
		transaction.commit();
		
		Notifier.notify(subscriptions, pollingChannelFromDB, ResourceStatus.CHILD_CREATED);
		
		// Set location and content if requested
		setLocationAndCreationContent(request, response, pollingChannelFromDB);
		response.setResponseStatusCode(ResponseStatusCode.CREATED);
		return response;
	}

	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		// Creating the response primitive
		ResponsePrimitive response = new ResponsePrimitive(request);
		
		// Check the existence of the resource
		PollingChannelEntity pollingChannelEntity = dbs.getDAOFactory().getPollingChannelDAO().find(transaction, request.getTargetId());
		if(pollingChannelEntity == null){
			throw new ResourceNotFoundException();
		}
		
		List<AccessControlPolicyEntity> acpList = pollingChannelEntity.getLinkedAcps();
		checkACP(acpList, request.getFrom(), Operation.RETRIEVE);
		
		PollingChannel pollingChannelResource = EntityMapperFactory.getPollingChannelMapper().mapEntityToResource(pollingChannelEntity, request);
		response.setContent(pollingChannelResource);
		
		response.setResponseStatusCode(ResponseStatusCode.OK);
		return response;
	}

	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		// create the response base
		ResponsePrimitive response = new ResponsePrimitive(request);
		
		// Check the existence of the resource
		PollingChannelEntity pollingChannelEntity = dbs.getDAOFactory().getPollingChannelDAO().find(transaction, request.getTargetId());
		if(pollingChannelEntity == null){
			throw new ResourceNotFoundException();
		}
		
		// check ACP
		checkACP(pollingChannelEntity.getLinkedAcps(), request.getFrom(), Operation.UPDATE);
		

		// check if content is present
		if (request.getContent() == null) {
			throw new BadRequestException("A content is requiered for AccessControlPolicy update");
		}
		
		// Get the java object from the representation
		PollingChannel pollingChannel = null;
		try{
			if (request.getRequestContentType().equals(MimeMediaType.OBJ)){
				pollingChannel = (PollingChannel) request.getContent();
			} else {
				pollingChannel = (PollingChannel)DataMapperSelector.getDataMapperList()
						.get(request.getRequestContentType()).stringToObj((String)request.getContent());
			}	
		} catch (ClassCastException e){
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if (pollingChannel == null){
			throw new BadRequestException("Error in provided content");
		}
		
		UpdateUtil.checkNotPermittedParameters(pollingChannel);
		
		PollingChannel modifiedAttributes = new PollingChannel();
		// expirationTime O
		if(pollingChannel.getExpirationTime() != null){
			pollingChannelEntity.setExpirationTime(pollingChannel.getExpirationTime());
			modifiedAttributes.setExpirationTime(pollingChannel.getExpirationTime());
		}
		
		// labels O
		if(!pollingChannel.getLabels().isEmpty()){
			pollingChannelEntity.setLabelsEntitiesFromSring(pollingChannel.getLabels());
			modifiedAttributes.getLabels().addAll(pollingChannel.getLabels());
		}
		pollingChannelEntity.setLastModifiedTime(DateUtil.now());
		modifiedAttributes.setLastModifiedTime(pollingChannelEntity.getLastModifiedTime());
		response.setContent(modifiedAttributes);
		
		dbs.getDAOFactory().getPollingChannelDAO().update(transaction, pollingChannelEntity);
		transaction.commit();
		
		response.setResponseStatusCode(ResponseStatusCode.UPDATED);
		return response;
	}

	@Override
	public ResponsePrimitive doDelete(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);
		
		// Check the existence of the resource
		PollingChannelEntity pollingChannelEntity = dbs.getDAOFactory().getPollingChannelDAO().find(transaction, request.getTargetId());
		if(pollingChannelEntity == null){
			throw new ResourceNotFoundException();
		}
		
		// check access control policies
		checkACP(pollingChannelEntity.getLinkedAcps(), request.getFrom(), Operation.DELETE);
		
		UriMapper.deleteUri(pollingChannelEntity.getHierarchicalURI());
		Notifier.notifyDeletion(null, pollingChannelEntity);
		
		// delete the resource
		dbs.getDAOFactory().getPollingChannelDAO().delete(transaction, pollingChannelEntity);
		// commit & close the transaction
		transaction.commit();
		
		//return the response
		response.setResponseStatusCode(ResponseStatusCode.DELETED);
		return response;
	}

}
