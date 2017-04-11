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

public class DBConstants {

	/** Boolean specifying if the database should be reset */
	public static final boolean DB_RESET = Boolean.valueOf(System.getProperty(
			"org.eclipse.om2m.dbReset", "true"));

	/** URL of the database (file, memory, server...) */
	public static final String DB_URL = System.getProperty(
			"org.eclipse.om2m.dbUrl", "jdbc:h2:./data/database");

	/** JDBC Driver used for the database */
	public static final String DB_DRIVER = System.getProperty(
			"org.eclipse.om2m.dbDriver", "org.h2.Driver");

	/** User parameter for the database */
	public static final String DB_USER = System.getProperty(
			"org.eclipse.om2m.dbUser", "om2m");

	/** User password for the database */
	public static final String DB_PASSWORD = System.getProperty(
			"org.eclipse.om2m.dbPassword", "om2m");

	/** cache parameter for the database */
	public static final String DB_CACHE = System.getProperty(
			"org.eclipse.om2m.dbCache", "true");

	/** Set the logging to a verbose mode */
	public static final boolean DB_VERBOSE = Boolean.valueOf(System
			.getProperty("org.eclipse.om2m.dbVerbose", "false"));

}
