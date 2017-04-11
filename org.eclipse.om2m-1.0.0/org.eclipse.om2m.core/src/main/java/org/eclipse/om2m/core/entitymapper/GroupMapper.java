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
package org.eclipse.om2m.core.entitymapper;

import java.math.BigInteger;

import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.resource.ChildResourceRef;
import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.Subscription;

public class GroupMapper extends EntityMapper<GroupEntity, Group>{

	@Override
	protected Group createResource() {
		return new Group();
	}

	@Override
	protected void mapAttributes(GroupEntity entity, Group resource) {
		resource.setConsistencyStrategy(entity.getConsistencyStrategy());
		resource.setExpirationTime(entity.getExpirationTime());
		resource.setCreator(entity.getCreator());
		resource.setCurrentNrOfMembers(BigInteger.valueOf(entity.getMemberIDs().size()));
		resource.setFanOutPoint(entity.getHierarchicalURI() + "/" + ShortName.FANOUTPOINT);
		resource.setGroupName(resource.getGroupName());
		resource.setMaxNrOfMembers(entity.getMaxNrOfMembers());
		resource.setMemberType(entity.getMemberType());
		resource.setMemberTypeValidated(entity.isMemberTypeValidated());
		for(AccessControlPolicyEntity acpEntity : entity.getAccessControlPolicies()){
			resource.getAccessControlPolicyIDs().add(acpEntity.getResourceID());
		}
		if (!entity.getAnnouncedAttribute().isEmpty()) {			
			resource.getAnnouncedAttribute().addAll(entity.getAnnouncedAttribute());
		}
		if (!entity.getAnnounceTo().isEmpty()) {			
			resource.getAnnounceTo().addAll(entity.getAnnounceTo());
		}
		if(!entity.getMemberIDs().isEmpty()){
			resource.getMemberIDs().addAll(entity.getMemberIDs());
		}
		if(!entity.getMemberAcpIds().isEmpty()){
			resource.getMembersAccessControlPolicyIDs().addAll(entity.getMemberAcpIds());
		}
	}

	@Override
	protected void mapChildResourceRef(GroupEntity entity, Group resource) {
		// ChildResourceRef Subscription
		for(SubscriptionEntity sub : entity.getSubscriptions()){
			ChildResourceRef ref = new ChildResourceRef();
			ref.setResourceName(sub.getName());
			ref.setType(sub.getResourceType());
			ref.setValue(sub.getResourceID());
			resource.getChildResource().add(ref);
		}
	}

	@Override
	protected void mapChildResources(GroupEntity entity, Group resource) {
		for(SubscriptionEntity sub : entity.getSubscriptions()){
			Subscription subRes = new SubscriptionMapper().mapEntityToResource(sub, ResultContent.ATTRIBUTES);
			resource.getSubscription().add(subRes);
		}
	}
	
	

}
