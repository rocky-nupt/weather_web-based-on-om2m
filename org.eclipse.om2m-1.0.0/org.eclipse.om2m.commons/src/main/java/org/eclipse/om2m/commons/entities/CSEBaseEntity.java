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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Cse Base JPA entity
 *
 */
@Entity(name=DBEntities.CSEBASE_ENTITY)
public class CSEBaseEntity extends ResourceEntity {
	/**
	 * List of owned AccessControlPolicies 
	 */
	@ManyToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name=DBEntities.CSEBACP_JOIN,
			joinColumns={@JoinColumn(name=DBEntities.CSEB_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			inverseJoinColumns={@JoinColumn(name=DBEntities.ACP_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected List<AccessControlPolicyEntity> accessControlPolicies;

	@Column(name=ShortName.CSE_TYPE)
	protected BigInteger cseType; // TODO see if better int ? short ?

	@Column(name=ShortName.CSE_ID)
	protected String cseid;

	@Column(name=ShortName.SRT)
	protected List<BigInteger> supportedResourceType;

	@Column(name=ShortName.POA)
	protected List<String> pointOfAccess;
	@Column(name=ShortName.NODE_LINK)
	protected String nodeLink;

	
	
	/** List of Nodes */
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name=DBEntities.CSBNOD_CH_JOIN,
			joinColumns={@JoinColumn(name=DBEntities.CSEB_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			inverseJoinColumns={@JoinColumn(name=DBEntities.NOD_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected List<NodeEntity> childNodes;
	
	
	// TODO list of mgmtCmd
	// TODO list of location policy
	// TODO list of stats config
	// TODO list of stats collect
	// TODO list of delivery
	// TODO list of m2m service subscription profile
	// TODO list of service subscribed app rule
	
	/** List of Child Request */
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name = DBEntities.CSEB_REQ_JOIN,
			joinColumns={@JoinColumn(name=DBEntities.CSEB_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			inverseJoinColumns={@JoinColumn(name=DBEntities.REQ_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected List<RequestEntity> childReq;
	
	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = ScheduleEntity.class)
	@JoinColumn(name = "sch_id")
	protected ScheduleEntity linkedSchedule;


	/** List of ApplicationEntities */
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name=DBEntities.CSEBAE_JOIN,
			joinColumns={@JoinColumn(name=DBEntities.CSEB_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			inverseJoinColumns={@JoinColumn(name=DBEntities.AE_JOINID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected List<AeEntity> childAes;

	/** List of Remote CSEs */
	@OneToMany(fetch = FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name = DBEntities.CSBCSR_JOIN,
			joinColumns = {@JoinColumn(name = DBEntities.CSEB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID)},
			inverseJoinColumns = {@JoinColumn(name = DBEntities.CSR_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID)}
			)
	protected List<RemoteCSEEntity> childRemoteCses;

	// TODO list of nodes
	
	/** List of ContainerEntities */
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name=DBEntities.CSEB_CNT_JOIN,
			joinColumns={@JoinColumn(name=DBEntities.CSEB_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			inverseJoinColumns={@JoinColumn(name=DBEntities.CNT_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected List<ContainerEntity> childContainers;

	/** List of ContainerEntities */
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name=DBEntities.CSEB_GRP_JOIN,
			joinColumns={@JoinColumn(name=DBEntities.CSEB_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			inverseJoinColumns={@JoinColumn(name=DBEntities.GRP_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected List<GroupEntity> childGroups;

	/**
	 * List of child AccessControlPolicies
	 */
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name=DBEntities.CSEBCHILDACP_JOIN,
			joinColumns={@JoinColumn(name=DBEntities.CSEB_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			inverseJoinColumns={@JoinColumn(name=DBEntities.ACP_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected List<AccessControlPolicyEntity> childAccessControlPolicies;

	// list of subscription
	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
	@JoinTable(
			name = DBEntities.CSBSUB_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.CSEB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.SUB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<SubscriptionEntity> childSubscriptions;

	/**
	 * @return the aes
	 */
	public List<AeEntity> getAes() {
		if (this.childAes == null) {
			this.childAes = new ArrayList<>();
		}
		return childAes;
	}

	/**
	 * @param aes the aes to set
	 */
	public void setAes(List<AeEntity> aes) {
		this.childAes = aes;
	}

	/**
	 * @return the accessControlPolicies
	 */
	public List<AccessControlPolicyEntity> getAccessControlPolicies() {
		if (accessControlPolicies == null){
			accessControlPolicies = new ArrayList<>();
		}
		return accessControlPolicies;
	}

	/**
	 * @param accessControlPolicies the accessControlPolicies to set
	 */
	public void setAccessControlPolicies(
			List<AccessControlPolicyEntity> accessControlPolicies) {
		this.accessControlPolicies = accessControlPolicies;
	}

	/**
	 * @return the cseType
	 */
	public BigInteger getCseType() {
		return cseType;
	}

	/**
	 * @param cseType the cseType to set
	 */
	public void setCseType(BigInteger cseType) {
		this.cseType = cseType;
	}

	/**
	 * @return the cseid
	 */
	public String getCseid() {
		return cseid;
	}

	/**
	 * @param cseid the cseid to set
	 */
	public void setCseid(String cseid) {
		this.cseid = cseid;
	}

	/**
	 * @return the supportedResourceType
	 */
	public List<BigInteger> getSupportedResourceType() {
		if (supportedResourceType == null){
			supportedResourceType = new ArrayList<>();
		}
		return supportedResourceType;
	}

	/**
	 * @param supportedResourceType the supportedResourceType to set
	 */
	public void setSupportedResourceType(List<BigInteger> supportedResourceType) {
		this.supportedResourceType = supportedResourceType;
	}

	/**
	 * @return the pointOfAccess
	 */
	public List<String> getPointOfAccess() {
		if (pointOfAccess == null){
			pointOfAccess = new ArrayList<>();
		}
		return pointOfAccess;
	}

	/**
	 * @param pointOfAccess the pointOfAccess to set
	 */
	public void setPointOfAccess(List<String> pointOfAccess) {
		this.pointOfAccess = pointOfAccess;
	}

	/**
	 * @return the nodeLink
	 */
	public String getNodeLink() {
		return nodeLink;
	}

	/**
	 * @param nodeLink the nodeLink to set
	 */
	public void setNodeLink(String nodeLink) {
		this.nodeLink = nodeLink;
	}

	/**
	 * @return the childAccessControlPolicies
	 */
	public List<AccessControlPolicyEntity> getChildAccessControlPolicies() {
		if (childAccessControlPolicies == null){
			childAccessControlPolicies = new ArrayList<>();
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
	 * @return the containers
	 */
	public List<ContainerEntity> getChildContainers() {
		if (childContainers == null) {
			childContainers = new ArrayList<>();
		}
		return childContainers;
	}

	/**
	 * @param containers the containers to set
	 */
	public void setContainers(List<ContainerEntity> containers) {
		this.childContainers = containers;
	}

	/**
	 * @return the remoteCses
	 */
	public List<RemoteCSEEntity> getRemoteCses() {
		if (childRemoteCses == null) {
			childRemoteCses = new ArrayList<>();
		}
		return childRemoteCses;
	}

	/**
	 * @param remoteCses the remoteCses to set
	 */
	public void setRemoteCses(List<RemoteCSEEntity> remoteCses) {
		this.childRemoteCses = remoteCses;
	}

	/**
	 * @return the groups
	 */
	public List<GroupEntity> getGroups() {
		if (this.childGroups == null) {
			this.childGroups = new ArrayList<>();
		}
		return childGroups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<GroupEntity> groups) {
		this.childGroups = groups;
	}

	/**
	 * @return the subscriptions
	 */
	public List<SubscriptionEntity> getSubscriptions() {
		if (this.childSubscriptions == null) {
			this.childSubscriptions = new ArrayList<>();
		}
		return childSubscriptions;
	}

	/**
	 * @param subscriptions the subscriptions to set
	 */
	public void setSubscriptions(List<SubscriptionEntity> subscriptions) {
		this.childSubscriptions = subscriptions;
	}

	/**
	 * @return the linkedSchedule
	 */
	public ScheduleEntity getLinkedSchedule() {
		return linkedSchedule;
	}

	/**
	 * @param linkedSchedule the linkedSchedule to set
	 */
	public void setLinkedSchedule(ScheduleEntity linkedSchedule) {
		this.linkedSchedule = linkedSchedule;
	}

	/**
	 * @return the list of child requests
	 */
	public List<RequestEntity> getChildReq() {
		if(childReq == null){
			childReq = new ArrayList<RequestEntity>();
		}
		return childReq;
	}

	/**
	 * @param childReq
	 */
	public void setChildReq(List<RequestEntity> childReq) {
		this.childReq = childReq;
	}
	
	/**
	 * @return the childNodes
	 */
	public List<NodeEntity> getChildNodes() {
		return childNodes;
	}

	/**
	 * @param childNodes the childNodes to set
	 */
	public void setChildNodes(List<NodeEntity> childNodes) {
		this.childNodes = childNodes;
	}
}
