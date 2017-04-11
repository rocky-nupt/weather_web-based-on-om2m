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
package org.eclipse.om2m.persistence.service;

/**
 * Generic DAO
 * 
 * The DAO (Data Object Access) Pattern is used to make separation between data
 * access layer and the business layer of an application. It allows better
 * control of changes that might be made on the system of data storage and
 * therefore to prepare a migration from one system to another (DB to XML files
 * for example). This is done by separating data access (BDD) and business
 * objects (POJOs).
 * 
 * @param <T>
 *            Type of the resource
 */
public interface DAO<T> {
	/** Create a resource using the database transaction */
	public abstract void create(DBTransaction dbTransaction, T resource);

	/** Load an object from the database using the transaction */
	public abstract T find(DBTransaction dbTransaction, Object id);

	/** Update an object in the database using the transaction */
	public abstract void update(DBTransaction dbTransaction, T resource);

	/** Delete an object in the database */
	public abstract void delete(DBTransaction dbTransaction, T resource);

}
