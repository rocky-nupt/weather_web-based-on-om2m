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
 * Big integer constants for operations
 *
 */
public class Operation {
	
	/** Private constructor */
	private Operation(){}
	
	/** Big integer constant for CREATE operation */
	public static final BigInteger CREATE = BigInteger.valueOf(1);
	/** Big integer constant for RETRIEVE operation */
	public static final BigInteger RETRIEVE = BigInteger.valueOf(2);
	/** Big integer constant for UPDATE operation */
	public static final BigInteger UPDATE = BigInteger.valueOf(3);
	/** Big integer constant for DELETE operation */
	public static final BigInteger DELETE = BigInteger.valueOf(4);
	/** Big integer constant for NOTIFY operation */
	public static final BigInteger NOTIFY = BigInteger.valueOf(5);
	// Not in the norm, used for our convenience
	/** Big integer constant for DISCOVERY operation (non oneM2M) */
	public static final BigInteger DISCOVERY = BigInteger.valueOf(6);
	
}
