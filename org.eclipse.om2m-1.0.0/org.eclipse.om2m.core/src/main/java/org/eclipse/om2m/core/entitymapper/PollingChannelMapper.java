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
import org.eclipse.om2m.commons.entities.PollingChannelEntity;
import org.eclipse.om2m.commons.resource.PollingChannel;

public class PollingChannelMapper extends
		EntityMapper<PollingChannelEntity, PollingChannel> {

	@Override
	protected PollingChannel createResource() {
		return new PollingChannel();
	}

	@Override
	protected void mapAttributes(PollingChannelEntity entity,
			PollingChannel resource) {
		if (entity.getExpirationTime() != null) {
			resource.setExpirationTime(entity.getExpirationTime());
		}
		if (entity.getPollingChannelUri() != null) {
			resource.setPollingChannelURI(entity.getPollingChannelUri());
		}
		for (AccessControlPolicyEntity acpEntity : entity.getLinkedAcps()) {
			resource.getAccessControlPolicyIDs().add(acpEntity.getResourceID());
		}
	}

	@Override
	protected void mapChildResourceRef(PollingChannelEntity entity,
			PollingChannel resource) {
	}

	@Override
	protected void mapChildResources(PollingChannelEntity entity,
			PollingChannel resource) {
	}

}
