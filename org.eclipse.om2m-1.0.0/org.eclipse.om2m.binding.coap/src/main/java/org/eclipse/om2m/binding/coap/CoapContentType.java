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
package org.eclipse.om2m.binding.coap;

public class CoapContentType {
	
	/**
	 * Prevent this class from being created
	 */
	private CoapContentType(){}

	// Standards content formats
	public static final int PLAIN_TEXT = 0;
	public static final int APP_XML = 	41;
	public static final int APP_JSON = 	50;
	
	// oneM2M specific content formats
	public static final int RES_XML = 	10000;
	public static final int RES_JSON = 	10001;
	public static final int NTFY_XML = 	10002;
	public static final int NTFY_JSON = 10003;
	public static final int ATTRS_XML = 10004;
	public static final int ATTRS_JSON=	10005;
	public static final int PREQ_XML =	10006;
	public static final int PREQ_JSON =	10007;
	public static final int PRSQ_XML =	10008;
	public static final int PRSQ_JSON =	10009;
	
}
