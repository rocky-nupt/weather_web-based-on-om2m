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
package org.eclipse.om2m.core.interworking;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.om2m.interworking.service.InterworkingService;

/**
 * Contains the list of IPE registered to the core.
 * 
 */
public class IpeSelector {
	
	/** Map of Data Mapper Services */
	private static Map<String, InterworkingService> interworkingList = new HashMap<String, InterworkingService>();

	/**
	 * Return the interworking list of IPE
	 * @return list of IPE
	 */
	public static Map<String, InterworkingService> getInterworkingList(){
		return interworkingList;
	}
	
}
