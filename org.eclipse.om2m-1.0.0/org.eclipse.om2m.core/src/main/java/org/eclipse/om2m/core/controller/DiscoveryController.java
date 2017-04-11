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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.om2m.commons.constants.DiscoveryResultType;
import org.eclipse.om2m.commons.constants.FilterUsage;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.AreaNwkDeviceInfoEntity;
import org.eclipse.om2m.commons.entities.AreaNwkInfoEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.entities.ContentInstanceEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.LabelEntity;
import org.eclipse.om2m.commons.entities.NodeEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.entities.UriMapperEntity;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.NotImplementedException;
import org.eclipse.om2m.commons.exceptions.OperationNotAllowed;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.FilterCriteria;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.resource.URIList;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.router.Patterns;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

/**
 * Controller for Discovery operation
 *
 */
public class DiscoveryController extends Controller {

	@Override
	public ResponsePrimitive doCreate(RequestPrimitive request) {
		throw new OperationNotAllowed("Delete of Discovery is not allowed");
	}

	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		// Create the response
		ResponsePrimitive response = new ResponsePrimitive(request);

		//Get the database service
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();

		// Get the DAO of the parent
		DAO<?> dao = (DAO<?>) Patterns.getDAO(request.getTargetId(), dbs);
		if (dao == null){
			throw new ResourceNotFoundException("Root resource not found for discovery");
		}

		ResourceEntity resourceEntity = (ResourceEntity) dao.find(transaction, request.getTargetId());

		List<AccessControlPolicyEntity> acpsToCheck = getAcpsFromEntity(resourceEntity);

		// Check access control policy of the originator
		if (resourceEntity.getResourceType().intValue() == (ResourceType.ACCESS_CONTROL_POLICY)){
			checkSelfACP(acpsToCheck.get(0), request.getFrom(), Operation.DISCOVERY);
		} else {
			checkACP(acpsToCheck, request.getFrom(), Operation.DISCOVERY);			
		}
		response = new ResponsePrimitive(request);

		// Get the filter criteria object from request primitive
		FilterCriteria filter = request.getFilterCriteria();

		if(filter.getFilterUsage().equals(FilterUsage.EVENT_NOTIFICATION_CRITERIA)){
			throw new NotImplementedException("Event notification criteria is not implemented");
		}

		if (filter.getFilterUsage().intValue() > FilterUsage.EVENT_NOTIFICATION_CRITERIA.intValue()){
			throw new BadRequestException("Incorrect FilterUsage value (fu)");
		}

		// Check the discovery result type
		if(request.getDiscoveryResultType() == null){
			request.setDiscoveryResultType(DiscoveryResultType.HIERARCHICAL);
		}		
		if (request.getDiscoveryResultType().equals(DiscoveryResultType.CSEID_AND_RESOURCEID)){
			throw new NotImplementedException();
		} else if (request.getDiscoveryResultType().intValue() > DiscoveryResultType.CSEID_AND_RESOURCEID.intValue()){
			throw new BadRequestException("Incorrect discovery result type provided");
		}

		List<UriMapperEntity> childUris = new ArrayList<>();
		if(!filter.getLabels().isEmpty()){
			int limit = (filter.getLimit() != null ? filter.getLimit().intValue() : -1);
			for(int indexLabel = 0; indexLabel < filter.getLabels().size() ; indexLabel++){
				String label = filter.getLabels().get(indexLabel);
				LabelEntity labelEntity = dbs.getDAOFactory().getLabelDAO().find(transaction, label);
				List<UriMapperEntity> auxList = new ArrayList<>();
				if(labelEntity != null){
					List<ResourceEntity> allFoundResources = stackLabelResources(labelEntity, filter);
					// In the case its the first label
					for(ResourceEntity resEntity : allFoundResources){
						if (resEntity.getHierarchicalURI().matches(resourceEntity.getHierarchicalURI() + "/?.*")) {
							UriMapperEntity uriEntity = new UriMapperEntity();
							uriEntity.setHierarchicalUri(resEntity.getHierarchicalURI());
							uriEntity.setNonHierarchicalUri(resEntity.getResourceID());
							uriEntity.setResourceType(resEntity.getResourceType());
							// In the case of multiple label, you have to make the intersection of resource
							if(indexLabel == 0){
								childUris.add(uriEntity);
							} else if (childUris.contains(uriEntity)){
								auxList.add(uriEntity);
								if(indexLabel == filter.getLabels().size() - 1 && limit != -1 && auxList.size() == limit){
									break;
								}
							}
						}
					}
					if(indexLabel != 0){
						childUris = auxList;
					}
				} else{
					// If a label is null, the intersection with other labels is null
					childUris = new ArrayList<UriMapperEntity>();
					break;
				}
			}
		} else {
			// Get the list of UriMapperEntity from database with some filters
			childUris = dbs.getDBUtilManager().getComplexFindUtil().
					getChildUrisDis(request.getTargetId(), filter);
		}
		
