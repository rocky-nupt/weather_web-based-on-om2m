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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.eclipse.om2m.commons.constants.DBEntities;

/**
 * Access control rule JPA entity
 *
 */
@Entity(name = DBEntities.ACCESSCONTROLRULE_ENTITY)
public class AccessControlRuleEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name=DBEntities.ACCESSCONTROLRULE_ID)
	private String accessControlRuleId;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	// TODO force names for join table
	protected List<AccessControlOriginatorEntity> accessControlOriginators;
	
	/** boolean flags for operations permissions */
	protected boolean create = false;
	protected boolean retrieve = false;
	protected boolean delete = false;
	protected boolean update = false;
	protected boolean notify = false;
	protected boolean discovery = false;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	protected List<AccessControlContextEntity> accessControlContexts;

	/**
	 * @return the accessControlOriginators
	 */
	public List<AccessControlOriginatorEntity> getAccessControlOriginators() {
		if (accessControlOriginators == null){
			accessControlOriginators = new ArrayList<>();
		}
		return accessControlOriginators;
	}

	/**
	 * @param accessControlOriginators the accessControlOriginators to set
	 */
	public void setAccessControlOriginators(
			List<AccessControlOriginatorEntity> accessControlOriginators) {
		this.accessControlOriginators = accessControlOriginators;
	}

	/**
	 * @return the create
	 */
	public boolean isCreate() {
		return create;
	}

	/**
	 * @param create the create to set
	 */
	public void setCreate(boolean create) {
		this.create = create;
	}

	/**
	 * @return the retrieve
	 */
	public boolean isRetrieve() {
		return retrieve;
	}

	/**
	 * @param retrieve the retrieve to set
	 */
	public void setRetrieve(boolean retrieve) {
		this.retrieve = retrieve;
	}

	/**
	 * @return the delete
	 */
	public boolean isDelete() {
		return delete;
	}

	/**
	 * @param delete the delete to set
	 */
	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	/**
	 * @return the update
	 */
	public boolean isUpdate() {
		return update;
	}

	/**
	 * @param update the update to set
	 */
	public void setUpdate(boolean update) {
		this.update = update;
	}

	/**
	 * @return the notify
	 */
	public boolean isNotify() {
		return notify;
	}

	/**
	 * @param notify the notify to set
	 */
	public void setNotify(boolean notify) {
		this.notify = notify;
	}

	/**
	 * @return the discovery
	 */
	public boolean isDiscovery() {
		return discovery;
	}

	/**
	 * @param discovery the discovery to set
	 */
	public void setDiscovery(boolean discovery) {
		this.discovery = discovery;
	}

	/**
	 * @return the accessControlContexts
	 */
	public List<AccessControlContextEntity> getAccessControlContexts() {
		if (accessControlContexts == null){
			accessControlContexts = new ArrayList<>();
		}
		return accessControlContexts;
	}

	/**
	 * @param accessControlContexts the accessControlContexts to set
	 */
	public void setAccessControlContexts(
			List<AccessControlContextEntity> accessControlContexts) {
		this.accessControlContexts = accessControlContexts;
	}
	
	
	
}
