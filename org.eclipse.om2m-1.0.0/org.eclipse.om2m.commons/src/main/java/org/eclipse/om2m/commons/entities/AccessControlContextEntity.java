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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.resource.LocationRegion;

/**
 * Access Control Context JPA Entity
 *
 */
@Entity(name=DBEntities.ACCESSCONTROLCONTEXT_ENTITY)
public class AccessControlContextEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String accessControlContextId;

	protected List<String> accessControlWindow;

	protected List<String> ipv4Addresses;

	protected List<String> ipv6Addresses;

	//TODO to store as an external entity to avoid duplication of data
	@Transient
	protected LocationRegion accessControlLocationRegion;

	/**
	 * @return the accessControlWindow
	 */
	public List<String> getAccessControlWindow() {
		if (accessControlWindow == null){
			accessControlWindow = new ArrayList<>();
		}
		return accessControlWindow;
	}

	/**
	 * @param accessControlWindow the accessControlWindow to set
	 */
	public void setAccessControlWindow(List<String> accessControlWindow) {
		this.accessControlWindow = accessControlWindow;
	}

	/**
	 * @return the ipv4Addresses
	 */
	public List<String> getIpv4Addresses() {
		if (ipv4Addresses == null){
			ipv4Addresses = new ArrayList<>();
		}
		return ipv4Addresses;
	}

	/**
	 * @param ipv4Addresses the ipv4Addresses to set
	 */
	public void setIpv4Addresses(List<String> ipv4Addresses) {
		this.ipv4Addresses = ipv4Addresses;
	}

	/**
	 * @return the ipv6Addresses
	 */
	public List<String> getIpv6Addresses() {
		if (ipv6Addresses == null){
			ipv6Addresses = new ArrayList<>();
		}
		return ipv6Addresses;
	}

	/**
	 * @param ipv6Addresses the ipv6Addresses to set
	 */
	public void setIpv6Addresses(List<String> ipv6Addresses) {
		this.ipv6Addresses = ipv6Addresses;
	}

	/**
	 * @return the accessControlLocationRegion
	 */
	public LocationRegion getAccessControlLocationRegion() {
		return accessControlLocationRegion;
	}

	/**
	 * @param accessControlLocationRegion the accessControlLocationRegion to set
	 */
	public void setAccessControlLocationRegion(
			LocationRegion accessControlLocationRegion) {
		this.accessControlLocationRegion = accessControlLocationRegion;
	}
	
	
	
}
