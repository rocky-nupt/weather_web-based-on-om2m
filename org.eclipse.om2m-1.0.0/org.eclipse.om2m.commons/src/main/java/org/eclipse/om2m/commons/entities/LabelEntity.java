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
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Label JPA entity
 *
 */
@Entity(name = DBEntities.LABEL_ENTITY)
public class LabelEntity {

	@Id
	@Column(name = ShortName.LABELS)
	protected String label;

	// TODO add link to RESOURCES
	
	@ManyToMany(targetEntity = CSEBaseEntity.class, mappedBy = "labelsEntities")
	protected List<AeEntity> linkedCsb;

	@ManyToMany(targetEntity = AeEntity.class, mappedBy = "labelsEntities")
	protected List<AeEntity> linkedAe;

	@ManyToMany(targetEntity = ContainerEntity.class, mappedBy = "labelsEntities")
	protected List<ContainerEntity> linkedCnt;

	@ManyToMany(targetEntity = ContentInstanceEntity.class, mappedBy = "labelsEntities")
	protected List<ContentInstanceEntity> linkedCin;

	@ManyToMany(targetEntity = GroupEntity.class, mappedBy = "labelsEntities")
	protected List<ContentInstanceEntity> linkedGroup;

	@ManyToMany(targetEntity = RemoteCSEEntity.class, mappedBy = "labelsEntities")
	protected List<RemoteCSEEntity> linkedCsr;

	@ManyToMany(targetEntity = SubscriptionEntity.class, mappedBy = "labelsEntities")
	protected List<SubscriptionEntity> linkedSub;

	@ManyToMany(targetEntity = PollingChannelEntity.class, mappedBy = "labelsEntities")
	protected List<PollingChannelEntity> linkedPch;
	
	@ManyToMany(targetEntity = NodeEntity.class, mappedBy = "labelsEntities")
	protected List<NodeEntity> linkedNodes;
	
	@ManyToMany(targetEntity = AreaNwkInfoEntity.class, mappedBy = "labelsEntities")
	protected List<AreaNwkInfoEntity> linkedAni;
	
	@ManyToMany(targetEntity = AreaNwkDeviceInfoEntity.class, mappedBy = "labelsEntities")
	protected List<AreaNwkDeviceInfoEntity> linkedAndi;
	
	/**
	 * @return the linkedSub
	 */
	public List<SubscriptionEntity> getLinkedSub() {
		if(linkedSub == null){
			linkedSub = new ArrayList<>();
		}
		return linkedSub;
	}

	/**
	 * @param linkedSub the linkedSub to set
	 */
	public void setLinkedSub(List<SubscriptionEntity> linkedSub) {
		this.linkedSub = linkedSub;
	}

	/**
	 * Constructor
	 */
	public LabelEntity() {
	}

	/**
	 * Constructor with label in argument
	 * @param string label to store
	 */
	public LabelEntity(String string) {
		this.label = string;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	
	
	/**
	 * @return the linkedCsb
	 */
	public List<AeEntity> getLinkedCsb() {
		if (this.linkedCsb == null) {
			this.linkedCsb = new ArrayList<>();
		}
		return linkedCsb;
	}

	/**
	 * @param linkedCsb the linkedCsb to set
	 */
	public void setLinkedCsb(List<AeEntity> linkedCsb) {
		this.linkedCsb = linkedCsb;
	}

	/**
	 * @return the linkedAe
	 */
	public List<AeEntity> getLinkedAe() {
		if (this.linkedAe == null) {
			this.linkedAe = new ArrayList<>();
		}
		return linkedAe;
	}

	/**
	 * @param linkedAe the linkedAe to set
	 */
	public void setLinkedAe(List<AeEntity> linkedAe) {
		this.linkedAe = linkedAe;
	}

	/**
	 * @return the linkedCnt
	 */
	public List<ContainerEntity> getLinkedCnt() {
		if (this.linkedCnt == null) {
			this.linkedCnt = new ArrayList<>();
		}
		return linkedCnt;
	}

	/**
	 * @param linkedCnt the linkedCnt to set
	 */
	public void setLinkedCnt(List<ContainerEntity> linkedCnt) {
		this.linkedCnt = linkedCnt;
	}

	/**
	 * @return the linkedCin
	 */
	public List<ContentInstanceEntity> getLinkedCin() {
		if (this.linkedCin == null) {
			this.linkedCin = new ArrayList<>();
		}
		return linkedCin;
	}

	/**
	 * @param linkedCin the linkedCin to set
	 */
	public void setLinkedCin(List<ContentInstanceEntity> linkedCin) {
		this.linkedCin = linkedCin;
	}

	/**
	 * @return the linkedGroup
	 */
	public List<ContentInstanceEntity> getLinkedGroup() {
		return linkedGroup;
	}

	/**
	 * @param linkedGroup the linkedGroup to set
	 */
	public void setLinkedGroup(List<ContentInstanceEntity> linkedGroup) {
		this.linkedGroup = linkedGroup;
	}

	/**
	 * @return the linkedCsr
	 */
	public List<RemoteCSEEntity> getLinkedCsr() {
		if (this.linkedCsr == null) {
			this.linkedCsr = new ArrayList<>();
		}
		return linkedCsr;
	}

	/**
	 * @param linkedCsr the linkedCsr to set
	 */
	public void setLinkedCsr(List<RemoteCSEEntity> linkedCsr) {
		this.linkedCsr = linkedCsr;
	}

	/**
	 * @return the linkedPch
	 */
	public List<PollingChannelEntity> getLinkedPch() {
		if (this.linkedPch == null) {
			this.linkedPch = new ArrayList<>();
		}
		return linkedPch;
	}

	/**
	 * @param linkedPch the linkedPch to set
	 */
	public void setLinkedPch(List<PollingChannelEntity> linkedPch) {
		this.linkedPch = linkedPch;
	}
	
	
	

	/**
	 * @return the linkedNode
	 */
	public List<NodeEntity> getLinkedNodes() {
		if (this.linkedNodes == null) {
			this.linkedNodes = new ArrayList<>();
		}
		return linkedNodes;
	}

	/**
	 * @param linkedNode the linkedNode to set
	 */
	public void setLinkedNodes(List<NodeEntity> linkedNode) {
		this.linkedNodes = linkedNode;
	}

	/**
	 * @return the linkedAni
	 */
	public List<AreaNwkInfoEntity> getLinkedAni() {
		if (this.linkedAni == null) {
			this.linkedAni = new ArrayList<>();
		}
		return linkedAni;
	}

	/**
	 * @param linkedAni the linkedAni to set
	 */
	public void setLinkedAni(List<AreaNwkInfoEntity> linkedAni) {
		this.linkedAni = linkedAni;
	}

	/**
	 * @return the linkedAndi
	 */
	public List<AreaNwkDeviceInfoEntity> getLinkedAndi() {
		if (this.linkedAndi == null) {
			this.linkedAndi = new ArrayList<>();
		}
		return linkedAndi;
	}

	/**
	 * @param linkedAndi the linkedAndi to set
	 */
	public void setLinkedAndi(List<AreaNwkDeviceInfoEntity> linkedAndi) {
		this.linkedAndi = linkedAndi;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;			
		}
		if (obj == null){
			return false;			
		}
		if (getClass() != obj.getClass()){
			return false;			
		}
		LabelEntity other = (LabelEntity) obj;
		if (label == null) {
			if (other.label != null){
				return false;				
			}
		} else if (!label.equals(other.label)){
			return false;			
		}
		return true;
	}
	
}
