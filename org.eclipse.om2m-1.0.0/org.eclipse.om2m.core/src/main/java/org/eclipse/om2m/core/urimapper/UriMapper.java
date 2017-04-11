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
package org.eclipse.om2m.core.urimapper;

import org.eclipse.om2m.commons.entities.UriMapperEntity;
import org.eclipse.om2m.commons.utils.UriUtil;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.router.Patterns;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

/**
 * Mapper between hierarchical uri and non-hierarchical uri
 *
 */
public class UriMapper {

	/**
	 * Get the non-hierarchical URI of a resource from its hierarchical one.
	 * 
	 * @param hierarchicalUri
	 *            of the resource
	 * @return non-hierarchical Uri of the resource
	 */
	public static String getNonHierarchicalUri(String hierarchicalUri) {
		String spRelativeUri = UriUtil.toSpRelativeUri(hierarchicalUri);
		if (Patterns.match(Patterns.NON_HIERARCHICAL_PATTERN, spRelativeUri)){
			return spRelativeUri;
		}
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();
		UriMapperEntity entity = dbs.getDAOFactory().getUriMapperEntity()
				.find(transaction, spRelativeUri);
		transaction.close();
		if (entity != null) {
			return entity.getNonHierarchicalUri();
		}
		return null;
	}

	/**
	 * Add a new entry for hierarchical uri mapping
	 * 
	 * @param hierarchicalUri
	 *            of the resource
	 * @param nonHierarchicalUri
	 *            of the resource
	 * @return true if succeed
	 */
	public static boolean addNewUri(String hierarchicalUri,
			String nonHierarchicalUri, int resourceType) {
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();
		if (dbs.getDAOFactory().getUriMapperEntity()
				.find(transaction, hierarchicalUri) != null) {
			transaction.close();
			return false;
		}
		UriMapperEntity entity = new UriMapperEntity();
		entity.setHierarchicalUri(hierarchicalUri);
		entity.setNonHierarchicalUri(nonHierarchicalUri);
		entity.setResourceType(resourceType);
		dbs.getDAOFactory().getUriMapperEntity().create(transaction, entity);
		transaction.commit();
		transaction.close();
		return true;
	}

	/**
	 * Delete an entry of hierarchical uri mapping
	 * 
	 * @param hierarchicalUri
	 *            of the resource
	 */
	public static void deleteUri(String hierarchicalUri) {
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();
		UriMapperEntity entity = dbs.getDAOFactory().getUriMapperEntity()
				.find(transaction, hierarchicalUri);
		if (entity != null){
			dbs.getDAOFactory().getUriMapperEntity().delete(transaction, entity);
			transaction.commit();			
		}
		transaction.close();
	}

}
