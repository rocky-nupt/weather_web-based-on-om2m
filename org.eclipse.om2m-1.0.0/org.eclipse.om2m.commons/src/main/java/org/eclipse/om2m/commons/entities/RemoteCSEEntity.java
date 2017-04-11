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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Remote CSE JPA entity
 *
 */
@Entity(name=DBEntities.REMOTECSE_ENTITY)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class RemoteCSEEntity extends AnnounceableSubordinateEntity {
	@Column(name=ShortName.CSE_TYPE)
	protected BigInteger cseType;
	@Column(name = ShortName.POA)
	protected List<String> pointOfAccess;
	@Column(name = ShortName.CSE_BASE)
	protected String remoteCseUri;
	@Column(name = ShortName.CSE_ID)
	protected String remoteCseId ;
	@Column(name = ShortName.M2M_EXT_ID)
	protected String m2mExtId;
	@Column(name = ShortName.TRIGGER_RECIPIENT_ID)
	protected Long triggerRecipientID;
	@Column(name = ShortName.REQUEST_REACHABILITY)
	protected boolean requestReachability;
	@Column(name = ShortName.NODE_LINK)
	protected String nodeLink;
	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	protected AccessControlPolicyEntity generatedAcp ;

	// database link with the parent CSEBase entity
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(
			name = DBEntities.CSBCSR_JOIN,
			joinColumns = {@JoinColumn(name = DBEntities.CSR_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID)},
			inverseJoinColumns = {@JoinColumn(name = DBEntities.CSEB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID)}
			)
	protected CSEBaseEntity parentCseBase;

	// database link with the acp entities
	/** List of AccessControlPolicies */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = DBEntities.CSRACP_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.CSR_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.ACP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<AccessControlPolicyEntity> accessControlPolicies;

	//TODO list of nodelink
	// list of AE
	/** List of AE */
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(
			name = DBEntities.CSRAECHILD_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.CSR_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.AE_JOINID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<AeEntity> childAes;

	// list of child CNT
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(
			name = DBEntities.CSRCNTCHILD_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.CSR_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.CNT_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<ContainerEntity> childCnt;

	// list of child GROUP
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(
			name = DBEntities.CSRGRPCHILD_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.CSR_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.GRP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<GroupEntity> childGrps;

	// database link with the CHILD acp entities
	/** List of child AccessControlPolicies */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = DBEntities.CSRACPCHILD_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.CSR_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.ACP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<AccessControlPolicyEntity> childAcps;

	//list of subscription
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(
			name = DBEntities.CSRSUB_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.CSR_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.SUB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<SubscriptionEntity> subscriptions;

	// list of Polling channels
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = PollingChannelEntity.class)
	@JoinTable(
			name = DBEntities.CSRPCH_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.CSR_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.PCH_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<PollingChannelEntity> pollingChannels;
	
	
	// schedule
	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = ScheduleEntity.class)
	@JoinColumn(name = "sch_id")
	protected ScheduleEntity linkedSchedule;
	
	/** List of Nodes */
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name=DBEntities.CSRNOD_CH_JOIN,
			joinColumns={@JoinColumn(name=DBEntities.CSR_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			inverseJoinColumns={@JoinColumn(name=DBEntities.NOD_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected List<NodeEntity> childNodes;
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
	 * @return the pointOfAccess
	 */
	public List<String> getPointOfAccess() {
		if (pointOfAccess == null) {
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
	 * @return the remoteCseUri
	 */
	public String getRemoteCseUri() {
		return remoteCseUri;
	}

	/**
	 * @param remoteCseUri the remoteCseUri to set
	 */
	public void setRemoteCseUri(String remoteCseUri) {
		this.remoteCseUri = remoteCseUri;
	}

	/**
	 * @return the remoteCseId
	 */
	public String getRemoteCseId() {
		return remoteCseId;
	}

	/**
	 * @param remoteCseId the remoteCseId to set
	 */
	public void setRemoteCseId(String remoteCseId) {
		this.remoteCseId = remoteCseId;
	}

	/**
	 * @return the m2mExtId
	 */
	public String getM2mExtId() {
		return m2mExtId;
	}

	/**
	 * @param m2mExtId the m2mExtId to set
	 */
	public void setM2mExtId(String m2mExtId) {
		this.m2mExtId = m2mExtId;
	}

	/**
	 * @return the triggerRecipientID
	 */
	public Long getTriggerRecipientID() {
		return triggerRecipientID;
	}

	/**
	 * @param triggerRecipientID the triggerRecipientID to set
	 */
	public void setTriggerRecipientID(Long triggerRecipientID) {
		this.triggerRecipientID = triggerRecipientID;
	}

	/**
	 * @return the requestReachability
	 */
	public boolean isRequestReachability() {
		return requestReachability;
	}

	/**
	 * @param requestReachability the requestReachability to set
	 */
	public void setRequestReachability(boolean requestReachability) {
		this.requestReachability = requestReachability;
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
	 * @return the parentCseBase
	 */
	public CSEBaseEntity getParentCseBase() {
		return parentCseBase;
	}

	/**
	 * @param parentCseBase the parentCseBase to set
	 */
	public void setParentCseBase(CSEBaseEntity parentCseBase) {
		this.parentCseBase = parentCseBase;
	}

	/**
	 * @return the accessControlPolicies
	 */
	public List<AccessControlPolicyEntity> getAccessControlPolicies() {
		if (accessControlPolicies == null) {
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
	 * @return the childAes
	 */
	public List<AeEntity> getChildAes() {
		if (this.childAes == null) {
			this.childAes = new ArrayList<>();
		}
		return childAes;
	}

	/**
	 * @param childAes the childAes to set
	 */
	public void setChildAes(List<AeEntity> childAes) {
		this.childAes = childAes;
	}

	/**
	 * @return the childCnt
	 */
	public List<ContainerEntity> getChildCnt() {
		if (this.childCnt == null) {
			this.childCnt = new ArrayList<>();
		}
		return childCnt;
	}

	/**
	 * @param childCnt the childCnt to set
	 */
	public void setChildCnt(List<ContainerEntity> childCnt) {
		this.childCnt = childCnt;
	}

	/**
	 * @return the childGrps
	 */
	public List<GroupEntity> getChildGrps() {
		if (this.childGrps == null) {
			this.childGrps = new ArrayList<>();
		}
		return childGrps;
	}

	/**
	 * @param childGrps the childGrps to set
	 */
	public void setChildGrps(List<GroupEntity> childGrps) {
		this.childGrps = childGrps;
	}

	/**
	 * @return the childAcps
	 */
	public List<AccessControlPolicyEntity> getChildAcps() {
		if (this.childAcps == null) {
			this.childAcps = new ArrayList<>();
		}
		return childAcps;
	}

	/**
	 * @param childAcps the childAcps to set
	 */
	public void setChildAcps(List<AccessControlPolicyEntity> childAcps) {
		this.childAcps = childAcps;
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
