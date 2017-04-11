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
package org.eclipse.om2m.core.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.ConsistencyStrategy;
import org.eclipse.om2m.commons.constants.MemberType;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.MemberNonFoundException;
import org.eclipse.om2m.commons.exceptions.MemberTypeInconsistentException;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.router.Patterns;
import org.eclipse.om2m.core.urimapper.UriMapper;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

public class GroupUtil {

	private static final Log LOGGER = LogFactory.getLog(GroupUtil.class);

	private GroupUtil(){}

	public static void validateGroupMember(GroupEntity group) 
			throws MemberNonFoundException, MemberTypeInconsistentException{
		// if the member type is not validated
		if(!group.isMemberTypeValidated() && !group.getMemberType().equals(MemberType.MIXED)){
			LOGGER.info("Validating the memberType of " + group.getHierarchicalURI());
			// get the database service 
			DBService dbs = PersistenceService.getInstance().getDbService();
			DBTransaction transaction = dbs.getDbTransaction();
			transaction.open();
			List<String> result = new ArrayList<String>();
			// for each member of the group
			for(String memberUri : group.getMemberIDs()){
				String nonHierarchicalUri = UriMapper.getNonHierarchicalUri(memberUri);
				if(nonHierarchicalUri == null){
					transaction.close();
					throw new MemberNonFoundException("Member not found: " + memberUri);
				}
				
				DAO<ResourceEntity> dao = (DAO<ResourceEntity>) Patterns.getDAO(nonHierarchicalUri, dbs);
				if(dao == null){
					transaction.close();
					throw new MemberNonFoundException("Member not found: " + memberUri);
				}
				ResourceEntity entity = dao.find(transaction, nonHierarchicalUri);
				if(entity == null){
					transaction.close();
					throw new MemberNonFoundException("Member not found: " + memberUri);
				}
				result.add(memberUri);				
				if(!entity.getResourceType().equals(group.getMemberType())){
					if(group.getConsistencyStrategy().equals(ConsistencyStrategy.ABANDON_GROUP)){
						transaction.close();
						throw new MemberTypeInconsistentException(
								"MemberType is inconsistent and consistency strategy is set to ABANDON_GROUP");
					} else if(group.getConsistencyStrategy().equals(ConsistencyStrategy.ABANDON_MEMBER)){
						LOGGER.info("Member deleted: " + memberUri);
						result.remove(memberUri);
					} else {
						LOGGER.info("Changing the memberType to MIXED");
						group.setMemberType(MemberType.MIXED);
						break;
					}
				}
			}
			if(result.isEmpty()){
				throw new BadRequestException("All member IDs are incorrect (according to the provided type)");
			}
			group.setMemberIDs(result);
			group.setMemberTypeValidated(true);
			transaction.close();
		}
	}

}
