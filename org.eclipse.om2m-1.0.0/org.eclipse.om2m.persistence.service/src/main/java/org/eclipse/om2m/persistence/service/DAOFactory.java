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
package org.eclipse.om2m.persistence.service;

import org.eclipse.om2m.commons.entities.AccessControlOriginatorEntity;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.entities.ContentInstanceEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.LabelEntity;
import org.eclipse.om2m.commons.entities.NodeEntity;
import org.eclipse.om2m.commons.entities.PollingChannelEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.RequestEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.entities.UriMapperEntity;

/**
 * Returns the corresponding DAO 
 */
public interface DAOFactory {
		
	public abstract DAO<AccessControlPolicyEntity> getAccessControlPolicyDAO();	
	
	public abstract DAO<AeEntity> getAeDAO();
	
	public abstract DAO<ContainerEntity> getContainerDAO();

	public abstract DAO<ContentInstanceEntity> getContentInstanceDAO();

	public abstract DAO<CSEBaseEntity> getCSEBaseDAO();

	public abstract DAO<GroupEntity> getGroupDAO();

	public abstract DAO<LabelEntity> getLabelDAO();

	public abstract DAO<NodeEntity> getNodeEntityDAO();

	public abstract DAO<UriMapperEntity> getUriMapperEntity();

	public abstract DAO<PollingChannelEntity> getPollingChannelDAO();

	public abstract DAO<RemoteCSEEntity> getRemoteCSEDAO();
	
	public abstract DAO<RemoteCSEEntity> getRemoteCSEbyCseIdDAO();

	public abstract DAO<RequestEntity> getRequestEntityDAO();

	public abstract DAO<SubscriptionEntity> getSubsciptionDAO();

	public abstract DAO<AccessControlOriginatorEntity> getAccessControlOriginatorDAO();
	
}