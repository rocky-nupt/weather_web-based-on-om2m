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

import org.eclipse.om2m.commons.constants.ConsistencyStrategy;
import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MemberType;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResourceStatus;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.ConflictException;
import org.eclipse.om2m.commons.exceptions.NotImplementedException;
import org.eclipse.om2m.commons.exceptions.NotPermittedAttrException;
import org.eclipse.om2m.commons.exceptions.Om2mException;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.Group;
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
import org.eclipse.om2m.core.util.GroupUtil;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

/**
 * Controller for group
 *
 */
public class GroupController extends Controller {

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
	 * labels					O
	 * announceTo				O
	 * announcedAttribute		O
	 * creator					O
	 * memberType				M
	 * currentNrOdMembers		NP
	 * maxNrOfMembers			M
	 * memberID					M
	 * membersACPIDs			O
	 * memberTypeValidated		NP
	 * consistencyStrategy		O
	 * groupName				O
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
		ResourceEntity parentEntity = (ResourceEntity) dao.find(transaction, request.getTargetId());
		// Check the parent existence
		if (parentEntity == null){
			throw new ResourceNotFoundException("Cannot find parent resource");
		}

		// Get lists to change in the method corresponding to specific object
		List<AccessControlPolicyEntity> acpsToCheck = null;
		List<GroupEntity> childGroups = null;
		List<SubscriptionEntity> subscriptions = null;

		// Distinguish parents
		// Case of CSEBase
		if(parentEntity.getResourceType().intValue() == (ResourceType.CSE_BASE)){
			CSEBaseEntity cseBase = (CSEBaseEntity) parentEntity;
			acpsToCheck = cseBase.getAccessControlPolicies();
			childGroups = cseBase.getGroups();
			subscriptions = cseBase.getSubscriptions();
		}

		if(parentEntity.getResourceType().intValue() == (ResourceType.AE)){
			AeEntity ae = (AeEntity) parentEntity;
			acpsToCheck = ae.getAccessControlPolicies();
			childGroups = ae.getChildGroups();
			subscriptions = ae.getSubscriptions();
		}

		if(parentEntity.getResourceType().intValue() == (ResourceType.REMOTE_CSE)){
			RemoteCSEEntity remoteCSE = (RemoteCSEEntity) parentEntity;
			acpsToCheck = remoteCSE.getAccessControlPolicies();
			childGroups = remoteCSE.getChildGrps();
			subscriptions = remoteCSE.getSubscriptions();
		}

		if(parentEntity.getResourceType().intValue() == (ResourceType.REMOTE_CSE_ANNC)){
			throw new NotImplementedException("remote cse announcer not implemented");
		}

		if(parentEntity.getResourceType().intValue() == (ResourceType.AE_ANNC)){
			// TODO AE_ANNC group parent
			throw new NotImplementedException("ae annc not implemented");
		}

		// Check access control policy of the originator
		checkACP(acpsToCheck, request.getFrom(), Operation.CREATE);

		// Check if content is present
		if (request.getContent() == null){
			throw new BadRequestException("A content is requiered for Group creation");
		}

		Group group = null;
		try {
			if(request.getRequestContentType().equals(MimeMediaType.OBJ)){
				group = (Group) request.getContent();
			} else {
				group = (Group) DataMapperSelector.getDataMapperList().
						get(request.getRequestContentType()).stringToObj((String)request.getContent());
			}
		} catch (ClassCastException e){
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if (group == null){
			throw new BadRequestException("Error in provided content");
		}

		GroupEntity groupEntity = new GroupEntity();
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
		ControllerUtil.CreateUtil.fillEntityFromAnnounceableResource(group, groupEntity);

		// currentNrOdMembers		NP
		if (group.getCurrentNrOfMembers() != null){
			throw new NotPermittedAttrException("CurrentNrOfMembers is Not Permitted");
		}
		// memberTypeValidated		NP
		if (group.getMemberTypeValidated() != null){
			throw new NotPermittedAttrException("MemberTypeValidated is Not Permitted");
		}

		// maxNrOfMembers			M
		if(group.getMaxNrOfMembers() == null){
			throw new BadRequestException("MaxNrOfMembers is Mandatory");
		}
		groupEntity.setMaxNrOfMembers(group.getMaxNrOfMembers());

		// memberType				O
		if(group.getMemberType() == null){
			groupEntity.setMemberType(MemberType.MIXED);
		} else {
			groupEntity.setMemberType(group.getMemberType());			
		}
		// memberID					M
		if (group.getMemberIDs().isEmpty()){
			throw new BadRequestException("MemberIDs is Mandatory");
		}
		if(group.getMemberIDs().size() > group.getMaxNrOfMembers().intValue()){
			throw new Om2mException("Max number of member exceeded", 
					ResponseStatusCode.MAX_NUMBER_OF_MEMBER_EXCEEDED);
		}
		groupEntity.getMemberIDs().addAll(group.getMemberIDs());

		// consistencyStrategy		O
		if (group.getConsistencyStrategy() != null){
			groupEntity.setConsistencyStrategy(group.getConsistencyStrategy());
		} else {
			groupEntity.setConsistencyStrategy(ConsistencyStrategy.ABANDON_MEMBER);
		}

		// accessControlPolicyIDs	O
		if(!group.getAccessControlPolicyIDs().isEmpty()){
			groupEntity.setAccessControlPolicies(
					ControllerUtil.buildAcpEntityList(group.getAccessControlPolicyIDs(), transaction));

		} else {
			groupEntity.getAccessControlPolicies().addAll(acpsToCheck);
		}
		// creator					O
		if(group.getCreator() != null){
			groupEntity.setCreator(group.getCreator());
		}
		// membersACPIDs			O
		if (!group.getMembersAccessControlPolicyIDs().isEmpty()){
			groupEntity.getMemberAcpIds().addAll(group.getMembersAccessControlPolicyIDs());
		}
		// groupName				O
		if (group.getGroupName() != null){
			groupEntity.setGroupName(group.getGroupName());
		}

		String generatedId = generateId();
		groupEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.GROUP + Constants.PREFIX_SEPERATOR + generatedId);;
		groupEntity.setCreationTime(DateUtil.now());
		groupEntity.setLastModifiedTime(DateUtil.now());
		groupEntity.setParentID(parentEntity.getResourceID());
		groupEntity.setResourceType(ResourceType.GROUP);
		
