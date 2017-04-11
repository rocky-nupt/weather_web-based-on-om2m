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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Content Instance Entity for persistence.
 *
 */
@Entity(name = DBEntities.CONTENTINSTANCE_ENTITY)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ContentInstanceEntity extends AnnounceableSubordinateEntity {
	
	@Column(name = ShortName.STATETAG, nullable = false)
	protected BigInteger stateTag;
	
	@Column(name = ShortName.CREATOR)
	protected String creator;
	
	@Column(name = ShortName.CONTENT_INFO)
	protected String contentInfo = MimeMediaType.TEXT_PLAIN 
			+ MimeMediaType.CONTENT_INFO_SEPARATOR + MimeMediaType.ENCOD_PLAIN;
	
	@Column(name = ShortName.CONTENT_SIZE)
	protected BigInteger contentSize;
	
	@Column(name = ShortName.ONTOLOGY_REF)
	protected String ontologyRef;
	
	@Column(name = ShortName.CONTENT, nullable = false)
	@Lob
	protected String content;
	
	@ManyToOne(fetch=FetchType.EAGER, targetEntity = ContainerEntity.class)
	@JoinTable(
			name=DBEntities.CNTCINCHILD_JOIN,
			inverseJoinColumns={@JoinColumn(name=DBEntities.CNT_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			joinColumns={@JoinColumn(name=DBEntities.CINCH_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected ContainerEntity parentContainer;
	
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
	 * @return the contentInfo
	 */
	public String getContentInfo() {
		return contentInfo;
	}
	/**
	 * @param contentInfo the contentInfo to set
	 */
	public void setContentInfo(String contentInfo) {
		this.contentInfo = contentInfo;
	}
	/**
	 * @return the contentSize
	 */
	public BigInteger getContentSize() {
		return contentSize;
	}
	/**
	 * @param contentSize the contentSize to set
	 */
	public void setContentSize(BigInteger contentSize) {
		this.contentSize = contentSize;
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
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
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
	 * Return the acp list from the parent entity: container or containerAnnc
	 * @return acp list, null if not found
	 */
	public List<AccessControlPolicyEntity> getAcpListFromParent() {
		if (this.getParentContainer() != null) {
			return this.getParentContainer().getAccessControlPolicies();
		}
		// TODO add case parent ContainerAnnc
		return null;
	}
	
	/**
	 * Return the current byte size of the content of the content instance
	 * @return
	 */
	public int getByteSize() {
		return this.content.getBytes().length;
	}

}
