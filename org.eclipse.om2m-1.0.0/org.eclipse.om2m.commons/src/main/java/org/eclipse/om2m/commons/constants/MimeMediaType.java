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

/**
 * Constants for mime media types
 *
 */
public class MimeMediaType {
	
	/** Private constructor */
	private MimeMediaType(){
	}
	
	/** Constant for mime media type oneM2M prefix */
	private static final String PREFIX = "application/vnd.onem2m-";
	/** Constant for mime media type xml */
	public static final String XML = "application/xml";
	/** Constant for mime media type JSON */
	public static final String JSON = "application/json";
	/** Constant for OBJECT */
	public static final String OBJ = "application/obj";
	/** Constant for oBIX */
	public static final String OBIX = "application/obix";
	
	/** Constant for mime media type oneM2M XML resource */
	public static final String XML_RESOURCE = PREFIX + "res+xml";
	/** Constant for mime media type oneM2M XML_NOTIFICATION */
	public static final String XML_NOTIFICATION = PREFIX + "ntfy+xml";
	/** Constant for mime media type oneM2M XML_NOTIFICATION */
	public static final String XML_ATTRIBUTES = PREFIX + "attrs+xml";
	/** Constant for mime media type oneM2M XML_NOTIFICATION */
	public static final String XML_REQUEST_PRIMITIVE = PREFIX + "preq+xml";
	/** Constant for mime media type oneM2M XML_NOTIFICATION */
	public static final String XML_RESPONSE_PRIMITIVE = PREFIX + "prsp+xml";
	
	/** Constant for mime media type oneM2M XML_NOTIFICATION */
	public static final String JSON_RESOURCE = PREFIX + "res+json";
	/** Constant for mime media type oneM2M XML_NOTIFICATION */
	public static final String JSON_NOTIFICATION = PREFIX + "ntfy+json";
	/** Constant for mime media type oneM2M XML_NOTIFICATION */
	public static final String JSON_ATTRIBUTES = PREFIX + "attrs+json";
	/** Constant for mime media type oneM2M XML_NOTIFICATION */
	public static final String JSON_REQUEST_PRIMITIVE = PREFIX + "preq+json";
	/** Constant for mime media type oneM2M XML_NOTIFICATION */
	public static final String JSON_RESPONSE_PRIMITIVE = PREFIX + "prsp+json";
	
	/** Constant for content info separator */
	public static final String CONTENT_INFO_SEPARATOR = ":";
	/** Constant for text plain */
	public static final String TEXT_PLAIN = "text/plain";
	/** Integer constant for encod plain */
	public static final Integer ENCOD_PLAIN = 0;
	/** Integer constant for encod base 64 */
	public static final Integer ENCOD_BASE64 = 1;
	/** Integer constant for encod base 64 binary */
	public static final Integer ENCOD_BASE64_BINARY = 2;
}
