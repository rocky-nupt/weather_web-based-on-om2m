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

import javax.persistence.CascadeType;
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
import org.eclipse.om2m.commons.constants.ShortName;

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity(name = DBEntities.NODE_ENTITY)
public class NodeEntity extends AnnounceableSubordinateEntity {

	// linked ACP
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(
			name = DBEntities.ACPNOD_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.NOD_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.ACP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<AccessControlPolicyEntity> linkedAcps;

	// node id
	@Column(name = ShortName.NODE_ID)
	protected String nodeID;
	// hosted CSE LINK
	@Column(name = ShortName.HOSTED_CSE_LINK)
	protected String hostedCSELink;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(
			name = DBEntities.CSBNOD_CH_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.NOD_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.CSEB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected CSEBaseEntity parentCsb;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(
			name = DBEntities.CSRNOD_CH_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.NOD_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.CSR_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected CSEBaseEntity parentCsr;

	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
	@JoinTable(
			name = DBEntities.NODSUB_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.NOD_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.SUB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<SubscriptionEntity> childSubscriptions;

	// Database link to AreaNwkInfo Entity
	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
	@JoinTable(
			name = DBEntities.ANINOD_JOIN,
			inverseJoinColumns = { @JoinColumn(name = DBEntities.ANI_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			joinColumns = { @JoinColumn(name = DBEntities.NOD_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<AreaNwkInfoEntity> childAreaNwkInfoEntities;
	
	// Database link to AreaNwkDeviceInfo entity
	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
	@JoinTable(
			name = DBEntities.ANDINOD_JOIN,
			inverseJoinColumns = { @JoinColumn(name = DBEntities.ANDI_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			joinColumns = { @JoinColumn(name = DBEntities.NOD_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<AreaNwkDeviceInfoEntity> childAreaNwkDeviceInfoEntities;

	/**
	 * @return the accessControlPolicies
	 */
	public List<AccessControlPolicyEntity> getAccessControlPolicies() {
		if (this.linkedAcps == null) {
			this.linkedAcps = new ArrayList<>();
		}
		return linkedAcps;
	}
	/**
	 * @param accessControlPolicies the accessControlPolicies to set
	 */
	public void setAccessControlPolicies(
			List<AccessControlPolicyEntity> accessControlPolicies) {
		this.linkedAcps = accessControlPolicies;
	}
	/**
	 * @return the nodeID
	 */
	public String getNodeID() {
		return nodeID;
	}
	/**
	 * @param nodeID the nodeID to set
	 */
	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
	/**
	 * @return the hostedCSELink
	 */
	public String getHostedCSELink() {
		return hostedCSELink;
	}
	/**
	 * @param hostedCSELink the hostedCSELink to set
	 */
	public void setHostedCSELink(String hostedCSELink) {
		this.hostedCSELink = hostedCSELink;
	}
	/**
	 * @return the parentCsb
	 */
	public CSEBaseEntity getParentCsb() {
		return parentCsb;
	}
	/**
	 * @param parentCsb the parentCsb to set
	 */
	public void setParentCsb(CSEBaseEntity parentCsb) {
		this.parentCsb = parentCsb;
	}
	/**
	 * @return the parentCsr
	 */
	public CSEBaseEntity getParentCsr() {
		return parentCsr;
	}
	/**
	 * @param parentCsr the parentCsr to set
	 */
	public void setParentCsr(CSEBaseEntity parentCsr) {
		this.parentCsr = parentCsr;
	}
	/**
	 * @return the childSubscriptions
	 */
	public List<SubscriptionEntity> getChildSubscriptions() {
		return childSubscriptions;
	}
	/**
	 * @param childSubscriptions the childSubscriptions to set
	 */
	public void setChildSubscriptions(List<SubscriptionEntity> childSubscriptions) {
		this.childSubscriptions = childSubscriptions;
	}
	/**
	 * @return the childAreaNwkInfoEntities
	 */
	public List<AreaNwkInfoEntity> getChildAreaNwkInfoEntities() {
		if (this.childAreaNwkInfoEntities == null) {
			this.childAreaNwkInfoEntities = new ArrayList<>();
		}
		return childAreaNwkInfoEntities;
	}
	/**
	 * @param childAreaNwkInfoEntities the childAreaNwkInfoEntities to set
	 */
	public void setChildAreaNwkInfoEntities(
			List<AreaNwkInfoEntity> childAreaNwkInfoEntities) {
		this.childAreaNwkInfoEntities = childAreaNwkInfoEntities;
	}
	/**
	 * @return the childAreaNwkDeviceInfoEntities
	 */
	public List<AreaNwkDeviceInfoEntity> getChildAreaNwkDeviceInfoEntities() {
		return childAreaNwkDeviceInfoEntities;
	}
	/**
	 * @param childAreaNwkDeviceInfoEntities the childAreaNwkDeviceInfoEntities to set
	 */
	public void setChildAreaNwkDeviceInfoEntities(
			List<AreaNwkDeviceInfoEntity> childAreaNwkDeviceInfoEntities) {
		this.childAreaNwkDeviceInfoEntities = childAreaNwkDeviceInfoEntities;
	}

}
