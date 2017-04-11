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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Util for time handling
 *
 */
public class Util {
	
	protected static String dateFormat = System.getProperty("org.eclipse.om2m.date.format", "yyyyMMdd'T'HHmmss");
//	protected static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ";

	/** Private consctructor */
	private Util() {}

	/**
	 * Util methods for date string representation.
	 *
	 */
	public static class DateUtil {
		
		private static final Log LOGGER = LogFactory.getLog(DateUtil.class);
		
		private DateUtil() {
		}

		/**
		 * Returns the current date as a string for xml representation.
		 * @return
		 */
		public static String now() {
			return new SimpleDateFormat(dateFormat).format(new Date()).toString();
		}
		
	    /**
	     * Checks if the expirationTime is out of date or not
	     * @param expirationTime - expiration time present in the request representation
	     * @return false if the expirationTime attribute is out of date otherwise true
	     */
	    public static boolean checkExpirationTime(String expirationTime) {
	        DateFormat df=new SimpleDateFormat(dateFormat);
	        Date expDate;
	        try {
	            expDate = df.parse(expirationTime);
	            if (expDate.compareTo(new Date()) > 0) {
	                return true;
	            } else {
	                return false;
	            }
	        } catch (ParseException e) {
	            LOGGER.error("Invalid XMLGregorianCalendar Format", e);
	            return false;
	        }
	    }

	    /**
	     * Generates a new ExpirationTime by adding seconds to the current Time
	     * @param addedSeconds - seconds to add to the current time
	     * @return New expirationTime value of the resource
	     */
	    public static String getNewExpirationTime(long addedSeconds) {
	        long addedMilSeconds = addedSeconds * 1000;
	        Date newDate = new Date((new Date()).getTime() + addedMilSeconds);
	        return DateConverter.toXMLGregorianCalendar(newDate).toString();
	    }

	    /**
	     * Generates a new DelayTolerance by adding seconds to the current Time
	     * @param addedSeconds - seconds to add to the current time
	     * @return New delayTolerance of the resource
	     */
	    public static String getNewDelayTolerance(long addedSeconds) {
	        long addedMilSeconds = addedSeconds * 1000;
	        Date newDate = new Date((new Date()).getTime() + addedMilSeconds);
	        return DateConverter.toXMLGregorianCalendar(newDate).toString();
	    }
	    
	    /**
	     * Get the date in 1 year for default expiration time
	     * @return
	     */
	    public static String getDefaultExpirationTime(){
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(new Date());
	    	cal.add(Calendar.YEAR, 1);
	    	return new SimpleDateFormat(dateFormat).format(cal.getTime());
	    }
	}
	
	/**
	 * Convert an InputStream to a String
	 * @param is input stream
	 * @return the converted string
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

}