		URIList uriList = new URIList();
		for(UriMapperEntity uriEntity : childUris){
			if(filter.getLimit() != null && uriList.getListOfUri().size() == filter.getLimit().intValue()){
				break;
			}
			if(request.getDiscoveryResultType().equals(DiscoveryResultType.HIERARCHICAL)){
				if(!uriList.getListOfUri().contains(uriEntity.getHierarchicalUri())){
					uriList.getListOfUri().add(uriEntity.getHierarchicalUri());
				}
			} else {
				if(!uriList.getListOfUri().contains(uriEntity.getNonHierarchicalUri())){
					uriList.getListOfUri().add(uriEntity.getNonHierarchicalUri());
				}
			}
		}

		response.setContent(uriList);
		response.setResponseStatusCode(ResponseStatusCode.OK);
		return response;
	}

	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		throw new OperationNotAllowed("Update of Discovery is not allowed");
	}

	@Override
	public ResponsePrimitive doDelete(RequestPrimitive request) {
		throw new OperationNotAllowed("Delete of Discovery is not allowed");
	}

	private List<AccessControlPolicyEntity> getAcpsFromEntity(
			ResourceEntity resourceEntity) {
		int ty = resourceEntity.getResourceType().intValue();
		switch(ty){		
		case ResourceType.ACCESS_CONTROL_POLICY:
			return Arrays.asList((AccessControlPolicyEntity) resourceEntity);
		case ResourceType.AE:
			return ((AeEntity) resourceEntity).getAccessControlPolicies();
		case ResourceType.CONTAINER:
			return ((ContainerEntity) resourceEntity).getAccessControlPolicies();		
		case ResourceType.CONTENT_INSTANCE:
			return ((ContentInstanceEntity) resourceEntity).getAcpListFromParent();		
		case ResourceType.GROUP:
			return ((GroupEntity) resourceEntity).getAccessControlPolicies();	
		case ResourceType.REMOTE_CSE:
			return ((RemoteCSEEntity) resourceEntity).getAccessControlPolicies();		
		case ResourceType.CSE_BASE:
			return ((CSEBaseEntity) resourceEntity).getAccessControlPolicies();
		case ResourceType.SUBSCRIPTION:
			return ((SubscriptionEntity) resourceEntity).getAcpList();
		case ResourceType.NODE:
			return ((NodeEntity) resourceEntity).getAccessControlPolicies();
		case ResourceType.MGMT_OBJ:
			if (resourceEntity instanceof AreaNwkInfoEntity) {
				return ((AreaNwkInfoEntity) resourceEntity).getAcps();
			}
			if (resourceEntity instanceof AreaNwkDeviceInfoEntity) {
				return ((AreaNwkDeviceInfoEntity) resourceEntity).getAcps();
			}
			return null;
		default:
			// TODO On implementing resource, add the reference here
			return null;
		}
	}

	private List<ResourceEntity> stackLabelResources(LabelEntity labelEntity, FilterCriteria filter) {
		List<ResourceEntity> result = new ArrayList<>();
		BigInteger rty = filter.getResourceType();
		if (rty != null){
			switch (rty.intValue()) {
			case ResourceType.AE:
				result.addAll(labelEntity.getLinkedAe());
				break;
			case(ResourceType.CONTENT_INSTANCE):
				result.addAll(labelEntity.getLinkedCin());	
			break;
			case(ResourceType.CONTAINER):
				result.addAll(labelEntity.getLinkedCnt());	
			break;
			case(ResourceType.GROUP):
				result.addAll(labelEntity.getLinkedGroup());
			break;
			case(ResourceType.REMOTE_CSE):
				result.addAll(labelEntity.getLinkedCsr());
			break;
			case(ResourceType.CSE_BASE):
				result.addAll(labelEntity.getLinkedCsb());
			break;
			case(ResourceType.NODE): {
				result.addAll(labelEntity.getLinkedNodes());
			}
			break;
			case(ResourceType.MGMT_OBJ): {
				result.addAll(labelEntity.getLinkedAni());
				result.addAll(labelEntity.getLinkedAndi());
			}
			default:
				break;
			}
		} else {
			result.addAll(labelEntity.getLinkedAe());			
			result.addAll(labelEntity.getLinkedCin());			
			result.addAll(labelEntity.getLinkedCnt());
			result.addAll(labelEntity.getLinkedGroup());
			result.addAll(labelEntity.getLinkedCsr());
			result.addAll(labelEntity.getLinkedCsb());
			result.addAll(labelEntity.getLinkedNodes());
			result.addAll(labelEntity.getLinkedAni());
			result.addAll(labelEntity.getLinkedAndi());
		}
		return result;
	}

}
