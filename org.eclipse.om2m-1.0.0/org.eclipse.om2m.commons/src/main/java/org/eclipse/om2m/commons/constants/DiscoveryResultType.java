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
 * Constants for discovery resul type
 *
 */
public class DiscoveryResultType {
	/** Big integer constant for Hierarchical discovery result type */
	public static final BigInteger HIERARCHICAL = BigInteger.valueOf(1);
	/** Big integer constant for Non Hierarchical discovery result type */
	public static final BigInteger NON_HIERARCHICAL = BigInteger.valueOf(2);
	/** Big integer constant for CSEID and resource IDs discovery result type */
	public static final BigInteger CSEID_AND_RESOURCEID = BigInteger.valueOf(3);

}
