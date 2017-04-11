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

import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AreaNwkInfoEntity;
import org.eclipse.om2m.commons.entities.LabelEntity;
import org.eclipse.om2m.commons.resource.AreaNwkInfo;

public class AreaNwkInfoMapper extends EntityMapper<AreaNwkInfoEntity, AreaNwkInfo> {

	@Override
	protected void mapAttributes(AreaNwkInfoEntity entity, AreaNwkInfo resource) {
		resource.setAreaNwkType(entity.getAreaNwkType());
		resource.setCreationTime(entity.getCreationTime());
		resource.setDescription(entity.getDescription());
		resource.setExpirationTime(entity.getExpirationTime());
		resource.setLastModifiedTime(entity.getLastModifiedTime());
		resource.setMgmtDefinition(entity.getMgmtDefinition());
		resource.setName(entity.getName());
		resource.setParentID(entity.getParentID());
		resource.setResourceID(entity.getResourceID());
		resource.setResourceType(entity.getResourceType());
		if (!entity.getAnnouncedAttribute().isEmpty()) {
			resource.getAnnouncedAttribute().addAll(entity.getAnnouncedAttribute());
		}
		if (!entity.getAnnounceTo().isEmpty()) {
			resource.getAnnounceTo().addAll(entity.getAnnounceTo());
		}
		for (AccessControlPolicyEntity acp : entity.getAcps()) {
			resource.getAccessControlPolicyIDs().add(acp.getResourceID());
		}
		for (LabelEntity lbl : entity.getLabelsEntities()) {
			resource.getLabels().add(lbl.getLabel());
		}
		if (!entity.getListOfDevices().isEmpty()) {
			resource.getListOfDevices().addAll(entity.getListOfDevices());
		}
	}

	@Override
	protected void mapChildResourceRef(AreaNwkInfoEntity entity,
			AreaNwkInfo resource) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void mapChildResources(AreaNwkInfoEntity entity,
			AreaNwkInfo resource) {
		// TODO Auto-generated method stub

	}

	@Override
	protected AreaNwkInfo createResource() {
		return new AreaNwkInfo();
	}

}

