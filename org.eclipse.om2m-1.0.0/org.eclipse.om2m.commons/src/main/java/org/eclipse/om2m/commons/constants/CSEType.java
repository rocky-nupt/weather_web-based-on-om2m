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
 * Constants for CSE
 *
 */
public class CSEType {

	/** Private constructor */
	private CSEType(){}
	/** big integer constant for CSE type IN */
	public static final BigInteger IN_CSE = BigInteger.valueOf(1);
	/** big integer constant for CSE type MN */
	public static final BigInteger MN_CSE = BigInteger.valueOf(2);
	/** big integer constant for CSE type ASN */
	public static final BigInteger ASN_CSE = BigInteger.valueOf(3);
	/** String constant for CSE type IN */
	public static final String IN = "in-cse";
	/** String constant for CSE type MN */
	public static final String MN = "mn-cse";
	/** String constant for CSE type ASN */
	public static final String ASN = "asn-cse";

	/**
	 * Get the big integer constant from string one
	 * @param cseType
	 * @return
	 */
	public static BigInteger toBigInteger(String cseType){
		String type = cseType.toLowerCase();
		switch(type){
		case IN:
			return IN_CSE;
		case MN:
			return MN_CSE;
		case ASN:
			return ASN_CSE;
		default:
			return IN_CSE;
		}
	}
}
