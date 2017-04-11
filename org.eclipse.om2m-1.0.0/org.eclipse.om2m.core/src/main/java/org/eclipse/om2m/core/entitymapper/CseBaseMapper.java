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
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.LabelEntity;
import org.eclipse.om2m.commons.entities.NodeEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.RequestEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.resource.AE;
import org.eclipse.om2m.commons.resource.AccessControlPolicy;
import org.eclipse.om2m.commons.resource.CSEBase;
import org.eclipse.om2m.commons.resource.ChildResourceRef;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.RemoteCSE;
import org.eclipse.om2m.commons.resource.Request;
import org.eclipse.om2m.commons.resource.Subscription;

public class CseBaseMapper extends EntityMapper<CSEBaseEntity, CSEBase> {

	@Override
	protected CSEBase createResource() {
		return new CSEBase();
	}

	@Override
	protected void mapAttributes(CSEBaseEntity cseBaseEntity, CSEBase cseBaseResource) {
		cseBaseResource.setNodeLink(cseBaseEntity.getNodeLink());
		cseBaseResource.setCSEID(cseBaseEntity.getCseid());
		cseBaseResource.setCseType(cseBaseEntity.getCseType());
		
		// setting supported resources
		for (BigInteger ty : cseBaseEntity.getSupportedResourceType()){
			cseBaseResource.getSupportedResourceType().add(ty);
		}
		
		// setting access control policy ids
		for (AccessControlPolicyEntity acp : cseBaseEntity.getAccessControlPolicies()){
			cseBaseResource.getAccessControlPolicyIDs().add(acp.getResourceID());
		}		
		if (!cseBaseEntity.getLabelsEntities().isEmpty()) {
			for (LabelEntity l : cseBaseEntity.getLabelsEntities()) {
				cseBaseResource.getLabels().add(l.getLabel());
			}
		}
		if (!cseBaseEntity.getPointOfAccess().isEmpty()) {
			cseBaseResource.getPointOfAccess().addAll(cseBaseEntity.getPointOfAccess());
		}
	}

	@Override
	protected void mapChildResourceRef(CSEBaseEntity cseBaseEntity, CSEBase cseBaseResource) {
		// setting child resources refs
		// setting acps refs
		for (AccessControlPolicyEntity acp : cseBaseEntity.getChildAccessControlPolicies()){
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(acp.getName());
			child.setType(ResourceType.ACCESS_CONTROL_POLICY);
			child.setValue(acp.getResourceID());
			cseBaseResource.getChildResource().add(child);
		}
		// adding aes refs
		for (AeEntity ae : cseBaseEntity.getAes()){
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(ae.getName());
			child.setType(ResourceType.AE);
			child.setValue(ae.getResourceID());
			cseBaseResource.getChildResource().add(child);
		}
		// adding container refs
		for (ContainerEntity cnt : cseBaseEntity.getChildContainers()) {
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(cnt.getName());
			child.setType(ResourceType.CONTAINER);
			child.setValue(cnt.getResourceID());
			cseBaseResource.getChildResource().add(child);
		}
		// adding remoteCSE refs
		for (RemoteCSEEntity csr : cseBaseEntity.getRemoteCses()) {
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(csr.getName());
			child.setType(ResourceType.REMOTE_CSE);
			child.setValue(csr.getResourceID());
			cseBaseResource.getChildResource().add(child);
		}
		// adding group refs
		for (GroupEntity group : cseBaseEntity.getGroups()){
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(group.getName());
			child.setType(ResourceType.GROUP);
			child.setValue(group.getResourceID());
			cseBaseResource.getChildResource().add(child);
		}
		// adding subscription refs
		for (SubscriptionEntity sub : cseBaseEntity.getSubscriptions()){
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(sub.getName());
			child.setType(ResourceType.SUBSCRIPTION);
			child.setValue(sub.getResourceID());
			cseBaseResource.getChildResource().add(child);
		}
		// adding request refs
		for(RequestEntity req : cseBaseEntity.getChildReq()){
			ChildResourceRef child = new ChildResourceRef();
			child.setResourceName(req.getName());
			child.setType(ResourceType.REQUEST);
			child.setValue(req.getResourceID());
			cseBaseResource.getChildResource().add(child);
		}
		// adding node refs
		for (NodeEntity nod : cseBaseEntity.getChildNodes()){
			ChildResourceRef ch = new ChildResourceRef();
			ch.setResourceName(nod.getName());
			ch.setType(ResourceType.NODE);
			ch.setValue(nod.getResourceID());
			cseBaseResource.getChildResource().add(ch);
		}
	}



	@Override
	protected void mapChildResources(CSEBaseEntity entity, CSEBase resource) {
		for (AccessControlPolicyEntity acp : entity.getChildAccessControlPolicies()){
			AccessControlPolicy acpRes = new AcpMapper().mapEntityToResource(acp, ResultContent.ATTRIBUTES);
			resource.getRemoteCSEOrNodeOrAE().add(acpRes);
		}
		// adding aes refs
		for (AeEntity ae : entity.getAes()){
			AE aeRes = new AeMapper().mapEntityToResource(ae, ResultContent.ATTRIBUTES);
			resource.getRemoteCSEOrNodeOrAE().add(aeRes);
		}
		// adding container refs
		for (ContainerEntity cnt : entity.getChildContainers()) {
			Container cntRes = new ContainerMapper().mapEntityToResource(cnt, ResultContent.ATTRIBUTES);
			resource.getRemoteCSEOrNodeOrAE().add(cntRes);
		}
		// adding remoteCSE refs
		for (RemoteCSEEntity csr : entity.getRemoteCses()) {
			RemoteCSE csrRes = new RemoteCSEMapper().mapEntityToResource(csr, ResultContent.ATTRIBUTES);
			resource.getRemoteCSEOrNodeOrAE().add(csrRes);
		}
		// adding group refs
		for (GroupEntity group : entity.getGroups()){
			Group grp = new GroupMapper().mapEntityToResource(group, ResultContent.ATTRIBUTES);
			resource.getRemoteCSEOrNodeOrAE().add(grp);
		}
		// adding subscription refs
		for (SubscriptionEntity sub : entity.getSubscriptions()){
			Subscription subRes = new SubscriptionMapper().mapEntityToResource(sub, ResultContent.ATTRIBUTES);
			resource.getRemoteCSEOrNodeOrAE().add(subRes);
		}
		// adding request refs
		for(RequestEntity req : entity.getChildReq()){
			Request reqResource = new RequestMapper().mapEntityToResource(req, ResultContent.ATTRIBUTES);
			resource.getRemoteCSEOrNodeOrAE().add(reqResource);
		}
	}

}
