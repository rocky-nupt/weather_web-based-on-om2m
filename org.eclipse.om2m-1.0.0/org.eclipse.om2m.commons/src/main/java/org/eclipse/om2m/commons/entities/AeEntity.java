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
import javax.persistence.OneToOne;

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Application Entity JPA entity
 *
 */
@Entity(name = DBEntities.AE_ENTITY)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AeEntity extends AnnounceableSubordinateEntity {
	@Column(name=ShortName.APP_NAME)
	protected String appName;
	@Column(name=ShortName.APP_ID, nullable = false)
	protected String appID;
	@Column(name=ShortName.AE_ID, nullable = false)
	protected String aeid;
	@Column(name=ShortName.POA)
	protected List<String> pointOfAccess;
	@Column(name=ShortName.ONTOLOGY_REF)
	protected String ontologyRef;
	// TODO to improve
	@Column(name=ShortName.NODE_LINK)
	protected String nodeLink;
	@Column(name = ShortName.REQUEST_REACHABILITY)
	protected boolean requestReachability = false;
	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	protected AccessControlPolicyEntity generatedAcp ;
	
	/** AccessControlPolicies linked to the AE */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(
			name = DBEntities.AEACP_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.AE_JOINID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.ACP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<AccessControlPolicyEntity> accessControlPolicies;

	/** Child AccessControlPolicies of the AE */
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name = DBEntities.AEACPCHILD_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.AE_JOINID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.ACP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<AccessControlPolicyEntity> childAccessControlPolicies;
	
	/** Linked containers */
	@ManyToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name = DBEntities.AECNTCHILD_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.AE_JOINID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.CNT_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<ContainerEntity> childContainers;
	
	/** list of groups */
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name = DBEntities.AEGRPCHILD_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.AE_JOINID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.GRP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<GroupEntity> childGroups;
	
	/** list of polling channels */
	@JoinTable(
			name = DBEntities.AEPCH_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.AE_JOINID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.PCH_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<PollingChannelEntity> pollingChannels;
	
	/** Database link to the parent */
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CSEBaseEntity.class)
	@JoinTable(
			name=DBEntities.CSEBAE_JOIN,
			inverseJoinColumns={@JoinColumn(name=DBEntities.CSEB_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			joinColumns={@JoinColumn(name=DBEntities.AE_JOINID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected CSEBaseEntity parentCse;
	
	// Database link to remoteCse parent
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=RemoteCSEEntity.class)
	@JoinTable(
			name=DBEntities.CSRAECHILD_JOIN,
			inverseJoinColumns={@JoinColumn(name=DBEntities.CSR_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			joinColumns={@JoinColumn(name=DBEntities.AE_JOINID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected RemoteCSEEntity parentCsr;
	
	
	// Database link to Subscriptions
	@OneToMany(fetch = FetchType.LAZY, targetEntity = SubscriptionEntity.class)
	@JoinTable(
			name = DBEntities.AESUB_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.AE_JOINID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.SUB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<SubscriptionEntity> subscriptions;
	
	/**
	 * @return the childAccessControlPolicies
	 */
	public List<AccessControlPolicyEntity> getChildAccessControlPolicies() {
		if(this.childAccessControlPolicies == null) {
			this.childAccessControlPolicies = new ArrayList<>();
		}
		return childAccessControlPolicies;
	}

	/**
	 * @param childAccessControlPolicies the childAccessControlPolicies to set
	 */
	public void setChildAccessControlPolicies(
			List<AccessControlPolicyEntity> childAccessControlPolicies) {
		this.childAccessControlPolicies = childAccessControlPolicies;
	}

	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @param appName
	 *            the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @return the appID
	 */
	public String getAppID() {
		return appID;
	}

	/**
	 * @param appID
	 *            the appID to set
	 */
	public void setAppID(String appID) {
		this.appID = appID;
	}

	/**
	 * @return the aeid
	 */
	public String getAeid() {
		return aeid;
	}

	/**
	 * @param aeid
	 *            the aeid to set
	 */
	public void setAeid(String aeid) {
		this.aeid = aeid;
	}

	/**
	 * @return the pointOfAccess
	 */
	public List<String> getPointOfAccess() {
		if(pointOfAccess == null){
			pointOfAccess = new ArrayList<>();
		}
		return pointOfAccess;
	}

	/**
	 * @param pointOfAccess
	 *            the pointOfAccess to set
	 */
	public void setPointOfAccess(List<String> pointOfAccess) {
		this.pointOfAccess = pointOfAccess;
	}

	/**
	 * @return the ontologyRef
	 */
	public String getOntologyRef() {
		return ontologyRef;
	}

	/**
	 * @param ontologyRef
	 *            the ontologyRef to set
	 */
	public void setOntologyRef(String ontologyRef) {
		this.ontologyRef = ontologyRef;
	}

	/**
	 * @return the nodeLink
	 */
	public String getNodeLink() {
		return nodeLink;
	}

	/**
	 * @param nodeLink
	 *            the nodeLink to set
	 */
	public void setNodeLink(String nodeLink) {
		this.nodeLink = nodeLink;
	}

	/**
	 * @return the accessControlPolicies
	 */
	public List<AccessControlPolicyEntity> getAccessControlPolicies() {
		if (this.accessControlPolicies == null) {
			this.accessControlPolicies = new ArrayList<>();
		}
		return accessControlPolicies;
	}

	/**
	 * @param accessControlPolicies
	 *            the accessControlPolicies to set
	 */
	public void setAccessControlPolicies(
			List<AccessControlPolicyEntity> accessControlPolicies) {
		this.accessControlPolicies = accessControlPolicies;
	}

	/**
	 * @return the childContainers
	 */
	public List<ContainerEntity> getChildContainers() {
		if (childContainers == null) {
			childContainers = new ArrayList<>();
		}
		return childContainers;
	}

	/**
	 * @param childContainers the childContainers to set
	 */
	public void setChildContainers(List<ContainerEntity> childContainers) {
		this.childContainers = childContainers;
	}

	/**
	 * @return the parentCse
	 */
	public CSEBaseEntity getParentCse() {
		return parentCse;
	}

	/**
	 * @param parentCse the parentCse to set
	 */
	public void setParentCse(CSEBaseEntity parentCse) {
		this.parentCse = parentCse;
	}

	/**
	 * @return the childGroups
	 */
	public List<GroupEntity> getChildGroups() {
		if (this.childGroups == null) {
			this.childGroups = new ArrayList<>();
		}
		return childGroups;
	}

	/**
	 * @param childGroups the childGroups to set
	 */
	public void setChildGroups(List<GroupEntity> childGroups) {
		this.childGroups = childGroups;
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

	/**
	 * @return the pollingChannels
	 */
	public List<PollingChannelEntity> getPollingChannels() {
		if (this.pollingChannels == null) {
			this.pollingChannels = new ArrayList<>();
		}
		return pollingChannels;
	}

	/**
	 * @param pollingChannels the pollingChannels to set
	 */
	public void setPollingChannels(List<PollingChannelEntity> pollingChannels) {
		this.pollingChannels = pollingChannels;
	}

	/**
	 * @return the requestReachability
	 */
	public boolean isRequestReachable() {
		return requestReachability;
	}

	/**
	 * @param requestReachability the requestReachability to set
	 */
	public void setRequestReachability(boolean requestReachability) {
		this.requestReachability = requestReachability;
	}

	/**
	 * @return the generatedAcp
	 */
	public AccessControlPolicyEntity getGeneratedAcp() {
		return generatedAcp;
	}

	/**
	 * @param generatedAcp the generatedAcp to set
	 */
	public void setGeneratedAcp(AccessControlPolicyEntity generatedAcp) {
		this.generatedAcp = generatedAcp;
	}
	

}
