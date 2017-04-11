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
package org.eclipse.om2m.commons.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.om2m.commons.constants.AccessControl;
import org.eclipse.om2m.commons.entities.AccessControlContextEntity;
import org.eclipse.om2m.commons.entities.AccessControlOriginatorEntity;
import org.eclipse.om2m.commons.entities.AccessControlRuleEntity;
import org.eclipse.om2m.commons.resource.AccessControlRule;
import org.eclipse.om2m.commons.resource.AccessControlRule.AccessControlContexts;
import org.eclipse.om2m.commons.resource.AccessControlRule.AccessControlContexts.AccessControlIpAddresses;
import org.eclipse.om2m.commons.resource.SetOfAcrs;

/**
 * Utils for ACP
 *
 */
public class AcpUtils {
	
	private AcpUtils(){}

	/**
	 * Use to perform the mapping between a list of access control rule & set of
	 * acrs in the oneM2M norm.
	 * 
	 * @param ruleEntities
	 * @return the set of acrs
	 */
	public static SetOfAcrs getSetOfArcsFromACRE(
			List<AccessControlRuleEntity> ruleEntities) {
		// Initializing result
		SetOfAcrs result = new SetOfAcrs();
		// For each rule entity
		for (AccessControlRuleEntity ruleEntity : ruleEntities) {
			AccessControlRule rule = new AccessControlRule();
			rule.setAccessControlOperations(AccessControl
					.getOperationFromACR(ruleEntity));
			// For each originator in ACRE
			for (AccessControlOriginatorEntity originatorEntity : ruleEntity
					.getAccessControlOriginators()) {
				rule.getAccessControlOriginators().add(
						originatorEntity.getOriginatorID());
			}
			// For each context in ACRE
			for (AccessControlContextEntity contextEntity : ruleEntity
					.getAccessControlContexts()) {
				AccessControlContexts context = new AccessControlContexts();
				context.getAccessControlWindow().addAll(
						contextEntity.getAccessControlWindow());
				AccessControlIpAddresses addresses = new AccessControlIpAddresses();
				addresses.getIpv4Addresses().addAll(
						contextEntity.getIpv4Addresses());
				addresses.getIpv6Addresses().addAll(
						contextEntity.getIpv6Addresses());
				context.setAccessControlIpAddresses(addresses);
				context.setAccessControlLocationRegion(contextEntity
						.getAccessControlLocationRegion());
				rule.getAccessControlContexts().add(context);
			}
			result.getAccessControlRule().add(rule);
		}
		return result;
	}

	/**
	 * Use to perform the mapping between a SetOfAcrs (list of AccessControlRule)
	 * and a list of ACREntity for the database management.
	 * @param setOfAcrs to map
	 * @return the list of AccessControlRuleEntity
	 */
	public static List<AccessControlRuleEntity> getACREntityFromSetOfArcs(SetOfAcrs setOfAcrs){
		// Initializing result
		List<AccessControlRuleEntity> result = new ArrayList<>();
		// For each ACR in SetOfAcrs
		for (AccessControlRule rule : setOfAcrs.getAccessControlRule()){
			AccessControlRuleEntity ruleEntity = AccessControl.
					getARCFromOperation(rule.getAccessControlOperations());
			// For each Constexts in ACR
			for (AccessControlContexts context : rule.getAccessControlContexts()){
				AccessControlContextEntity contextEntity = new AccessControlContextEntity();
				contextEntity.getIpv4Addresses().addAll(context.getAccessControlIpAddresses().getIpv4Addresses());
				contextEntity.getIpv6Addresses().addAll(context.getAccessControlIpAddresses().getIpv6Addresses());
				contextEntity.setAccessControlLocationRegion(context.getAccessControlLocationRegion());
				contextEntity.getAccessControlWindow().addAll(context.getAccessControlWindow());
				ruleEntity.getAccessControlContexts().add(contextEntity);
			}

			// For each originator in ACR
			for (String originator : rule.getAccessControlOriginators()){
				AccessControlOriginatorEntity originatorEntity = new AccessControlOriginatorEntity(originator);
				ruleEntity.getAccessControlOriginators().add(originatorEntity);
			}

			result.add(ruleEntity);
		}

		return result;
	}
	
}
