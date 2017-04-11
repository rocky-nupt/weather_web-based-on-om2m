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
import javax.persistence.OrderBy;

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Container JPA entity
 *
 */
@Entity(name=DBEntities.CONTAINER_ENTITY)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ContainerEntity extends AnnounceableSubordinateEntity{
	@Column(name= ShortName.STATETAG)
	protected BigInteger stateTag;
	@Column(name= ShortName.CREATOR)
	protected String creator;
	@Column(name= ShortName.MAX_NR_OF_INSTANCES)
	protected BigInteger maxNrOfInstances;
	@Column(name= ShortName.MAX_BYTE_SIZE)
	protected BigInteger maxByteSize;
	@Column(name= ShortName.MAX_INSTANCE_AGE)
	protected BigInteger maxInstanceAge;
	@Column(name= ShortName.LOCATION_ID)
	protected String locationID;
	@Column(name= ShortName.ONTOLOGY_REF)
	protected String ontologyRef;
	
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(
			name = DBEntities.CNTSUB_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.CNT_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.SUB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<SubscriptionEntity> subscriptions;
	
	/** List of AccessControlPolicies */
	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(
			name = DBEntities.CNTACP_JOIN,
			joinColumns = { @JoinColumn(name = DBEntities.CNT_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			inverseJoinColumns = { @JoinColumn(name = DBEntities.ACP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected List<AccessControlPolicyEntity> accessControlPolicies;
	
	/** List of child Container Entities */
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name=DBEntities.CNTCNTCHILD_JOIN,
			joinColumns={@JoinColumn(name=DBEntities.CNT_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			inverseJoinColumns={@JoinColumn(name=DBEntities.CNTCH_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected List<ContainerEntity> childContainers;
	
	/** List of child ContentInstances Entities */
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(
			name=DBEntities.CNTCINCHILD_JOIN,
			joinColumns={@JoinColumn(name=DBEntities.CNT_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			inverseJoinColumns={@JoinColumn(name=DBEntities.CINCH_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	@OrderBy("creationTime")
	protected List<ContentInstanceEntity> childContentInstances;

	// Database link to the possible parent Container
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=ContainerEntity.class)
	@JoinTable(
			name=DBEntities.CNTCNTCHILD_JOIN,
			inverseJoinColumns={@JoinColumn(name=DBEntities.CNT_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			joinColumns={@JoinColumn(name=DBEntities.CNTCH_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected ContainerEntity parentContainer;
	
	// Database link to the possible parent Application Entity
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=AeEntity.class)
	@JoinTable(
			name = DBEntities.AECNTCHILD_JOIN,
			inverseJoinColumns = { @JoinColumn(name = DBEntities.AE_JOINID, referencedColumnName = ShortName.RESOURCE_ID) }, 
			joinColumns = { @JoinColumn(name = DBEntities.CNT_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
			)
	protected AeEntity parentAE;

	// Database link to the possible parent CSEBase Entity
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CSEBaseEntity.class)
	@JoinTable(
			name=DBEntities.CSEB_CNT_JOIN,
			inverseJoinColumns={@JoinColumn(name=DBEntities.CSEB_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			joinColumns={@JoinColumn(name=DBEntities.CNT_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected CSEBaseEntity parentCSEB;
	
	// Database link to the possible parent remote cse Entity
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=RemoteCSEEntity.class)
	@JoinTable(
			name=DBEntities.CSRCNTCHILD_JOIN,
			inverseJoinColumns={@JoinColumn(name=DBEntities.CSR_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			joinColumns={@JoinColumn(name=DBEntities.CNT_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected RemoteCSEEntity parentCSR;
	
	/**
	 * @return the parentContainer
	 */
	public ContainerEntity getParentContainer() {
		return parentContainer;
	}

	/**
	 * @param parentContainer the parentContainer to set
	 */
	public void setParentContainer(ContainerEntity parentContainer) {
		this.parentContainer = parentContainer;
	}

	/**
	 * @return the parentAE
	 */
	public AeEntity getParentAE() {
		return parentAE;
	}

	/**
	 * @param parentAE the parentAE to set
	 */
	public void setParentAE(AeEntity parentAE) {
		this.parentAE = parentAE;
	}

	/**
	 * @return the parentCSEB
	 */
	public CSEBaseEntity getParentCSEB() {
		return parentCSEB;
	}

	/**
	 * @param parentCSEB the parentCSEB to set
	 */
	public void setParentCSEB(CSEBaseEntity parentCSEB) {
		this.parentCSEB = parentCSEB;
	}

	/**
	 * @return the stateTag
	 */
	public BigInteger getStateTag() {
		return stateTag;
	}

	/**
	 * @param stateTag the stateTag to set
	 */
	public void setStateTag(BigInteger stateTag) {
		this.stateTag = stateTag;
	}

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
	 * @return the maxNrOfInstances
	 */
	public BigInteger getMaxNrOfInstances() {
		return maxNrOfInstances;
	}

	/**
	 * @param maxNrOfInstances the maxNrOfInstances to set
	 */
	public void setMaxNrOfInstances(BigInteger maxNrOfInstances) {
		this.maxNrOfInstances = maxNrOfInstances;
	}

	/**
	 * @return the maxByteSize
	 */
	public BigInteger getMaxByteSize() {
		return maxByteSize;
	}

	/**
	 * @param maxByteSize the maxByteSize to set
	 */
	public void setMaxByteSize(BigInteger maxByteSize) {
		this.maxByteSize = maxByteSize;
	}

	/**
	 * @return the maxInstanceAge
	 */
	public BigInteger getMaxInstanceAge() {
		return maxInstanceAge;
	}

	/**
	 * @param maxInstanceAge the maxInstanceAge to set
	 */
	public void setMaxInstanceAge(BigInteger maxInstanceAge) {
		this.maxInstanceAge = maxInstanceAge;
	}

	/**
	 * @return the locationID
	 */
	public String getLocationID() {
		return locationID;
	}

	/**
	 * @param locationID the locationID to set
	 */
	public void setLocationID(String locationID) {
		this.locationID = locationID;
	}

	/**
	 * @return the ontologyRef
	 */
	public String getOntologyRef() {
		return ontologyRef;
	}

	/**
	 * @param ontologyRef the ontologyRef to set
	 */
	public void setOntologyRef(String ontologyRef) {
		this.ontologyRef = ontologyRef;
	}

	/**
	 * @return the childContainers
	 */
	public List<ContainerEntity> getChildContainers() {
		if (this.childContainers == null) {
			this.childContainers = new ArrayList<>();
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
	 * @return the childContentInstances
	 */
	public List<ContentInstanceEntity> getChildContentInstances() {
		if (childContentInstances == null) {
			this.childContentInstances = new ArrayList<>();
		}
		return childContentInstances;
	}

	/**
	 * @param childContentInstances the childContentInstances to set
	 */
	public void setChildContentInstances(
			List<ContentInstanceEntity> childContentInstances) {
		this.childContentInstances = childContentInstances;
	}

	/**
	 * @return the parentCSR
	 */
	public RemoteCSEEntity getParentCSR() {
		return parentCSR;
	}

	/**
	 * @param parentCSR the parentCSR to set
	 */
	public void setParentCSR(RemoteCSEEntity parentCSR) {
		this.parentCSR = parentCSR;
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
	 * Return the current byte size of summed content instance content size in bytes
	 * @return
	 */
	public int getCurrentByteSize() {
		int result = 0;
		for (ContentInstanceEntity cin : this.getChildContentInstances()) {
			result += cin.getByteSize();
		}
		return result;
	}

}
