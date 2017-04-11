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
package org.eclipse.om2m.datamapping.service;


/**
 * Data Mapping Service to provide an binding between string representation of
 * an object and a java object.
 *
 */
public interface DataMapperService {

	/**
	 * Give the representation of a oneM2M object.
	 * 
	 * @param obj
	 *            Object to translate
	 * @return the representation of the object
	 */
	public abstract String objToString(Object ob);

	/**
	 * Give the corresponding object of a string representation.
	 * 
	 * @param obj
	 *            String to translate
	 * @return Corresponding java object
	 */
	public abstract Object stringToObj(String obj);

	/**
	 * Give the type of data that handle the service.
	 * 
	 * @return the data type
	 */
	public abstract String getServiceDataType();

}
