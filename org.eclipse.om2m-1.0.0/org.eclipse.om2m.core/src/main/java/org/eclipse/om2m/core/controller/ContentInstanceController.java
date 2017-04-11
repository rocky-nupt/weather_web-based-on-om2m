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
import org.eclipse.om2m.commons.constants.ResourceStatus;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.entities.ContentInstanceEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.ConflictException;
import org.eclipse.om2m.commons.exceptions.NotImplementedException;
import org.eclipse.om2m.commons.exceptions.OperationNotAllowed;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;
import org.eclipse.om2m.core.entitymapper.EntityMapperFactory;
import org.eclipse.om2m.core.notifier.Notifier;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.router.Patterns;
import org.eclipse.om2m.core.urimapper.UriMapper;
import org.eclipse.om2m.core.util.ControllerUtil;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

/**
 * Controller for the content instance resource
 *
 */
public class ContentInstanceController extends Controller {

	/**
	 * do create operation for contentInstance
	 * @param request
	 * @return response
	 */
	@Override
	public ResponsePrimitive doCreate(RequestPrimitive request) {
		/*
		 * ContentInstance creation procedure
		 * 
		 * @resourceName			NP
		 * resourceType				NP
		 * resourceID				NP
		 * parentID					NP
		 * creationTime				NP
		 * expirationTime			O?
		 * lastModifiedTime			NP
		 * labels					O
		 * announceTo				O
		 * announcedAttribute		NP
		 * 
		 * creator					O
		 * contentInfo				M
		 * contentSize				O
		 * ontologyRef				O
		 * content					M
		 * 
		 */
		// create the response
		ResponsePrimitive response = new ResponsePrimitive(request);

		// get the dao of the parent
		DAO<?> dao = (DAO<?>) Patterns.getDAO(request.getTargetId(), dbs);
		if (dao == null) {
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
		List<SubscriptionEntity> subscriptions = null;

		// different cases
		// case parent is Container
		if (parentEntity.getResourceType().intValue() == (ResourceType.CONTAINER)) {
			ContainerEntity container = (ContainerEntity) parentEntity;
			acpsToCheck = container.getAccessControlPolicies();
			subscriptions = container.getSubscriptions();
		}
		// case parent is ContainerAnnc
		if (parentEntity.getResourceType().intValue() == (ResourceType.CONTAINER_ANNC)) {
			throw new NotImplementedException("Parent is Container Annc, not implemented yet.");
		}
		
		// Check acp
		checkACP(acpsToCheck, request.getFrom(), request.getOperation());
		
		// check if content is present
		if (request.getContent() == null){
			throw new BadRequestException("A content is requiered for ContentInstance creation");
		}
		// get the object from the representation
		ContentInstance cin = null;
		try{
			if (request.getRequestContentType().equals(MimeMediaType.OBJ)){
				cin = (ContentInstance) request.getContent();
			} else {
				cin = (ContentInstance)DataMapperSelector.getDataMapperList()
						.get(request.getRequestContentType()).stringToObj((String)request.getContent());				
			}

		} catch (ClassCastException e){
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if (cin == null){
			throw new BadRequestException("Error in provided content");
		}


		ContentInstanceEntity cinEntity = new ContentInstanceEntity();
		// check attributes
		// @resourceName 		NP 
		// Resource Type 		NP
		// resourceID 			NP
		// parentID 			NP
		// lastModifiedTime 	NP
		// creationTime 		NP
		// labels				O
		ControllerUtil.CreateUtil.fillEntityFromGenericResource(cin, cinEntity);

		// announcedAttribute		O
		if (!cin.getAnnouncedAttribute().isEmpty()) {
			cinEntity.getAnnouncedAttribute().addAll(cin.getAnnouncedAttribute());
		}

		// contentInfo				M
		if (cin.getContentInfo() == null) {
			cinEntity.setContentInfo("text/plain:0");
		} else {
			cinEntity.setContentInfo(cin.getContentInfo());
		}
		// content					M
		if (cin.getContent() == null) {
			throw new BadRequestException("Content is Mandatory");
		}

		String generatedId = generateId("", "");
		cinEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.CIN + Constants.PREFIX_SEPERATOR + generatedId);
		// check & set resource name if present
		if (cin.getName() != null){
			if (!Patterns.checkResourceName(cin.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			cinEntity.setName(cin.getName());
		} else 
		if (request.getName() != null){
			if (!Patterns.checkResourceName(request.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			cinEntity.setName(request.getName());
		} else {
			cinEntity.setName(ShortName.CIN + "_" + generatedId);
		}
		cinEntity.setHierarchicalURI(parentEntity.getHierarchicalURI() + "/" + cinEntity.getName());
		if (!UriMapper.addNewUri(cinEntity.getHierarchicalURI(), cinEntity.getResourceID(), ResourceType.CONTENT_INSTANCE)){
			throw new ConflictException("Name already present in the parent collection.");
		}

		cinEntity.setParentID(parentEntity.getResourceID());
		cinEntity.setResourceType(ResourceType.CONTENT_INSTANCE);
		cinEntity.setContent(cin.getContent());
		cinEntity.setStateTag(BigInteger.valueOf(0));

		// creator					O
		if (cin.getCreator() != null) {
			cinEntity.setCreator(cin.getCreator());
		}

		// expiration time 			O?
		if (cin.getExpirationTime() != null){
			cinEntity.setExpirationTime(cin.getExpirationTime());
		}

		// announceTo				O
		if (!cin.getAnnounceTo().isEmpty()){
			cinEntity.getAnnounceTo().addAll(cin.getAnnounceTo());
		}
		// ontologyRef				O
		if (cin.getOntologyRef() != null){
			cinEntity.setOntologyRef(cin.getOntologyRef());
		}

		// case parent is Container
		if (parentEntity.getResourceType().intValue() == (ResourceType.CONTAINER)) {
			ContainerEntity container = (ContainerEntity) parentEntity;
			List<ContentInstanceEntity> cinList = container.getChildContentInstances();
			if (container.getMaxNrOfInstances() != null && (cinList.size() == container.getMaxNrOfInstances().intValue())) {
				dbs.getDAOFactory().getContentInstanceDAO().delete(transaction, container.getChildContentInstances().get(0));
			}
			cinEntity.setParentContainer(container);
			if(container.getStateTag() != null){
				container.setStateTag(BigInteger.valueOf(container.getStateTag().intValue() + 1));
				dbs.getDAOFactory().getContainerDAO().update(transaction, container);
			}
		}
		// case parent is ContainerAnnc
		if (parentEntity.getResourceType().intValue() == (ResourceType.CONTAINER_ANNC)) {
			//TODO set parent containerAnnc when implemented
		}

		// create the contentInstance in the DB
		dbs.getDAOFactory().getContentInstanceDAO().create(transaction, cinEntity);
		// commit the transaction
		transaction.commit();

		Notifier.notify(subscriptions, cinEntity, ResourceStatus.CHILD_CREATED);

		// create the response
		response.setResponseStatusCode(ResponseStatusCode.CREATED);
		// set the location of the resource
		setLocationAndCreationContent(request, response, cinEntity);
		return response;
	}

	/**
	 * Generic do retrieve operation
	 * @param request
	 * @return response
	 */
	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		// create the response primitive
		ResponsePrimitive response = new ResponsePrimitive(request);

		// check existence of the resource
		ContentInstanceEntity cinEntity = dbs.getDAOFactory().getContentInstanceDAO().find(transaction, request.getTargetId());
		if (cinEntity == null) {
			throw new ResourceNotFoundException("Resource not found");
		}

		// check authorization
		List<AccessControlPolicyEntity> acpList = cinEntity.getAcpListFromParent();
		checkACP(acpList, request.getFrom(), request.getOperation());
		

		// mapping the entity with the exchange resource
		ContentInstance cin = EntityMapperFactory.getContentInstanceMapper().
				mapEntityToResource(cinEntity, request);
		response.setContent(cin);

		response.setResponseStatusCode(ResponseStatusCode.OK); 
		// return the completed response
		return response;
	}


	/**
	 * Full update for Content Instance
	 */
	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		// this operation is not allowed for content instance resource
		throw new OperationNotAllowed("Update on ContentInstance is not Allowed");
	}

	/**
	 * Generic delete procedure
	 */
	@Override
	public ResponsePrimitive doDelete(RequestPrimitive request) {
		// create the response primitive
		ResponsePrimitive response = new ResponsePrimitive(request);
		// get the database service
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		// open the transaction
		transaction.open();

		// retrieve the target resource from database
		ContentInstanceEntity cin = dbs.getDAOFactory()
				.getContentInstanceDAO().find(transaction, request.getTargetId());
		if (cin == null) {
			throw new ResourceNotFoundException("Resource not found");
		}

		// check the access control policies
		checkACP(cin.getAcpListFromParent(),
				request.getFrom(), request.getOperation());		

		UriMapper.deleteUri(cin.getHierarchicalURI());
		Notifier.notifyDeletion(null, cin);

		// delete the resource
		dbs.getDAOFactory().getContentInstanceDAO().delete(transaction, cin);
		// commit the transaction
		transaction.commit();
		// return the correct response
		response.setResponseStatusCode(ResponseStatusCode.DELETED);
		return response;
	}

}