		if (group.getName() != null){
			if (!Patterns.checkResourceName(group.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			groupEntity.setName(group.getName());
		} else 
		if(request.getName() != null){
			if (!Patterns.checkResourceName(request.getName())){
				throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
			}
			groupEntity.setName(request.getName());
		} else {
			groupEntity.setName(ShortName.GROUP + "_" + generatedId);
		}
		groupEntity.setHierarchicalURI(parentEntity.getHierarchicalURI()+ "/" + groupEntity.getName());

		// Validate the memberType of memberIDs
		GroupUtil.validateGroupMember(groupEntity);

		if (!UriMapper.addNewUri(groupEntity.getHierarchicalURI(), groupEntity.getResourceID(), ResourceType.GROUP)){
			throw new ConflictException("Name already present in the parent collection.");
		}

		dbs.getDAOFactory().getGroupDAO().create(transaction, groupEntity);

		// Get the managed object from db
		GroupEntity groupDB = dbs.getDAOFactory().getGroupDAO().find(transaction, groupEntity.getResourceID());

		childGroups.add(groupDB);
		dao.update(transaction, parentEntity);
		transaction.commit();

		Notifier.notify(subscriptions, groupDB, ResourceStatus.CHILD_CREATED);

		response.setResponseStatusCode(ResponseStatusCode.CREATED);
		setLocationAndCreationContent(request, response, groupDB);
		return response;
	}

	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		/*
		 * Generic retrieve procedure
		 */
		ResponsePrimitive response = new ResponsePrimitive(request);

		// Check the existence of the resource
		GroupEntity groupEntity = dbs.getDAOFactory().getGroupDAO().
				find(transaction, request.getTargetId());
		if (groupEntity == null){
			throw new ResourceNotFoundException("Resource not found");
		}

		checkACP(groupEntity.getAccessControlPolicies(), request.getFrom(), 
				Operation.RETRIEVE);
		

		// Create the object used to create the representation of the resource
		Group group = EntityMapperFactory.getGroupMapper().mapEntityToResource(groupEntity, request);
		response.setContent(group);

		response.setResponseStatusCode(ResponseStatusCode.OK);

		return response;
	}

