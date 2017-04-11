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

public class CoapOptions {

	/** Private constructor */
	private CoapOptions(){
	}
	
	// Standard CoAP Options
	public static final int LOCATION = 8;
	public static final int CONTENT_FORMAT = 12;
	public static final int ACCEPT = 17;
	
	// oneM2M Specific Options
	public static final int ONEM2M_FR = 256;
	public static final int ONEM2M_RQI = 257;
	public static final int ONEM2M_OT = 259;
	public static final int ONEM2M_RQET = 260;
	public static final int ONEM2M_RSET = 261;
	public static final int ONEM2M_OET = 262;
	public static final int ONEM2M_RTURI = 263;
	public static final int ONEM2M_EC = 264;
	public static final int ONEM2M_RSC = 265;
	public static final int ONEM2M_GID = 266;
	public static final int ONEM2M_TY = 267;
	
}
