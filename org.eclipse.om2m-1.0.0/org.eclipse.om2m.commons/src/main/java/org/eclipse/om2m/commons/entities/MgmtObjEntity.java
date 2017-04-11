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
import javax.persistence.MappedSuperclass;

import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Generic common attributes for management objects entities
 *
 */
@MappedSuperclass
public class MgmtObjEntity extends AnnounceableSubordinateEntity {

	@Column (name = ShortName.MGMT_DEF)
	protected BigInteger mgmtDefinition;
	@Column(name = ShortName.OBJ_IDS)
	protected List<String> objectIDs;
	@Column(name = ShortName.OBJ_PATHS)
	protected List<String> objectPaths;
	@Column(name = ShortName.DESCRIPTION)
	protected String description;
	
	/**
	 * @return the mgmtDefinition
	 */
	public BigInteger getMgmtDefinition() {
		return mgmtDefinition;
	}
	/**
	 * @return the objectIDs
	 */
	public List<String> getObjectIDs() {
		if (this.objectIDs == null) {
			this.objectIDs = new ArrayList<>();
		}
		return objectIDs;
	}
	/**
	 * @param objectIDs the objectIDs to set
	 */
	public void setObjectIDs(List<String> objectIDs) {
		this.objectIDs = objectIDs;
	}
	/**
	 * @return the objectPaths
	 */
	public List<String> getObjectPaths() {
		if (this.objectPaths == null) {
			this.objectPaths = new ArrayList<>();
		}
		return objectPaths;
	}
	/**
	 * @param objectPaths the objectPaths to set
	 */
	public void setObjectPaths(List<String> objectPaths) {
		this.objectPaths = objectPaths;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @param mgmtDefinition the mgmtDefinition to set
	 */
	public void setMgmtDefinition(BigInteger mgmtDefinition) {
		this.mgmtDefinition = mgmtDefinition;
	}

}
