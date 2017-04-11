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
import javax.persistence.MappedSuperclass;

import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Generic attributes for announceable subordinate JPA entities
 *
 */
@MappedSuperclass
public class AnnounceableSubordinateEntity extends ResourceEntity {
	@Column(name=ShortName.EXPIRATION_TIME)
	protected String expirationTime;
	@Column(name=ShortName.ANNOUNCE_TO)
	protected List<String> announceTo;
	@Column(name=ShortName.ANNOUNCED_ATTRIBUTE)
	protected List<String> announcedAttribute;

	/**
	 * Gets the value of the expirationTime property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExpirationTime() {
		return expirationTime;
	}

	/**
	 * Sets the value of the expirationTime property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExpirationTime(String value) {
		this.expirationTime = value;
	}

	/**
	 * Gets the value of the announceTo property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the announceTo property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAnnounceTo().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public List<String> getAnnounceTo() {
		if (announceTo == null) {
			announceTo = new ArrayList<String>();
		}
		return this.announceTo;
	}

	/**
	 * Gets the value of the announcedAttribute property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the announcedAttribute property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAnnouncedAttribute().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public List<String> getAnnouncedAttribute() {
		if (announcedAttribute == null) {
			announcedAttribute = new ArrayList<String>();
		}
		return this.announcedAttribute;
	}

}
