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
package org.eclipse.om2m.persistence.eclipselink.internal.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.entities.UriMapperEntity;
import org.eclipse.om2m.commons.resource.FilterCriteria;
import org.eclipse.om2m.persistence.eclipselink.internal.DBTransactionJPAImpl;
import org.eclipse.om2m.persistence.service.util.ComplexFindUtil;

public class ComplexFindUtilImpl implements ComplexFindUtil {

	@Override
	public List<UriMapperEntity> getChildUrisDis(String rootUri, FilterCriteria filter) {
		DBTransactionJPAImpl transaction = new DBTransactionJPAImpl();
		transaction.open();
		
		String req = "SELECT uri.hierarchicalUri FROM " + DBEntities.URI_MAPPER_ENTITY + 
				" uri WHERE uri.nonHierarchicalUri = '" + rootUri + "'"  ; 
		Query q = transaction.getEm().createQuery(req);
		List<String> resultList = q.getResultList();
		if(resultList.size() != 1){
			return new ArrayList<>();
		}
		String hierarchicalUri = resultList.get(0);
		
		req = "SELECT uri FROM " + DBEntities.URI_MAPPER_ENTITY
				+ " uri WHERE uri.hierarchicalUri LIKE '"
				+ hierarchicalUri + "%'";
		if (filter.getResourceType() != null){
			req += " AND uri.resourceType = '" + filter.getResourceType() + "'"; 
		}
		
		q = transaction.getEm().createQuery(req);
		
		if (filter.getLimit() != null && filter.getLimit().intValue() > 0){
			q.setMaxResults(filter.getLimit().intValue());
		}
		
		List<UriMapperEntity> resultListEntities = q.getResultList();
		transaction.close();
		return resultListEntities;
	}
}
