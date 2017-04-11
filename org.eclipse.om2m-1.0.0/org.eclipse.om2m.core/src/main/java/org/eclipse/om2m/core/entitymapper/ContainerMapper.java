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

import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.entities.ContentInstanceEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.resource.ChildResourceRef;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.Subscription;

public class ContainerMapper extends EntityMapper<ContainerEntity, Container>{

	@Override
	protected Container createResource() {
		return new Container();
	}

	@Override
	protected void mapAttributes(ContainerEntity entity, Container resource) {
		resource.setCreator(entity.getCreator());
		resource.setCurrentByteSize(BigInteger.valueOf(entity.getCurrentByteSize()));
		resource.setCurrentNrOfInstances(BigInteger.valueOf(entity.getChildContentInstances().size()));
		resource.setLocationID(entity.getLocationID());
		resource.setMaxByteSize(entity.getMaxByteSize());
		resource.setMaxInstanceAge(entity.getMaxInstanceAge());
		resource.setMaxNrOfInstances(entity.getMaxNrOfInstances());
		resource.setOntologyRef(entity.getOntologyRef());
		resource.setStateTag(entity.getStateTag());
		resource.setExpirationTime(entity.getExpirationTime());
		if (!entity.getAnnouncedAttribute().isEmpty()) {			
			resource.getAnnouncedAttribute().addAll(entity.getAnnouncedAttribute());
		}
		if (!entity.getAnnounceTo().isEmpty()) {			
			resource.getAnnounceTo().addAll(entity.getAnnounceTo());
		}
		// setting acpIds
		for (AccessControlPolicyEntity acp : entity.getAccessControlPolicies()) {
			resource.getAccessControlPolicyIDs().add(acp.getResourceID());
		}
		resource.setOldest(entity.getHierarchicalURI() + "/" + ShortName.OLDEST);
		resource.setLatest(entity.getHierarchicalURI() + "/" + ShortName.LATEST);
	}

	@Override
	protected void mapChildResourceRef(ContainerEntity entity,
			Container resource) {

		// add child ref contentInstance
		for (ContentInstanceEntity cin : entity.getChildContentInstances()) {
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(cin.getName());
			child.setType(ResourceType.CONTENT_INSTANCE);
			child.setValue(cin.getResourceID());
			resource.getChildResource().add(child);	
		}

		// add child ref subscription
		for (SubscriptionEntity sub : entity.getSubscriptions()){
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(sub.getName());
			child.setType(ResourceType.SUBSCRIPTION);
			child.setValue(sub.getResourceID());
			resource.getChildResource().add(child);
		}
		
		
		// add child ref with containers
		for (ContainerEntity childCont : entity.getChildContainers()) {
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(childCont.getName());
			child.setType(ResourceType.CONTAINER);
			child.setValue(childCont.getResourceID());
			resource.getChildResource().add(child);
		}
	}

	@Override
	protected void mapChildResources(ContainerEntity entity, Container resource) {
		// add child ref contentInstance
		for (ContentInstanceEntity cin : entity.getChildContentInstances()) {
			ContentInstance cinRes = new ContentInstanceMapper().mapEntityToResource(cin, ResultContent.ATTRIBUTES);
			resource.getContentInstanceOrContainerOrSubscription().add(cinRes);
		}

		// add child ref subscription
		for (SubscriptionEntity sub : entity.getSubscriptions()){
			Subscription subRes = new SubscriptionMapper().mapEntityToResource(sub, ResultContent.ATTRIBUTES);
			resource.getContentInstanceOrContainerOrSubscription().add(subRes);
		}
		
		
		// add child ref with containers
		for (ContainerEntity childCont : entity.getChildContainers()) {
			Container cnt = new ContainerMapper().mapEntityToResource(childCont, ResultContent.ATTRIBUTES);
			resource.getContentInstanceOrContainerOrSubscription().add(cnt);
		}
	}

	
}
