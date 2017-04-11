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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Ae announced JPA entity
 *
 */
@Entity(name = ShortName.AE_ANNC)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AeAnncEntity extends ResourceEntity {

	// TODO acp ids ???

	@Column(name = ShortName.EXPIRATION_TIME)
	protected String expirationTime;
	@Column(name = ShortName.LINK)
	protected String link;

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
	@Column(name=ShortName.NODE_LINK)
	protected String nodeLink;

	// TODO add link sub
	// TODO add link cnt
	// TODO add link cntA
	// TODO add link grp
	// TODO add link grpA
	// TODO add link acp
	// TODO add link acpA
	// TODO add link pch

	/**
	 * @return the expirationTime
	 */
	public String getExpirationTime() {
		return expirationTime;
	}
	/**
	 * @param expirationTime the expirationTime to set
	 */
	public void setExpirationTime(String expirationTime) {
		this.expirationTime = expirationTime;
	}
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}
	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}
	/**
	 * @param appName the appName to set
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
	 * @param appID the appID to set
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
	 * @param aeid the aeid to set
	 */
	public void setAeid(String aeid) {
		this.aeid = aeid;
	}
	/**
	 * @return the pointOfAccess
	 */
	public List<String> getPointOfAccess() {
		if (this.pointOfAccess == null) {
			this.pointOfAccess = new ArrayList<>();
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





}
