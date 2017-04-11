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
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AreaNwkDeviceInfoEntity;
import org.eclipse.om2m.commons.entities.AreaNwkInfoEntity;
import org.eclipse.om2m.commons.entities.NodeEntity;
import org.eclipse.om2m.commons.resource.AreaNwkDeviceInfo;
import org.eclipse.om2m.commons.resource.AreaNwkInfo;
import org.eclipse.om2m.commons.resource.ChildResourceRef;
import org.eclipse.om2m.commons.resource.Node;

/**
 * Mapper for Node resource - entity
 *
 */
public class NodeMapper extends EntityMapper<NodeEntity, Node> {

	@Override
	protected void mapAttributes(NodeEntity entity, Node resource) {
		resource.setNodeID(entity.getNodeID());
		resource.setHostedCSELink(entity.getHostedCSELink());
		resource.setExpirationTime(entity.getExpirationTime());

		if (!entity.getAnnouncedAttribute().isEmpty()) {
			resource.getAnnouncedAttribute().addAll(entity.getAnnouncedAttribute());
		}
		if (!entity.getAnnounceTo().isEmpty()) {
			resource.getAnnounceTo().addAll(entity.getAnnounceTo());
		}

		for (AccessControlPolicyEntity acp : entity.getAccessControlPolicies()) {
			resource.getAccessControlPolicyIDs().add(acp.getResourceID());
		}
	}

	@Override
	protected void mapChildResourceRef(NodeEntity entity, Node resource) {
		// add child area nwk info entities
		for (AreaNwkInfoEntity aniEntity : entity.getChildAreaNwkInfoEntities()) {
			ChildResourceRef chref = new ChildResourceRef();
			chref.setResourceName(aniEntity.getName());
			chref.setType(ResourceType.MGMT_OBJ);
			chref.setValue(aniEntity.getResourceID());
			resource.getChildResource().add(chref);
		}
		// add child area nwk device info entities
		for (AreaNwkDeviceInfoEntity andiEntity : entity.getChildAreaNwkDeviceInfoEntities()) {
			ChildResourceRef chref = new ChildResourceRef();
			chref.setResourceName(andiEntity.getName());
			chref.setType(ResourceType.MGMT_OBJ);
			chref.setValue(andiEntity.getResourceID());
			resource.getChildResource().add(chref);
		}
	}

	@Override
	protected void mapChildResources(NodeEntity entity, Node resource) {
		// add child area nwk info entities
		for (AreaNwkInfoEntity aniEntity : entity.getChildAreaNwkInfoEntities()) {
			AreaNwkInfo aniRes = new AreaNwkInfoMapper().mapEntityToResource(aniEntity, ResultContent.ATTRIBUTES);
			resource.getMemoryOrBatteryOrAreaNwkInfo().add(aniRes);
		}
		// add child area nwk device info entities
		for (AreaNwkDeviceInfoEntity andiEntity : entity.getChildAreaNwkDeviceInfoEntities()) {
			AreaNwkDeviceInfo andiRes = new AreaNwkDeviceInfoMapper().mapEntityToResource(andiEntity, ResultContent.ATTRIBUTES);
			resource.getMemoryOrBatteryOrAreaNwkInfo().add(andiRes);
		}
	}

	@Override
	protected Node createResource() {
		return new Node();
	}

}
