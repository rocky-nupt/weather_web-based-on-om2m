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
package org.eclipse.om2m.persistence.eclipselink.internal.dao;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.om2m.commons.entities.LabelEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.persistence.eclipselink.internal.DBTransactionJPAImpl;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBTransaction;

public abstract class AbstractDAO<T> implements DAO<T>{

	@Override
	public void create(DBTransaction dbTransaction, T resource) {
		DBTransactionJPAImpl transaction = (DBTransactionJPAImpl) dbTransaction;
		if (resource instanceof ResourceEntity) {
			List<LabelEntity> lbls = processLabels(dbTransaction, ((ResourceEntity)resource).getLabelsEntities());
			((ResourceEntity) resource).setLabelsEntities(lbls);
		}
		transaction.getEm().persist(resource);	
	}

	@Override
	public abstract T find(DBTransaction dbTransaction, Object id);

	@Override
	public void update(DBTransaction dbTransaction, T resource) {
		DBTransactionJPAImpl transaction = (DBTransactionJPAImpl) dbTransaction;
		transaction.getEm().flush();	
	}

	@Override
	public abstract void delete(DBTransaction dbTransaction, T resource);

	protected List<LabelEntity> processLabels(DBTransaction dbTransaction, List<LabelEntity> toProcess) {
		List<LabelEntity> result = new ArrayList<>();
		DBTransactionJPAImpl transaction = (DBTransactionJPAImpl) dbTransaction;
		for (LabelEntity lbl : toProcess) {
			LabelEntity lblDb = transaction.getEm().find(LabelEntity.class, lbl.getLabel());
			if (lblDb == null) {
				lblDb = new LabelEntity(lbl.getLabel());
				transaction.getEm().persist(lblDb);
				lblDb = transaction.getEm().find(LabelEntity.class, lbl.getLabel());
			}
			result.add(lblDb);
		}
		return result;
	}

}
