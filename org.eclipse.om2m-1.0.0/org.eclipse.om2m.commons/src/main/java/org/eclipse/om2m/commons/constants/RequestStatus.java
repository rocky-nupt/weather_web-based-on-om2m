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
 * Class representing the request status values
 *
 */
public class RequestStatus {

	/** Private status to avoir creation of this object */
	private RequestStatus(){}

	/** Represent the completed state */
	public static final BigInteger COMPLETED = BigInteger.valueOf(1);
	/** Represent the failed state */
	public static final BigInteger FAILED = BigInteger.valueOf(2);
	/** Represent the pending state */
	public static final BigInteger PENDING = BigInteger.valueOf(3);
	/** Represent the forwarded state */
	public static final BigInteger FORWARDED = BigInteger.valueOf(4);
	
}
