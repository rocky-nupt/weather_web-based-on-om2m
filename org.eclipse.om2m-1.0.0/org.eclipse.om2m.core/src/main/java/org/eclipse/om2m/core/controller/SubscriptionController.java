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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.ConflictException;
import org.eclipse.om2m.commons.exceptions.NotImplementedException;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.resource.Subscription;
import org.eclipse.om2m.commons.utils.Util.DateUtil;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;
import org.eclipse.om2m.core.entitymapper.EntityMapperFactory;
import org.eclipse.om2m.core.notifier.Notifier;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.router.Patterns;
import org.eclipse.om2m.core.urimapper.UriMapper;
import org.eclipse.om2m.core.util.ControllerUtil;
import org.eclipse.om2m.core.util.ControllerUtil.CreateUtil;
import org.eclipse.om2m.core.util.ControllerUtil.UpdateUtil;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

/**
 * Controller for Subscription
 *
 */
public class SubscriptionController extends Controller{

	@Override
	public ResponsePrimitive doCreate(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Get the DAO of the parent
		DAO<?> dao = (DAO<?>) Patterns.getDAO(request.getTargetId(), dbs);
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

		// Distinguish parents
		// Case of CSEBase
		if(parentEntity.getResourceType().intValue() == (ResourceType.CSE_BASE)){
			CSEBaseEntity cseBase = (CSEBaseEntity) parentEntity;
			acpsToCheck = cseBase.getAccessControlPolicies();
		}

		if(parentEntity.getResourceType().intValue() == (ResourceType.AE)){
			AeEntity ae = (AeEntity) parentEntity;
			acpsToCheck = ae.getAccessControlPolicies();
		}

		if(parentEntity.getResourceType().intValue() == (ResourceType.REMOTE_CSE)){
			RemoteCSEEntity remoteCSE = (RemoteCSEEntity) parentEntity;
			acpsToCheck = remoteCSE.getAccessControlPolicies();
		}

		if(parentEntity.getResourceType().intValue() == (ResourceType.GROUP)){
			GroupEntity group = (GroupEntity) parentEntity;
			acpsToCheck = group.getAccessControlPolicies();
		}

		if(parentEntity.getResourceType().intValue() == (ResourceType.CONTAINER)){
			ContainerEntity container = (ContainerEntity) parentEntity;
			acpsToCheck = container.getAccessControlPolicies();
		}

		if(parentEntity.getResourceType().intValue() == (ResourceType.ACCESS_CONTROL_POLICY)){
			AccessControlPolicyEntity acp = (AccessControlPolicyEntity) parentEntity;
			acpsToCheck = new ArrayList<>();
			acpsToCheck.add(acp); // TODO check the acp to check in case of acp parent for subs
		}
		
		if(parentEntity.getResourceType().intValue() == ResourceType.REMOTE_CSE){
			RemoteCSEEntity csr = (RemoteCSEEntity) parentEntity;
			acpsToCheck = csr.getAccessControlPolicies();
		}

		if(acpsToCheck == null){
			throw new NotImplementedException("Subscription is not yet supported on this resource");
		}

		// Check access control policy of the originator
		checkACP(acpsToCheck, request.getFrom(), Operation.RETRIEVE);

		// Check if content is present
		if (request.getContent() == null){
			throw new BadRequestException("A content is requiered for Subscription creation");
		}

		Subscription subscription = null;
		try {
			if(request.getRequestContentType().equals(MimeMediaType.OBJ)){
				subscription = (Subscription) request.getContent();
			} else {
				subscription = (Subscription) DataMapperSelector.getDataMapperList().
						get(request.getRequestContentType()).stringToObj((String)request.getContent());
			}
		} catch (ClassCastException e){
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if(subscription == null){
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
		SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
		CreateUtil.fillEntityFromGenericResource(subscription, subscriptionEntity);

		// notificationUri M
		if(subscription.getNotificationURI().isEmpty()){
			throw new BadRequestException("Notification URI is mandatory");
		} else {
			subscriptionEntity.getNotificationURI().addAll(subscription.getNotificationURI());
		}

		// expirationTime			O
		if(subscription.getExpirationTime() != null){
			subscriptionEntity.setExpirationTime(subscription.getExpirationTime());
		}

		// eventNotificationCriteria O
		if(subscription.getEventNotificationCriteria() != null){
			// TODO Subscription EventNotoficationCriteria handling
		}

		// expirationCounter O
		if(subscription.getExpirationCounter() != null){
			subscriptionEntity.setExpirationCounter(subscription.getExpirationCounter());
		}

		// groupID O
		if(subscription.getGroupID() != null){
			subscriptionEntity.setGroupID(subscription.getGroupID());
		}

		// notificationForwardingUri O
		if(subscription.getNotificationForwardingURI() != null){

		}

		// batchNotify O
		if(subscription.getBatchNotify() != null){
			// TODO BatchNotify
		}

		// rateLimit O
		if(subscription.getRateLimit() != null){
			// TODO RateLimit
		}

		// preSubscriptionNotification O
		if(subscription.getPreSubscriptionNotify() != null){
			subscriptionEntity.setPreSubscriptionNotify(
					subscription.getPreSubscriptionNotify());
		}

		// pendingNotification O
		if(subscription.getPendingNotification() != null){
			subscriptionEntity.setPendingNotification(subscription.getPendingNotification());
		}

		// notificationStoragePriority O
		if(subscription.getNotificationStoragePriority() != null){
			subscriptionEntity.setNotificationStoragePriority(
					subscription.getNotificationStoragePriority());
		}

		// latestNotify O
		if(subscription.isLatestNotify() != null){
			subscriptionEntity.setLatestNotify(subscription.isLatestNotify());
		}

		// notificationContentType O
		if(subscription.getNotificationContentType() != null){
			subscriptionEntity.setNotificationContentType(
					subscription.getNotificationContentType());
		}

		// notificationEventCat O
		if(subscription.getNotificationEventCat() != null){
			subscriptionEntity.setNotificationEventCat(
					subscription.getNotificationEventCat());
		}

		// creator O
		if(subscription.getCreator() != null){
			subscriptionEntity.setCreator(subscription.getCreator());
		}

		// subscriberURI O
		if(subscription.getSubscriberURI() != null){
			subscriptionEntity.setSubscriberURI(subscription.getSubscriberURI());
		}

		if(!subscription.getAccessControlPolicyIDs().isEmpty()){
			subscriptionEntity.setAcpList(
					ControllerUtil.buildAcpEntityList(subscription.getAccessControlPolicyIDs(), transaction));
		} else {
			subscriptionEntity.getAcpList().addAll(acpsToCheck);
		}

		String generatedId = generateId();
		subscriptionEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.SUB + Constants.PREFIX_SEPERATOR + generatedId);
		subscriptionEntity.setCreationTime(DateUtil.now());
		subscriptionEntity.setLastModifiedTime(DateUtil.now());
		subscriptionEntity.setParentID(parentEntity.getResourceID());
		subscriptionEntity.setResourceType(ResourceType.SUBSCRIPTION);

		if (subscription.getName() != null){
			if (!Patterns.checkResourceName(subscription.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			subscriptionEntity.setName(subscription.getName());
		} else 
		if(request.getName() != null){
			if(!Patterns.checkResourceName(request.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			subscriptionEntity.setName(request.getName());
		} else {
			subscriptionEntity.setName(ShortName.SUB + "_" + generatedId);
		}
		Notifier.performVerificationRequest(request, subscriptionEntity);
		
		subscriptionEntity.setHierarchicalURI(parentEntity.getHierarchicalURI() + "/" + subscriptionEntity.getName());
		if(!UriMapper.addNewUri(subscriptionEntity.getHierarchicalURI(), subscriptionEntity.getResourceID(), ResourceType.SUBSCRIPTION)){
			throw new ConflictException("Name already present in the parent collection.");
		}
		
		
		subscriptionEntity.setParentEntity(parentEntity);	
		dbs.getDAOFactory().getSubsciptionDAO().create(transaction, subscriptionEntity);
		transaction.commit();
		response.setResponseStatusCode(ResponseStatusCode.CREATED);
		setLocationAndCreationContent(request, response, subscriptionEntity);
		return response;
	}

	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);

		SubscriptionEntity subscriptionEntity = dbs.getDAOFactory()
				.getSubsciptionDAO().find(transaction, request.getTargetId());
		if (subscriptionEntity == null){
			throw new ResourceNotFoundException();
		}

		checkACP(subscriptionEntity.getAcpList(), request.getFrom(), 
				Operation.RETRIEVE);
		

		// Create the object used to create the representation of the resource
		Subscription subscription = EntityMapperFactory.getSubscriptionMapper().mapEntityToResource(subscriptionEntity, request);
		response.setContent(subscription);
		
		response.setResponseStatusCode(ResponseStatusCode.OK);
		return response;
	}

	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);

		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();

		SubscriptionEntity subscriptionEntity = dbs.getDAOFactory()
				.getSubsciptionDAO().find(transaction, request.getTargetId());
		if (subscriptionEntity == null){
			throw new ResourceNotFoundException();
		}

		checkACP(subscriptionEntity.getAcpList(), request.getFrom(), 
				Operation.UPDATE);
		

		// Check if content is present
		if (request.getContent() == null){
			throw new BadRequestException("A content is requiered for Subscription update");
		}

		Subscription subscription = null;
		try {
			if(request.getRequestContentType().equals(MimeMediaType.OBJ)){
				subscription = (Subscription) request.getContent();
			} else {
				subscription = (Subscription) DataMapperSelector.getDataMapperList().
						get(request.getRequestContentType()).stringToObj((String)request.getContent());
			}
		} catch (ClassCastException e){
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if(subscription == null){
			throw new BadRequestException("Error in provided content");
		}

		// Check attributes

		// NP Attributes are ignored

		// resourceName NP
		// resourceType NP
		// resourceID NP
		// parentID NP
		// creationTime NP
		// lastTimeModified NP
		UpdateUtil.checkNotPermittedParameters(subscription);
		// preSubscriptionNotify NP
		if(subscription.getPreSubscriptionNotify() != null){
			throw new BadRequestException("PreSubscriptionNotify is NP");
		}
		// subscriberURI NP
		if(subscription.getSubscriberURI() != null){
			throw new BadRequestException("SubscripberURI is NP");
		}
		
		Subscription modifiedAttributes = new Subscription();

		// ACPIDs O
		if(!subscription.getAccessControlPolicyIDs().isEmpty()){
			for(AccessControlPolicyEntity acpe : subscriptionEntity.getAcpList()){
				checkSelfACP(acpe, request.getFrom(), Operation.UPDATE);
			}
			subscriptionEntity.getAcpList().clear();
			subscriptionEntity.setAcpList(ControllerUtil.buildAcpEntityList(subscription.getAccessControlPolicyIDs(), transaction));
			modifiedAttributes.getAccessControlPolicyIDs().addAll(subscription.getAccessControlPolicyIDs());
		}
		// expirationTime O
		if(subscription.getExpirationTime() != null){
			subscriptionEntity.setExpirationTime(subscription.getExpirationTime());
			modifiedAttributes.setExpirationTime(subscription.getExpirationTime());
		}
		// labels O
		if(!subscription.getLabels().isEmpty()){
			subscriptionEntity.getLabelsEntities().clear();
			subscriptionEntity.setLabelsEntitiesFromSring(subscription.getLabels());
			modifiedAttributes.getLabels().addAll(subscription.getLabels());
		}
		// eventNotificationCriteria O
		if(subscription.getEventNotificationCriteria() != null){
			// TODO eventNotificationCriteria
		}
		// expirationCounter O
		if(subscription.getExpirationCounter() != null){
			subscriptionEntity.setExpirationCounter(subscription.getExpirationCounter());
			modifiedAttributes.setExpirationCounter(subscription.getExpirationCounter());
		}
		// notificationUri O
		if(!subscription.getNotificationURI().isEmpty()){
			subscriptionEntity.getNotificationURI().clear();
			subscriptionEntity.getNotificationURI().addAll(subscription.getNotificationURI());
			modifiedAttributes.getNotificationURI().addAll(subscription.getNotificationURI());
		}
		// groupID O
		if(subscription.getGroupID() != null){
			subscriptionEntity.setGroupID(subscription.getGroupID());
			modifiedAttributes.setGroupID(subscription.getGroupID());
		}
		// notificationForwardingUri O
		if(subscription.getNotificationForwardingURI() != null){
			subscriptionEntity.setNotificationForwardingURI(subscription.getNotificationForwardingURI());
			modifiedAttributes.setNotificationForwardingURI(subscription.getNotificationForwardingURI());
		}
		// batchNotify O
		if(subscription.getBatchNotify() != null){
			// TODO batch notify
		}
		// rateLimit O
		if(subscription.getRateLimit() != null){
			// TODO rate limit
		}
		// pendingNotification O
		if(subscription.getPendingNotification() != null){
			subscriptionEntity.setPendingNotification(subscription.getPendingNotification());
			modifiedAttributes.setPendingNotification(subscription.getPendingNotification());
		}
		// notificationStorePriority O 
		if(subscription.getNotificationStoragePriority() != null){
			subscriptionEntity.setNotificationStoragePriority(subscription.getNotificationStoragePriority());
			modifiedAttributes.setNotificationStoragePriority(subscription.getNotificationStoragePriority());
		}
		// latestNotify O
		if(subscription.isLatestNotify() != null){
			subscriptionEntity.setLatestNotify(subscription.isLatestNotify());
			modifiedAttributes.setLatestNotify(subscription.isLatestNotify());
		}
		// notificationContentType O
		if(subscription.getNotificationContentType() != null){
			subscriptionEntity.setNotificationContentType(subscription.getNotificationContentType());
			modifiedAttributes.setNotificationContentType(subscription.getNotificationContentType());
		}
		// notificationEventCat O
		if(subscription.getNotificationEventCat() != null){
			subscriptionEntity.setNotificationEventCat(subscription.getNotificationEventCat());
			modifiedAttributes.setNotificationEventCat(subscription.getNotificationEventCat());
		}
		// creator O
		if(subscription.getCreator() != null){
			subscriptionEntity.setCreator(subscription.getCreator());
			modifiedAttributes.setCreator(subscription.getCreator());
		}

		// Update last time modified
		subscriptionEntity.setLastModifiedTime(DateUtil.now());
		modifiedAttributes.setLastModifiedTime(subscriptionEntity.getLastModifiedTime());
		response.setContent(modifiedAttributes);
		
		dbs.getDAOFactory().getSubsciptionDAO().update(transaction, subscriptionEntity);
		transaction.commit();

		response.setResponseStatusCode(ResponseStatusCode.UPDATED);
		return response;
	}

	@Override
	public ResponsePrimitive doDelete(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Get the database service
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();

		SubscriptionEntity se = dbs.getDAOFactory()
				.getSubsciptionDAO().find(transaction, request.getTargetId());
		if (se == null){
			throw new ResourceNotFoundException();
		}

		checkACP(se.getAcpList(), request.getFrom(), 
				Operation.DELETE);

		// Delete the resource in UriMapper table
		UriMapper.deleteUri(se.getHierarchicalURI());

		// Delete the resource
		dbs.getDAOFactory().getSubsciptionDAO().delete(transaction, se);
		transaction.commit();

		response.setResponseStatusCode(ResponseStatusCode.DELETED);
		return response;
	}

}
