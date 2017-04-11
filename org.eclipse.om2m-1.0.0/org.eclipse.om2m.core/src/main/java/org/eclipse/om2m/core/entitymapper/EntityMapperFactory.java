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
package org.eclipse.om2m.core.entitymapper;

import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.AreaNwkDeviceInfoEntity;
import org.eclipse.om2m.commons.entities.AreaNwkInfoEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.entities.ContentInstanceEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.MgmtObjEntity;
import org.eclipse.om2m.commons.entities.NodeEntity;
import org.eclipse.om2m.commons.entities.PollingChannelEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.RequestEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.resource.AE;
import org.eclipse.om2m.commons.resource.AccessControlPolicy;
import org.eclipse.om2m.commons.resource.AreaNwkDeviceInfo;
import org.eclipse.om2m.commons.resource.AreaNwkInfo;
import org.eclipse.om2m.commons.resource.CSEBase;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.Node;
import org.eclipse.om2m.commons.resource.PollingChannel;
import org.eclipse.om2m.commons.resource.RemoteCSE;
import org.eclipse.om2m.commons.resource.Request;
import org.eclipse.om2m.commons.resource.Subscription;

/**
 * Entity Mapper Factory
 *
 */
public class EntityMapperFactory {

	/** Get the CSE Base mapper */
	public static EntityMapper<CSEBaseEntity, CSEBase> getCseBaseMapper() {
		return new CseBaseMapper();
	}
	/** Get the AE mapper */
	public static EntityMapper<AeEntity, AE> getAEMapper(){
		return new AeMapper();
	}
	/** Get the ACP mapper */
	public static EntityMapper<AccessControlPolicyEntity,AccessControlPolicy> getAcpMapper(){
		return new AcpMapper();
	}
	/** Get the Container mapper */
	public static EntityMapper<ContainerEntity, Container> getContainerMapper(){
		return new ContainerMapper();
	}
	/** Get the Content instance mapper */
	public static EntityMapper<ContentInstanceEntity, ContentInstance> getContentInstanceMapper(){
		return new ContentInstanceMapper();
	}
	/** Get the group mapper */
	public static EntityMapper<GroupEntity, Group> getGroupMapper(){
		return new GroupMapper();
	}
	/** Get the node mapper */
	public static EntityMapper<NodeEntity, Node> getNodeMapper() {
		return new NodeMapper();
	}
	/** Get the Remote CSE mapper */
	public static EntityMapper<RemoteCSEEntity, RemoteCSE> getRemoteCseMapper(){
		return new RemoteCSEMapper();
	}
	/** Get the Subscription mapper */
	public static EntityMapper<SubscriptionEntity, Subscription> getSubscriptionMapper(){
		return new SubscriptionMapper();
	}
	/** Get the Polling channel mapper */
	public static EntityMapper<PollingChannelEntity, PollingChannel> getPollingChannelMapper() {
		return new PollingChannelMapper();
	}
	
	/** Get the Request mapper */
	public static EntityMapper<RequestEntity, Request> getRequestMapper(){
		return new RequestMapper();
	}

	/** Get the Area Nwk info mapper */
	public static EntityMapper<AreaNwkInfoEntity, AreaNwkInfo> getAreaNwkInfoMapper(){
		return new AreaNwkInfoMapper();
	}

	/** Get the Area Nwk device info mapper */
	public static EntityMapper<AreaNwkDeviceInfoEntity, AreaNwkDeviceInfo> getAreaNwkDeviceInfoMapper(){
		return new AreaNwkDeviceInfoMapper();
	}
	
	/**
	 * Get entity mapper from resource type
	 * @param resourceType (integer)
	 * @return corresponding entity mapper
	 */
	@SuppressWarnings("rawtypes")
	public static EntityMapper getMapperFromResourceType(int resourceType) {
		// match the resource type and return corresponding mapper
		switch (resourceType) {
		case ResourceType.CSE_BASE:
			return new CseBaseMapper();
		case ResourceType.AE:
			return new AeMapper();
		case ResourceType.ACCESS_CONTROL_POLICY:
			return new AcpMapper();
		case ResourceType.CONTAINER:
			return new ContainerMapper();
		case ResourceType.CONTENT_INSTANCE:
			return new ContentInstanceMapper();
		case ResourceType.GROUP:
			return new GroupMapper();
		case ResourceType.NODE:
			return new NodeMapper();
		case ResourceType.REMOTE_CSE:
			return new RemoteCSEMapper();
		case ResourceType.SUBSCRIPTION:
			return new SubscriptionMapper();
		case ResourceType.POLLING_CHANNEL:
			return new PollingChannelMapper();
		case ResourceType.REQUEST:
			return new RequestMapper();
		case ResourceType.MGMT_OBJ:
			throw new IllegalArgumentException("Cannot get Mapper for MGMT OBJ");
		default:
			return null;
		}
	}
	
	/**
	 * Get the entity mapper for specific mgmtObj entity
	 * @param mgmtObj
	 * @return entity mapper
	 */
	@SuppressWarnings("rawtypes")
	public static EntityMapper getMapperForMgmtObj(MgmtObjEntity mgmtObj) {
		if (mgmtObj instanceof AreaNwkInfoEntity) {
			return new AreaNwkInfoMapper();
		}
		if (mgmtObj instanceof AreaNwkDeviceInfoEntity) {
			return new AreaNwkDeviceInfoMapper();
		}
		return null;
	}

}
