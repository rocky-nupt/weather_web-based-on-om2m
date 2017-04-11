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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.eclipse.om2m.commons.constants.ShortName;
/**
 * Remote CSE announced JPA entity
 *
 */
@Entity(name = ShortName.CSRA)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class RemoteCseAnncEntity extends ResourceEntity {
	
	// TODO acp ids ???
	
	@Column(name = ShortName.EXPIRATION_TIME)
	protected String expirationTime;
	@Column(name = ShortName.LINK)
	protected String link;
	
	@Column(name=ShortName.CSE_TYPE)
	protected BigInteger cseType;
	@Column(name = ShortName.POA)
	protected List<String> pointOfAccess;
	@Column(name = ShortName.CSE_BASE)
	protected String remoteCseUri;
	@Column(name = ShortName.CSE_ID)
	protected String remoteCseId ;
	@Column(name = ShortName.REQUEST_REACHABILITY)
	protected boolean requestReachability;
	@Column(name = ShortName.NODE_LINK)
	protected String nodeLink;
	
	// TODO add child links
	// TODO link AE child
	// TODO link AeAnnc
	// TODO link Cnt
	// TODO link cntAnnc
	// TODO link grp
	// TODO link grpA
	// TODO link acp
	// TODO link acpA
	// TODO link sub
	// TODO link pch
	// TODO link schA
	// TODO link nodeA
	// TODO link locationpolicyA
	
	
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
	
}
