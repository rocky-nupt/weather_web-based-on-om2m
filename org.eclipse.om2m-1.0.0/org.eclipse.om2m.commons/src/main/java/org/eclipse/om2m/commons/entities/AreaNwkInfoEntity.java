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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.constants.MgmtDefinitionTypes;
import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Area Network Info Entity - Specialization of MgmtObj
 *
 */
@Entity(name = ShortName.ANI)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AreaNwkInfoEntity extends MgmtObjEntity {
	
	@Column(name = ShortName.AREA_NWK_TYPE)
	protected String areaNwkType;
	@Column(name = ShortName.LIST_DEVICES)
	protected List<String> listOfDevices;

	// link to acp
	// Database link to ACP
	@ManyToMany(fetch = FetchType.LAZY, targetEntity = AccessControlPolicyEntity.class)
	@JoinTable(
			name = DBEntities.ANIACP_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.ANI_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.ACP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<AccessControlPolicyEntity> acps;

	// Database link to Subscriptions
	@OneToMany(fetch = FetchType.LAZY, targetEntity = SubscriptionEntity.class)
	@JoinTable(
			name = DBEntities.ANISUB_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.ANI_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.SUB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<SubscriptionEntity> subscriptions;

	// Database link to Node
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = NodeEntity.class)
	@JoinTable(
			name = DBEntities.ANINOD_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.ANI_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.NOD_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected NodeEntity parentNode;

	/**
	 * Constructor
	 */
	public AreaNwkInfoEntity() {
		this.mgmtDefinition = MgmtDefinitionTypes.AREA_NWK_INFO;
	}
	
	/**
	 * @return the areaNwkType
	 */
	public String getAreaNwkType() {
		return areaNwkType;
	}

	/**
	 * @param areaNwkType the areaNwkType to set
	 */
	public void setAreaNwkType(String areaNwkType) {
		this.areaNwkType = areaNwkType;
	}

	/**
	 * @return the listOfDevices
	 */
	public List<String> getListOfDevices() {
		return listOfDevices;
	}

	/**
	 * @param listOfDevices the listOfDevices to set
	 */
	public void setListOfDevices(List<String> listOfDevices) {
		this.listOfDevices = listOfDevices;
	}
	
	/**
	 * @return the acps
	 */
	public List<AccessControlPolicyEntity> getAcps() {
		if (this.acps == null) {
			this.acps = new ArrayList<>();
		}
		return acps;
	}

	/**
	 * @param acps the acps to set
	 */
	public void setAcps(List<AccessControlPolicyEntity> acps) {
		this.acps = acps;
	}

	/**
	 * @return the subscriptions
	 */
	public List<SubscriptionEntity> getSubscriptions() {
		if (this.subscriptions == null) {
			this.subscriptions = new ArrayList<>();
		}
		return subscriptions;
	}

	/**
	 * @param subscriptions the subscriptions to set
	 */
	public void setSubscriptions(List<SubscriptionEntity> subscriptions) {
		this.subscriptions = subscriptions;
	}
	
	
	
	
}
