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

package org.eclipse.om2m.core.router;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;

/**
 * Patterns used as regular expressions and conditions
 *
 */
public class Patterns {
	
	/** All short name for filtering */
	private static final String ALL_SHORT_NAMES = ShortName.ACP+"|"+ShortName.AE+"|"+ShortName.CNT+
			"|"+ShortName.CIN + "|" + ShortName.REMOTE_CSE + "|" + ShortName.LATEST + "|" + ShortName.OLDEST +
			"|" + ShortName.GROUP + "|" + ShortName.FANOUTPOINT + "|" + ShortName.SUB + "|" + ShortName.PCH + 
			"|" + ShortName.POLLING_CHANNEL_URI + "|" + ShortName.REQ + "|" + ShortName.NODE + "|" + ShortName.ANI + "|" + ShortName.ANDI;
	
	private static final String NON_HIERARCHICAL_ID = "(" + Constants.PREFIX_SEPERATOR +"(\\b\\w+\\b)?)" ;
	
	public static final Pattern UNAUTHORIZED_NAMES = Pattern.compile(ShortName.ACP + NON_HIERARCHICAL_ID + "?|" + 
					ShortName.AE + NON_HIERARCHICAL_ID + "?|" + ShortName.CNT + NON_HIERARCHICAL_ID + "?|" +
					ShortName.CIN + NON_HIERARCHICAL_ID + "?|" + ShortName.REMOTE_CSE + NON_HIERARCHICAL_ID + "?|" +
					ShortName.LATEST + NON_HIERARCHICAL_ID + "?|" + ShortName.OLDEST + NON_HIERARCHICAL_ID + "?|" +
					ShortName.GROUP + NON_HIERARCHICAL_ID + "?|" + ShortName.FANOUTPOINT + NON_HIERARCHICAL_ID + "?|" +
					ShortName.SUB + NON_HIERARCHICAL_ID + "?|" + ShortName.PCH + "?|" + ShortName.POLLING_CHANNEL_URI + 
					"?|" + ShortName.REQ + "?|" + ShortName.NODE + "?");
	
	/** Main id string */
	public static final String ID_STRING = "([A-Za-z0-9_\\-~]|\\.)+";
	
	/** Main id pattern */
	public static final Pattern ID_PATTERN = Pattern.compile(ID_STRING);
	
    /** CseBase resource uri pattern. */
    public static final Pattern CSE_BASE_PATTERN= Pattern.compile("/" + Constants.CSE_ID);
    
    /** AccessControlPolicy uri pattern MAY BE NOT COMPLETE */
    public static final Pattern ACP_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + ShortName.ACP + Constants.PREFIX_SEPERATOR + ID_STRING );
    
    public static final Pattern AE_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + "(C|S)" + ID_STRING);
    
    public static final Pattern CONTAINER_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + ShortName.CNT + Constants.PREFIX_SEPERATOR + ID_STRING);

    public static final Pattern CONTENTINSTANCE_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + ShortName.CIN + Constants.PREFIX_SEPERATOR + ID_STRING);
    
    public static final Pattern REMOTE_CSE_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + ShortName.REMOTE_CSE + Constants.PREFIX_SEPERATOR + ID_STRING);
    
    public static final Pattern GROUP_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + ShortName.GROUP + Constants.PREFIX_SEPERATOR + ID_STRING);
    
    public static final Pattern SUBSCRIPTION_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + ShortName.SUB + Constants.PREFIX_SEPERATOR + ID_STRING);
    
    public static final Pattern POLLING_CHANNEL_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + ShortName.PCH + Constants.PREFIX_SEPERATOR + ID_STRING);
    
    public static final Pattern POLLING_CHANNEL_URI_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + ShortName.POLLING_CHANNEL_URI + Constants.PREFIX_SEPERATOR + ID_STRING);
    
    public static final Pattern REQUEST_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + ShortName.REQ + Constants.PREFIX_SEPERATOR + ID_STRING);
    
    public static final Pattern NON_RETARGETING_PATTERN = Pattern.compile("/" + Constants.CSE_ID + "(/("+ID_STRING+")?)*"); 
    
    public static final String FANOUT_POINT_MATCH = "/" + ShortName.FANOUTPOINT ;
    
    public static final Pattern NODE_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + ShortName.NODE + Constants.PREFIX_SEPERATOR + ID_STRING);

    public static final Pattern AREA_NW_INFO_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + ShortName.ANI + Constants.PREFIX_SEPERATOR + ID_STRING);

	public static final Pattern AREA_NWK_DEVICE_INFO_PATTERN = Pattern.compile(CSE_BASE_PATTERN + "/" + ShortName.ANDI + Constants.PREFIX_SEPERATOR + ID_STRING);

    /** Non-hierarchical URI pattern */
    public static final Pattern NON_HIERARCHICAL_PATTERN = Pattern.compile(
    		"(" + CSE_BASE_PATTERN + "/(" + ALL_SHORT_NAMES + ")" + Constants.PREFIX_SEPERATOR + ID_STRING + ")|(" + CSE_BASE_PATTERN+ ")|" +
    		AE_PATTERN.pattern()); 
    
    /** Hierarchical URI Pattern */
    public static final Pattern HIERARCHICAL_PATTERN = Pattern.compile(
    		CSE_BASE_PATTERN + "(/" + Constants.CSE_NAME + "(/"+ ID_PATTERN +")*)?"
    		);
    
	/**
	 * match uri with a pattern.
	 * @param pattern - pattern
	 * @param uri - resource uri
	 * @return true if matched, otherwise false.
	 */
	public static boolean match(Pattern pattern, String uri) {
	    // Match uri with pattern
	    Matcher m = pattern.matcher(uri);
	    if (!m.matches()){
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Retrieve the corresponding DAO from the URI
	 * @param uri uri of the targetted resource
	 * @param db database service
	 * @return DAO corresponding to the resource, null if not found
	 */
	public static DAO<?> getDAO(String uri, DBService db){
		if (match(CSE_BASE_PATTERN, uri)){
			return db.getDAOFactory().getCSEBaseDAO();
		}
		if (match(ACP_PATTERN, uri)){
			return db.getDAOFactory().getAccessControlPolicyDAO();
		}
		if (match(AE_PATTERN,uri)){
			return db.getDAOFactory().getAeDAO();
		}
		if(match(CONTAINER_PATTERN, uri)){
			return db.getDAOFactory().getContainerDAO();
		}
		if(match(CONTENTINSTANCE_PATTERN, uri)) {
			return db.getDAOFactory().getContentInstanceDAO();
		}
		if(match(REMOTE_CSE_PATTERN, uri)) {
			return db.getDAOFactory().getRemoteCSEDAO();
		}
		if(match(GROUP_PATTERN, uri)){
			return db.getDAOFactory().getGroupDAO();
		}
		if(match(SUBSCRIPTION_PATTERN, uri)){
			return db.getDAOFactory().getSubsciptionDAO();
		}
		if(match(POLLING_CHANNEL_PATTERN, uri)){
			return db.getDAOFactory().getPollingChannelDAO();
		}
		if(match(REQUEST_PATTERN, uri)){
			return db.getDAOFactory().getRequestEntityDAO();
		}
		if (match(NODE_PATTERN, uri)) {
			return db.getDAOFactory().getNodeEntityDAO();
		}
		return null;
	}
	
	/**
	 * Method used to check the validity of the resource name provided
	 * @param resourceName
	 * @return
	 */
	public static boolean checkResourceName(String resourceName){
		return match(ID_PATTERN, resourceName);
	}
}
