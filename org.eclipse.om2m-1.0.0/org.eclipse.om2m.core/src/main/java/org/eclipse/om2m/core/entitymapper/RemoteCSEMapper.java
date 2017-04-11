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

import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.PollingChannelEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.ScheduleEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.resource.AE;
import org.eclipse.om2m.commons.resource.AccessControlPolicy;
import org.eclipse.om2m.commons.resource.ChildResourceRef;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.PollingChannel;
import org.eclipse.om2m.commons.resource.RemoteCSE;
import org.eclipse.om2m.commons.resource.Subscription;

public class RemoteCSEMapper extends EntityMapper<RemoteCSEEntity, RemoteCSE>{

	@Override
	protected RemoteCSE createResource() {
		return new RemoteCSE();
	}

	@Override
	protected void mapAttributes(RemoteCSEEntity csrEntity, RemoteCSE csr) {
		csr.setCSEBase(csrEntity.getRemoteCseUri());
		csr.setCSEID(csrEntity.getRemoteCseId());
		csr.setCseType(csrEntity.getCseType());
		csr.setExpirationTime(csrEntity.getExpirationTime());
		csr.setM2MExtID(csrEntity.getM2mExtId());
		csr.setNodeLink(csrEntity.getNodeLink());
		csr.setRequestReachability(csrEntity.isRequestReachability());
		csr.setTriggerRecipientID(csrEntity.getTriggerRecipientID());
		if (!csrEntity.getAnnouncedAttribute().isEmpty()) {			
			csr.getAnnouncedAttribute().addAll(csrEntity.getAnnouncedAttribute());
		}
		if (!csrEntity.getAnnounceTo().isEmpty()) {			
			csr.getAnnounceTo().addAll(csrEntity.getAnnounceTo());
		}
		if (!csrEntity.getPointOfAccess().isEmpty()) {
			csr.getPointOfAccess().addAll(csrEntity.getPointOfAccess());
		}

		// setting acpIds
		for (AccessControlPolicyEntity acp : csrEntity.getAccessControlPolicies()) {
			csr.getAccessControlPolicyIDs().add(acp.getResourceID());
		}		
	}

	@Override
	protected void mapChildResourceRef(RemoteCSEEntity csrEntity,
			RemoteCSE csr) {
		// adding subscription refs
		for (SubscriptionEntity sub : csrEntity.getSubscriptions()){
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(sub.getName());
			child.setType(ResourceType.SUBSCRIPTION);
			child.setValue(sub.getResourceID());
			csr.getChildResource().add(child);
		}
		// adding ae ref
		for(AeEntity ae : csrEntity.getChildAes()){
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(ae.getName());
			child.setType(ResourceType.AE);
			child.setValue(ae.getResourceID());
			csr.getChildResource().add(child);
		}
		// adding acp ref
		for (AccessControlPolicyEntity acp : csrEntity.getChildAcps()){
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(acp.getName());
			child.setType(ResourceType.ACCESS_CONTROL_POLICY);
			child.setValue(acp.getResourceID());
			csr.getChildResource().add(child);
		}
		// adding cnt ref
		for (ContainerEntity container : csrEntity.getChildCnt()){
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(container.getName());
			child.setType(ResourceType.CONTAINER);
			child.setValue(container.getResourceID());
			csr.getChildResource().add(child);
		}
		// adding group ref
		for (GroupEntity group : csrEntity.getChildGrps()){
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(group.getName());
			child.setType(ResourceType.GROUP);
			child.setValue(group.getResourceID());
			csr.getChildResource().add(child);
		}
		// adding polling channel child
		for (PollingChannelEntity pollEntity : csrEntity.getPollingChannels()){
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(pollEntity.getName());
			child.setValue(pollEntity.getResourceID());
			child.setType(ResourceType.POLLING_CHANNEL);
			csr.getChildResource().add(child);
		}
		// adding schedule child
		ScheduleEntity sch = csrEntity.getLinkedSchedule();
		if (sch != null) {
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(sch.getName());
			child.setValue(sch.getResourceID());
			child.setType(ResourceType.SCHEDULE);
			csr.getChildResource().add(child);
		}
		//TODO add NODE ref
	}

	@Override
	protected void mapChildResources(RemoteCSEEntity csrEntity, RemoteCSE csr) {
		// adding subscription refs
		for (SubscriptionEntity sub : csrEntity.getSubscriptions()){
			Subscription chSub = new SubscriptionMapper().mapEntityToResource(sub, ResultContent.ATTRIBUTES);
			csr.getAEOrContainerOrGroup().add(chSub);
		}
		// adding ae ref
		for(AeEntity ae : csrEntity.getChildAes()){
			AE chAe = new AeMapper().mapEntityToResource(ae, ResultContent.ATTRIBUTES);
			csr.getAEOrContainerOrGroup().add(chAe);
		}
		// adding acp ref
		for (AccessControlPolicyEntity acp : csrEntity.getChildAcps()){
			AccessControlPolicy chAcp = new AcpMapper().mapEntityToResource(acp, ResultContent.ATTRIBUTES);
			csr.getAEOrContainerOrGroup().add(chAcp);
		}
		// adding cnt ref
		for (ContainerEntity container : csrEntity.getChildCnt()){
			Container chCnt = new ContainerMapper().mapEntityToResource(container, ResultContent.ATTRIBUTES);
			csr.getAEOrContainerOrGroup().add(chCnt);
		}
		// adding group ref
		for (GroupEntity grp : csrEntity.getChildGrps()){
			Group chGrp = new GroupMapper().mapEntityToResource(grp, ResultContent.ATTRIBUTES);
			csr.getAEOrContainerOrGroup().add(chGrp);
		}
		// adding polling channel child
		for (PollingChannelEntity pollEntity : csrEntity.getPollingChannels()){
			PollingChannel chPch = new PollingChannelMapper().mapEntityToResource(pollEntity, ResultContent.ATTRIBUTES);
			csr.getAEOrContainerOrGroup().add(chPch);
		}
		// adding schedule child
		ScheduleEntity sch = csrEntity.getLinkedSchedule();
		if (sch != null) {
			// TODO add schedule child schedule
		}		
	}

}
