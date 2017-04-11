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

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;

import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Representation of the Generic Resource to be extended by
 * others entities.
 *
 */
@MappedSuperclass
public class ResourceEntity {

	@Id
	@Column(name=ShortName.RESOURCE_ID)
	protected String resourceID;
	@Column(name = ShortName.RESOURCE_TYPE)
	protected BigInteger resourceType;
	@Column(name=ShortName.PARENT_ID)
	protected String parentID;
	@Column(name=ShortName.CREATION_TIME)
	protected String creationTime;
	@Column(name=ShortName.LAST_MODIFIED_TIME)
	protected String lastModifiedTime;
	@ManyToMany(targetEntity = LabelEntity.class, fetch = FetchType.LAZY)
	protected List<LabelEntity> labelsEntities;
	
	@Column(name=ShortName.RESOURCE_NAME)
	protected String name;
	@Column(name="huri")
	protected String hierarchicalURI;

	/**
	 * Gets the value of the resourceType property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getResourceType() {
		return resourceType;
	}

	/**
	 * Sets the value of the resourceType property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setResourceType(BigInteger value) {
		this.resourceType = value;
	}

	/**
	 * Gets the value of the resourceID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getResourceID() {
		return resourceID;
	}

	/**
	 * Sets the value of the resourceID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setResourceID(String value) {
		this.resourceID = value;
	}

	/**
	 * Gets the value of the parentID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getParentID() {
		return parentID;
	}

	/**
	 * Sets the value of the parentID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setParentID(String value) {
		this.parentID = value;
	}

	/**
	 * Gets the value of the creationTime property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCreationTime() {
		return creationTime;
	}

	/**
	 * Sets the value of the creationTime property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCreationTime(String value) {
		this.creationTime = value;
	}

	/**
	 * Gets the value of the lastModifiedTime property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * Sets the value of the lastModifiedTime property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setLastModifiedTime(String value) {
		this.lastModifiedTime = value;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * @return the hierarchicalURI
	 */
	public String getHierarchicalURI() {
		return hierarchicalURI;
	}

	/**
	 * @param hierarchicalURI the hierarchicalURI to set
	 */
	public void setHierarchicalURI(String hierarchicalURI) {
		this.hierarchicalURI = hierarchicalURI;
	}

	/**
	 * @return the labelsEntities
	 */
	public List<LabelEntity> getLabelsEntities() {
		if (this.labelsEntities == null) {
			this.labelsEntities = new ArrayList<>();
		}
		return labelsEntities;
	}

	/**
	 * @param labelsEntities the labelsEntities to set
	 */
	public void setLabelsEntities(List<LabelEntity> labelsEntities) {
		this.labelsEntities = labelsEntities;
	}
	
	/**
	 * @param labelsEntities the labelsEntities to set
	 */
	public void setLabelsEntitiesFromSring(List<String> labelsStrings) {
		this.getLabelsEntities().clear();
		for (String s: labelsStrings) {
			LabelEntity toAdd = new LabelEntity(s);
			if(!this.getLabelsEntities().contains(toAdd)){
				this.getLabelsEntities().add(new LabelEntity(s));				
			}
		}
	}
	
	public void setResourceType(int value){
		this.resourceType = BigInteger.valueOf(value);
	}
	

}
