/*******************************************************************************
ac * Copyright (c) 2013-2016 LAAS-CNRS (www.laas.fr)
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
package org.eclipse.om2m.commons.constants;

import java.math.BigInteger;

import org.eclipse.om2m.commons.entities.AccessControlRuleEntity;

/**
 * Constants for access control operations (CRUDDNA)
 * Tools to retrieve operations and Access control rule
 *
 */
public class AccessControl {
	/** Originator authorizing any originator to use the privileges */
	public static final String ORIGINATOR_ALL = "*";
	
	/** Private constructor */
	private AccessControl(){}
	
	/** Big integer constant for CREATE operation */
	public static final BigInteger CREATE = BigInteger.valueOf(1);
	/** Big integer constant for RETRIEVE operation */
	public static final BigInteger RETRIEVE = BigInteger.valueOf(2);
	/** Big integer constant for UPDATE operation */
	public static final BigInteger UPDATE = BigInteger.valueOf(4);
	/** Big integer constant for DELETE operation */
	public static final BigInteger DELETE = BigInteger.valueOf(8);
	/** Big integer constant for NOTIFY operation */
	public static final BigInteger NOTIFY = BigInteger.valueOf(16);
	/** Big integer constant for DISCOVERY operation */
	public static final BigInteger DISCOVERY = BigInteger.valueOf(32);
	/** Big integer constant for ALL operations */
	public static final BigInteger ALL = BigInteger.valueOf(63) ;
	
	/**
	 * Retrieve the operation(s) (big integer) from access control rule
	 * @param ruleEntity
	 * @return operation(s)
	 */
	public static BigInteger getOperationFromACR(AccessControlRuleEntity ruleEntity){
		int result = 0 ; 
		if (ruleEntity.isCreate()){
			result += CREATE.intValue();
		}
		if (ruleEntity.isRetrieve()){
			result += RETRIEVE.intValue();
		}
		if (ruleEntity.isUpdate()){
			result += UPDATE.intValue();
		}
		if (ruleEntity.isDelete()){
			result += DELETE.intValue();
		}
		if (ruleEntity.isDiscovery()){
			result += DISCOVERY.intValue();
		}
		if (ruleEntity.isNotify()){
			result += NOTIFY.intValue();
		}
		return BigInteger.valueOf(result);
	}
	
	/**
	 * Generate an Access Control Rule entity from operations big integer
	 * @param operations
	 * @return access control rule
	 */
	public static AccessControlRuleEntity getARCFromOperation(BigInteger operations){
		AccessControlRuleEntity ruleEntity = new AccessControlRuleEntity();
		int flags = operations.intValue();
		if ((flags & CREATE.intValue()) == CREATE.intValue()){
			ruleEntity.setCreate(true);
		}
		if ((flags & RETRIEVE.intValue()) == RETRIEVE.intValue()){
			ruleEntity.setRetrieve(true);
		}
		if ((flags & UPDATE.intValue()) == UPDATE.intValue()){
			ruleEntity.setUpdate(true);
		}
		if ((flags & DELETE.intValue()) == DELETE.intValue()){
			ruleEntity.setDelete(true);
		}
		if ((flags & DISCOVERY.intValue()) == DISCOVERY.intValue()){
			ruleEntity.setDiscovery(true);
		}
		if ((flags & NOTIFY.intValue()) == NOTIFY.intValue()){
			ruleEntity.setNotify(true);
		}
		return ruleEntity;
	}
}