	/*
	 * 							Req
	 * @resourceName			NP
	 * resourceType				NP
	 * resourceID				NP
	 * parentID					NP
	 * accessControlPolicyIDs	NP
	 * creationTime				NP
	 * expirationTime			O
	 * lastModifiedTime			NP
	 * labels					O
	 * announceTo				O
	 * announcedAttribute		O
	 * creator					NP
	 * memberType				O
	 * currentNrOdMembers		NP
	 * maxNrOfMembers			O
	 * memberID					O
	 * membersACPIDs			O
	 * memberTypeValidated		NP
	 * consistencyStrategy		NP
	 * groupName				O
	 */
	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);

		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();

		// Retrieve the resource from database
		GroupEntity groupEntity = dbs.getDAOFactory()
				.getGroupDAO().find(transaction, request.getTargetId());
		if (groupEntity == null){
			throw new ResourceNotFoundException("Resource not found");
		}

		checkACP(groupEntity.getAccessControlPolicies(), request.getFrom(), 
				Operation.UPDATE);

		// Check if content is present
		if (request.getContent() == null){
			throw new BadRequestException("A content is requiered for Group update");
		}

		Group group = null;
		try {
			if(request.getRequestContentType().equals(MimeMediaType.OBJ)){
				group = (Group) request.getContent();
			} else {
				group = (Group) DataMapperSelector.getDataMapperList().
						get(request.getRequestContentType()).stringToObj((String)request.getContent());
			}
		} catch (ClassCastException e){
			throw new BadRequestException("Incorrect resource representation in content", e);
		}
		if (group == null){
			throw new BadRequestException("Error in provided content");
		}

		// Check attributes

		// NP Attributes are ignores
		// @resourceName			NP
		// resourceType				NP
		// resourceID				NP
		// parentID					NP
		// accessControlPolicyIDs	NP
		// creationTime				NP
		// lastModifiedTime			NP
		UpdateUtil.checkNotPermittedParameters(group);
		// creator					NP
		if(group.getCreator() != null){
			throw new BadRequestException("Creator is NP");
		}
		// currentNrOdMembers		NP
		if(group.getCurrentNrOfMembers() != null){
			throw new BadRequestException("CurrentNrOfMembers is NP");
		}
		// memberTypeValidated		NP
		if(group.getMemberTypeValidated() != null){
			throw new BadRequestException("MemberTypeValidated is NP");
		}
		// consistencyStrategy		NP
		if(group.getConsistencyStrategy() != null){
			throw new BadRequestException("ConsistencyStrategy is NP");
		}

		Group modifiedAttributes = new Group();
		
		// expirationTime			O
		if (group.getExpirationTime() != null){
			groupEntity.setExpirationTime(group.getExpirationTime());
			modifiedAttributes.setExpirationTime(group.getExpirationTime());
		}
		// labels					O
		if(!group.getLabels().isEmpty()){
			groupEntity.getLabelsEntities().clear();
			groupEntity.setLabelsEntitiesFromSring(group.getLabels());
			modifiedAttributes.getLabels().addAll(group.getLabels());
		}
		// announceTo				O
		if(!group.getAnnounceTo().isEmpty()){
			// TODO Announcement in group update
			groupEntity.getAnnounceTo().clear();
			groupEntity.getAnnounceTo().addAll(group.getAnnounceTo());
			modifiedAttributes.getAnnounceTo().addAll(group.getAnnounceTo());
		}
		// announcedAttribute		O
		if(!group.getAnnouncedAttribute().isEmpty()){
			groupEntity.getAnnouncedAttribute().clear();
			groupEntity.getAnnouncedAttribute().addAll(group.getAnnouncedAttribute());
			modifiedAttributes.getAnnouncedAttribute().addAll(group.getAnnouncedAttribute());
		}
		// memberType				O
		if(group.getMemberType() != null){
			groupEntity.setMemberType(group.getMemberType());
			modifiedAttributes.setMemberType(group.getMemberType());
		}
		// maxNrOfMembers			O
		if(group.getMaxNrOfMembers() != null){
			groupEntity.setMaxNrOfMembers(group.getMaxNrOfMembers());
			modifiedAttributes.setMaxNrOfMembers(group.getMaxNrOfMembers());
		}
		// memberID					O
		if(!group.getMemberIDs().isEmpty()){
			groupEntity.getMemberIDs().clear();
			groupEntity.getMemberIDs().addAll(group.getMemberIDs());
			modifiedAttributes.getMemberIDs().addAll(group.getMemberIDs());
		}
		// membersACPIDs			O
		if(!group.getMembersAccessControlPolicyIDs().isEmpty()){
			for(AccessControlPolicyEntity acpe : groupEntity.getAccessControlPolicies()){
				checkSelfACP(acpe, request.getFrom(), Operation.UPDATE);
			}
			groupEntity.getMemberAcpIds().clear();
			groupEntity.getMemberAcpIds().addAll(group.getMembersAccessControlPolicyIDs());
			modifiedAttributes.getMembersAccessControlPolicyIDs().addAll(group.getMembersAccessControlPolicyIDs());
		}
		// groupName				O
		if(group.getGroupName() != null){
			groupEntity.setGroupName(group.getGroupName());
			modifiedAttributes.setGroupName(group.getGroupName());
		}

		// Last Time Modified update
		groupEntity.setLastModifiedTime(DateUtil.now());
		modifiedAttributes.setLastModifiedTime(groupEntity.getLastModifiedTime());
		response.setContent(modifiedAttributes);
		
		GroupUtil.validateGroupMember(groupEntity);
		
		// Update the resource in database
		dbs.getDAOFactory().getGroupDAO().update(transaction, groupEntity);
		transaction.commit();
		
		Notifier.notify(groupEntity.getSubscriptions(), groupEntity, ResourceStatus.UPDATED);
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
		GroupEntity groupEntity = dbs.getDAOFactory()
				.getGroupDAO().find(transaction, request.getTargetId());
		if (groupEntity == null){
			throw new ResourceNotFoundException("Resource not found");
		}

		checkACP(groupEntity.getAccessControlPolicies(), request.getFrom(), 
				Operation.DELETE);
		
		UriMapper.deleteUri(groupEntity.getHierarchicalURI());
		Notifier.notifyDeletion(groupEntity.getSubscriptions(), groupEntity);

		// Delete the resource
		dbs.getDAOFactory().getGroupDAO().delete(transaction, groupEntity);

		// Commit the transaction
		transaction.commit();

		response.setResponseStatusCode(ResponseStatusCode.DELETED);
		return response;
	}

}
