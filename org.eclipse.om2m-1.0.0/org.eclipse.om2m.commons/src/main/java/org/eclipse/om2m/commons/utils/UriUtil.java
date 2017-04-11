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
package org.eclipse.om2m.commons.utils;

import org.eclipse.om2m.commons.constants.Constants;

/**
 * Uri util methods to check uri relativity. 
 *
 */
public class UriUtil {

	private UriUtil(){}

	/**
	 * Check if the uri is absolute
	 * @param uri
	 * @return
	 */
	public static boolean isAbsoluteUri(String uri){
		return uri.startsWith("//") 
				|| uri.startsWith(Constants.ABSOLUTE_URI_SEPARATOR);
	}

	/**
	 * Check if the uri is sp relative
	 * @param uri
	 * @return
	 */
	public static boolean isSpRelativeUri(String uri){
		return (uri.startsWith("/") && !uri.startsWith("//")) 
				|| uri.startsWith(Constants.SP_RELATIVE_URI_SEPARATOR);
	}

	/**
	 * Check if the uri is cse relative
	 * @param uri
	 * @return
	 */
	public static boolean isCseRelativeUri(String uri){
		return (!uri.startsWith("/") 
				&& !uri.startsWith("//") 
				&& !uri.startsWith(Constants.SP_RELATIVE_URI_SEPARATOR)
				&& !uri.startsWith(Constants.ABSOLUTE_URI_SEPARATOR));
	}

	/**
	 * Check if the uri is in the current domain
	 * @param uri
	 * @return
	 */
	public static boolean isCurrentSpDomain(String uri){
		String aux = toAbsoluteUri(uri);
		return aux.startsWith("//" + Constants.M2M_SP_ID) 
				|| aux.startsWith(Constants.ABSOLUTE_URI_SEPARATOR + Constants.M2M_SP_ID);
	}

	/**
	 * Check if the uri is the current cse
	 * @param uri
	 * @return current cse or not
	 */
	public static boolean isCurrentCse(String uri){
		String aux = toSpRelativeUri(uri);
		return aux.startsWith(Constants.SP_RELATIVE_URI_SEPARATOR + "/" + Constants.CSE_ID) 
				|| aux.startsWith("/" + Constants.CSE_ID);
	}
	
	/**
	 * Only application for current Absolute
	 * @param uri 
	 * @return absolute uri
	 */
	public static String toAbsoluteUri(String uri){
		if(isAbsoluteUri(uri)){
			return uri;
		} else if(isSpRelativeUri(uri)){
			String cleanUri = uri.startsWith(Constants.SP_RELATIVE_URI_SEPARATOR) ? uri.substring(1) : uri ; 
			return "//" + Constants.M2M_SP_ID + cleanUri;
		} else {
			return toAbsoluteUri(toSpRelativeUri(uri));
		}
	}

	/**
	 * Only applicable for current SP Domain
	 * @param uri
	 * @return sp relative uri
	 */
	public static String toSpRelativeUri(String uri){
		if(isSpRelativeUri(uri)){
			return uri;
		} else if(isAbsoluteUri(uri)){
			String aux = uri.startsWith(Constants.PREFIX_SEPERATOR) ? uri.substring(1) : uri.substring(2);
			return aux.split(Constants.M2M_SP_ID)[1];
		} else {
			return "/" + Constants.CSE_ID + "/" + uri;
		}
	}

	/**
	 * Only application for current Cse
	 * @param uri 
	 * @return cse relative uri
	 */
	public static String toCseRelativeUri(String uri){
		if(isCseRelativeUri(uri)){
			return uri;
		} else if(isSpRelativeUri(uri)) {
			String aux = uri.startsWith(Constants.SP_RELATIVE_URI_SEPARATOR) ? uri.substring(1) : uri;
			if(aux.equals("/" + Constants.CSE_ID)){
				return aux;
			}
			return aux.split("/" + Constants.CSE_ID + "/")[1];
		} else {
			return toCseRelativeUri(toSpRelativeUri(uri));
		}
	}
}
