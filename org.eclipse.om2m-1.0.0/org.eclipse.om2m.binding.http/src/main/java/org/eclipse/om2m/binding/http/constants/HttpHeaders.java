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
package org.eclipse.om2m.binding.http.constants;

public class HttpHeaders {

	/** Private constructor */
	private HttpHeaders(){
	}
	
	public static final String REQUEST_IDENTIFIER = "X-M2M-RI";
	public static final String ACCEPT = "Accept";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_LOCATION = "Content-Location";
	public static final String ETAG = "Etag";
	public static final String ORIGINATOR = "X-M2M-Origin";
	public static final String NAME = "X-M2M-NM";
	public static final String GROUP_REQUEST_IDENTIFIER = "X-M2M-GID";
	public static final String RESPONSE_TYPE = "X-M2M-RTU";
	public static final String HOST = "Host";
	public static final String ORIGINATING_TIMESTAMP = "X-M2M-OT";
	public static final String RESULT_EXPIRATION_TIMESTAMP = "X-M2M-RST";
	public static final String REQUEST_EXPIRATION_TIMESTAMP = "X-M2M-RET";
	public static final String OPERATION_EXECUTION_TIME = "X-M2M-OET";
	public static final String EVENT_CATEGORY = "X-M2M-EC";
	public static final String RESPONSE_STATUS_CODE = "X-M2M-RSC";
	
}
