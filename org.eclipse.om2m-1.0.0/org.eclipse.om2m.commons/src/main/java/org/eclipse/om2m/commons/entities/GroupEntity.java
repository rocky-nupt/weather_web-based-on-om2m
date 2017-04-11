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

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Group JPA entity
 *
 */
@Entity(name = DBEntities.GROUP_ENTITY)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class GroupEntity extends AnnounceableSubordinateEntity {
	
	@Column(name = ShortName.CREATOR)
	protected String creator;
	@Column(name = ShortName.MEMBER_TYPE, nullable = false)
	protected BigInteger memberType;
	@Column(name = ShortName.MAX_NUM_MEMBERS)
	protected BigInteger maxNrOfMembers;
	@Column(name = ShortName.MEMBER_ID)
	protected List<String> memberIDs;
	@Column(name = ShortName.MEMBER_ACP_ID)
	protected List<String> memberAcpIds;
	@Column(name = ShortName.MEMBER_TYPE_VALIDATED)
	protected boolean memberTypeValidated;
	@Column(name = ShortName.CONSISTENCY_STRATEGY)
	protected BigInteger consistencyStrategy;
	@Column(name = ShortName.GROUP_NAME)
	protected String groupName;
	@Column(name = ShortName.FANOUTPOINT)
	protected String fanOutPoint;

	// subscription list
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(
			name = DBEntities.GRPSUB_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.GRP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.SUB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<SubscriptionEntity> subscriptions;
	
	// TODO add misssing link to fanoutpoint

	// database link with the acp entities
	/** List of AccessControlPolicies */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = DBEntities.GRPACP_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.GRP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.ACP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<AccessControlPolicyEntity> accessControlPolicies;
	
	// database link to parent CSEBase
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = CSEBaseEntity.class)
	@JoinTable(
			name = DBEntities.CSEB_GRP_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.GRP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.CSEB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected CSEBaseEntity parentCseBase;

	// database link to parent AE
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = AeEntity.class)
	@JoinTable(
			name = DBEntities.AEGRPCHILD_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.GRP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.AE_JOINID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected AeEntity parentAe;

	// database link to parent AE
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = RemoteCSEEntity.class)
	@JoinTable(
			name = DBEntities.CSR_GRP_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.GRP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.CSR_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected RemoteCSEEntity parentRemoteCse;
	
	// TODO add link to parent AEAnnc
	// TODO add link to parent RemoteCSEAnnc

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the memberType
	 */
	public BigInteger getMemberType() {
		return memberType;
	}

	/**
	 * @param memberType the memberType to set
	 */
	public void setMemberType(BigInteger memberType) {
		this.memberType = memberType;
	}

	/**
	 * @return the maxNrOfMembers
	 */
	public BigInteger getMaxNrOfMembers() {
		return maxNrOfMembers;
	}

	/**
	 * @param maxNrOfMembers the maxNrOfMembers to set
	 */
	public void setMaxNrOfMembers(BigInteger maxNrOfMembers) {
		this.maxNrOfMembers = maxNrOfMembers;
	}

	/**
	 * @return the memberIDs
	 */
	public List<String> getMemberIDs() {
		if (this.memberIDs == null) {
			this.memberIDs = new ArrayList<>();
		}
		return memberIDs;
	}

	/**
	 * @param memberIDs the memberIDs to set
	 */
	public void setMemberIDs(List<String> memberIDs) {
		this.memberIDs = memberIDs;
	}

	/**
	 * @return the memberTypeValidated
	 */
	public boolean isMemberTypeValidated() {
		return memberTypeValidated;
	}

	/**
	 * @param memberTypeValidated the memberTypeValidated to set
	 */
	public void setMemberTypeValidated(boolean memberTypeValidated) {
		this.memberTypeValidated = memberTypeValidated;
	}

	/**
	 * @return the consistencyStrategy
	 */
	public BigInteger getConsistencyStrategy() {
		return consistencyStrategy;
	}

	/**
	 * @param consistencyStrategy the consistencyStrategy to set
	 */
	public void setConsistencyStrategy(BigInteger consistencyStrategy) {
		this.consistencyStrategy = consistencyStrategy;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the fanOutPoint
	 */
	public String getFanOutPoint() {
		return fanOutPoint;
	}

	/**
	 * @param fanOutPoint the fanOutPoint to set
	 */
	public void setFanOutPoint(String fanOutPoint) {
		this.fanOutPoint = fanOutPoint;
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
	 * @param accessControlPolicies the accessControlPolicies to set
	 */
	public void setAccessControlPolicies(
			List<AccessControlPolicyEntity> accessControlPolicies) {
		this.accessControlPolicies = accessControlPolicies;
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
	 * @return the parentRemoteCse
	 */
	public RemoteCSEEntity getParentRemoteCse() {
		return parentRemoteCse;
	}

	/**
	 * @param parentRemoteCse the parentRemoteCse to set
	 */
	public void setParentRemoteCse(RemoteCSEEntity parentRemoteCse) {
		this.parentRemoteCse = parentRemoteCse;
	}

	/**
	 * @return the memberAcpIds
	 */
	public List<String> getMemberAcpIds() {
		if(memberAcpIds == null){
			memberAcpIds = new ArrayList<>();
		}
		return memberAcpIds;
	}

	/**
	 * @param memberAcpIds the memberAcpIds to set
	 */
	public void setMemberAcpIds(List<String> memberAcpIds) {
		this.memberAcpIds = memberAcpIds;
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
