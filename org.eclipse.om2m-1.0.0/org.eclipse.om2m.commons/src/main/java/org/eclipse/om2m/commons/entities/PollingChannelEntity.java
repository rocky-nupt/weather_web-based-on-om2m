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
package org.eclipse.om2m.commons.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Polling channel JPA entity
 *
 */
@Entity(name = DBEntities.POLLING_CHANNEL_ENTITY)
public class PollingChannelEntity extends ResourceEntity {
	
	@Column(name = ShortName.EXPIRATION_TIME)
	protected String expirationTime;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name=DBEntities.ACPPCH_JOIN,
			joinColumns={@JoinColumn(name=DBEntities.PCH_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			inverseJoinColumns={@JoinColumn(name=DBEntities.ACP_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected List<AccessControlPolicyEntity> linkedAcps;
	
	@Column(name = ShortName.POLLING_CHANNEL_URI)
	protected String pollingChannelUri;
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = AeEntity.class)
	@JoinTable(
			name = DBEntities.AEPCH_JOIN,
			inverseJoinColumns = { @JoinColumn(name = DBEntities.AE_JOINID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			joinColumns = { @JoinColumn(name = DBEntities.PCH_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected AeEntity parentAe;
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = RemoteCSEEntity.class)
	@JoinTable(
			name = DBEntities.CSRPCH_JOIN,
			inverseJoinColumns = { @JoinColumn(name = DBEntities.CSR_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			joinColumns = { @JoinColumn(name = DBEntities.PCH_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected RemoteCSEEntity parentCsr;

	/**
	 * @return the expirationTime
	 */
	public String getExpirationTime() {
		return expirationTime;
	}

	/**
	 * @param expirationTime the expirationTime to set
	 */
	public void setExpirationTime(String expirationTime) {
		this.expirationTime = expirationTime;
	}

	/**
	 * @return the linkedAcps
	 */
	public List<AccessControlPolicyEntity> getLinkedAcps() {
		if (this.linkedAcps == null) {
			this.linkedAcps = new ArrayList<>();
		}
		return linkedAcps;
	}

	/**
	 * @param linkedAcps the linkedAcps to set
	 */
	public void setLinkedAcps(List<AccessControlPolicyEntity> linkedAcps) {
		this.linkedAcps = linkedAcps;
	}

	/**
	 * @return the pollingChannelUri
	 */
	public String getPollingChannelUri() {
		return pollingChannelUri;
	}

	/**
	 * @param pollingChannelUri the pollingChannelUri to set
	 */
	public void setPollingChannelUri(String pollingChannelUri) {
		this.pollingChannelUri = pollingChannelUri;
	}

	/**
	 * @return the parentAe
	 */
	public AeEntity getParentAe() {
		return parentAe;
	}

	/**
	 * @param parentAe the parentAe to set
	 */
	public void setParentAe(AeEntity parentAe) {
		this.parentAe = parentAe;
	}

	/**
	 * @return the parentCsr
	 */
	public RemoteCSEEntity getParentCsr() {
		return parentCsr;
	}

	/**
	 * @param parentCsr the parentCsr to set
	 */
	public void setParentCsr(RemoteCSEEntity parentCsr) {
		this.parentCsr = parentCsr;
	}

	
	
}
