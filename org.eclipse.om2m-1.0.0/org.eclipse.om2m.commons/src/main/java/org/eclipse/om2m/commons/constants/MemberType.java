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
package org.eclipse.om2m.commons.constants;

import java.math.BigInteger;

/**
 * Refers to all MemberType possible with their corresponding values
 *
 */
public class MemberType {
	
	/**
	 * Private constructor to prevent creation of this object
	 */
	private MemberType(){
	}

	/** Big integer constant for ACP member type */
	public static final BigInteger ACP = BigInteger.valueOf(1);
	/** Big integer constant for AE member type */
	public static final BigInteger AE = BigInteger.valueOf(2);
	/** Big integer constant for CNT member type */
	public static final BigInteger CONTAINER = BigInteger.valueOf(3);
	/** Big integer constant for CIN member type */
	public static final BigInteger CONTENT_INSTANCE = BigInteger.valueOf(4);
	/** Big integer constant for CSB member type */
	public static final BigInteger CSE_BASE = BigInteger.valueOf(5);
	/** Big integer constant for Delivery member type */
	public static final BigInteger DELIVERY = BigInteger.valueOf(6);
	/** Big integer constant for Event config member type */
	public static final BigInteger EVENT_CONFIG = BigInteger.valueOf(7);
	/** Big integer constant for exec instance member type */
	public static final BigInteger EXEC_INSTANCE = BigInteger.valueOf(8);
	/** Big integer constant for GRP member type */
	public static final BigInteger GROUP = BigInteger.valueOf(9);
	/** Big integer constant for location policy member type */
	public static final BigInteger LOCATION_POLICY = BigInteger.valueOf(10);
	/** Big integer constant for M2M service subscription member type */
	public static final BigInteger M2M_SERVICE_SUBSCRIPTION = BigInteger.valueOf(11);
	/** Big integer constant for MGMT CMD member type */
	public static final BigInteger MGMT_CMD = BigInteger.valueOf(12);
	/** Big integer constant for MGMT OBJ member type */
	public static final BigInteger MGMT_OBJ = BigInteger.valueOf(13);
	/** Big integer constant for NODE member type */
	public static final BigInteger NODE = BigInteger.valueOf(14);
	/** Big integer constant for POLLING_CHANNEL member type */
	public static final BigInteger POLLING_CHANNEL = BigInteger.valueOf(15);
	/** Big integer constant for REMOTE_CSE member type */
	public static final BigInteger REMOTE_CSE = BigInteger.valueOf(16);
	/** Big integer constant for REQUEST member type */
	public static final BigInteger REQUEST = BigInteger.valueOf(17);
	/** Big integer constant for SCHEDULE member type */
	public static final BigInteger SCHEDULE = BigInteger.valueOf(18);
	/** Big integer constant for SERVICE_SUBSCRIBED_APP_RULE member type */
	public static final BigInteger SERVICE_SUBSCRIBED_APP_RULE = BigInteger.valueOf(19);
	/** Big integer constant for SERVICE_SUBSCRIBED_NODE member type */
	public static final BigInteger SERVICE_SUBSCRIBED_NODE = BigInteger.valueOf(20);
	/** Big integer constant for ACSTATS_COLLECTP member type */
	public static final BigInteger STATS_COLLECT = BigInteger.valueOf(21);
	/** Big integer constant for STATS_CONFIG member type */
	public static final BigInteger STATS_CONFIG = BigInteger.valueOf(22);
	/** Big integer constant for SUBSCRIPTION member type */
	public static final BigInteger SUBSCRIPTION = BigInteger.valueOf(23);
	/** Big integer constant for MIXED member type */
	public static final BigInteger MIXED = BigInteger.valueOf(24);
	
	
}
