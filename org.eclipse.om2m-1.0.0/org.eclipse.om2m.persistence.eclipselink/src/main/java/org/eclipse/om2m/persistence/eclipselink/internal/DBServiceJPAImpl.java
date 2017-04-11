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
package org.eclipse.om2m.persistence.eclipselink.internal;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.persistence.eclipselink.internal.util.DBUtilManagerImpl;
import org.eclipse.om2m.persistence.service.DAOFactory;
import org.eclipse.om2m.persistence.service.DBConstants;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;
import org.eclipse.om2m.persistence.service.util.DBUtilManager;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Class handling the connection to the database using JPA - EclipseLink.
 * Satisfies the singleton pattern. Implements the DBService Interface.
 */
public class DBServiceJPAImpl implements DBService {
	/** LOGGER */
	private static final Log LOGGER = LogFactory.getLog(DBServiceJPAImpl.class);


	/** Entity Manager Factory connected to the DB */
	private EntityManagerFactory emf;
	
	private DAOFactoryImpl daoFactory;


	/**
	 * Private main constructor
	 */
	private DBServiceJPAImpl() {
		this.daoFactory = new DAOFactoryImpl();
	}

	/** Local instance of the object */
	private static DBServiceJPAImpl dbService = new DBServiceJPAImpl();

	/**
	 * Gets an instance of the current class. The first time, it loads all
	 * informations from persistence.xml and initializes the database.
	 * 
	 * @return instance of DBServiceJPAImpl
	 */
	public static DBServiceJPAImpl getInstance() {
		return dbService;
	}

	public void init() {
		LOGGER.info("Initializing Database...");
		try {
			Map<Object, Object> properties = new HashMap<Object, Object>();
			properties.put(PersistenceUnitProperties.JDBC_DRIVER,
					DBConstants.DB_DRIVER);
			properties.put(PersistenceUnitProperties.JDBC_URL,
					DBConstants.DB_URL);
			properties.put(PersistenceUnitProperties.JDBC_USER,
					DBConstants.DB_USER);
			properties.put(PersistenceUnitProperties.JDBC_PASSWORD,
					DBConstants.DB_PASSWORD);
			properties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, DBConstants.DB_CACHE);
			
			if(DBConstants.DB_VERBOSE){
				properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "FINE");
			} else {
				properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "SEVERE");				
			}
			
			if (DBConstants.DB_RESET) {
				properties.put(PersistenceUnitProperties.DDL_GENERATION,
						PersistenceUnitProperties.DROP_AND_CREATE);
			} else {
				properties.put(PersistenceUnitProperties.DDL_GENERATION,
						PersistenceUnitProperties.CREATE_OR_EXTEND);
			}

			LOGGER.info("Creating new EntityManagerFactory...");
			emf = Persistence.createEntityManagerFactory("om2mdb", properties);
		} catch (Exception e) {
			LOGGER.error("Error in creation of EntityManagerFactory", e);
		}
		if (emf != null) {
			LOGGER.info("DataBase initialized.");
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			em.close();
		} else {
			LOGGER.error("ERROR initializing Database: EntityManagerFactory is null!");
		}
	}

	/**
	 * Closes the connection to the database.
	 */
	public void close() {
		if (emf != null) {
			emf.close();
		}
	}

	/**
	 * Returns an EntityManager to access the database.
	 * 
	 * @return Entity Manager
	 */
	public static EntityManager createEntityManager() {
		return getInstance().emf.createEntityManager();
	}

	@Override
	public DBTransaction getDbTransaction() {
		return new DBTransactionJPAImpl();
	}

	@Override
	public DAOFactory getDAOFactory() {
		return daoFactory;
	}

	@Override
	public DBUtilManager getDBUtilManager() {
		return new DBUtilManagerImpl();
	}

}
