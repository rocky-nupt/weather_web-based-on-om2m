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

public class CoapParameters {

	/** Private constructor */
	private CoapParameters() {
	}

	public static final String RESPONSE_TYPE = "rt";
	public static final String RESULT_PERSISTENCE = "rp";
	public static final String RESULT_CONTENT = "rcn";
	public static final String DELIVERY_AGGREGATION = "da";
	public static final String CREATED_BEFORE = "crb";
	public static final String CREATED_AFTER = "cra";
	public static final String MODIFIED_SINCE = "ms";
	public static final String UNMODIFIED_SINCE = "us";
	public static final String STATE_TAG_SMALLER = "sts";
	public static final String STATE_TAG_BIGGER = "stb";
	public static final String EXPIRE_BEFORE = "exb";
	public static final String EXPIRE_AFTER = "exa";
	public static final String LABELS = "lbl";
	public static final String RESOURCE_TYPE = "rty";
	public static final String SIZE_ABOVE = "sza";
	public static final String SIZE_BELOW = "szb";
	public static final String CONTENT_TYPE = "cty";
	public static final String LIMIT = "lim";
	public static final String ATTRIBUTE = "atr";
	public static final String FILTER_USAGE = "fu";
	public static final String DISCOVERY_RESULT_TYPE = "drt";
}
