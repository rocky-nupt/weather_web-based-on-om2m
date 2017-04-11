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
package org.eclipse.om2m.core.comm;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.binding.service.RestClientService;
import org.eclipse.om2m.commons.exceptions.InternalServerErrorException;
import org.eclipse.om2m.commons.exceptions.NotImplementedException;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;


/**
 *
 * A generic client that acts as a proxy to forward requests to specific rest clients based on their
 * communication protocol such as HTTP, COAP, etc.
 *
 */
public class RestClient{
	/** Logger  */
	private static Log LOGGER = LogFactory.getLog(RestClient.class);
	/** Contains all discovered specific rest clients that will considered for sending requests */
	public static Map<String,RestClientService> restClients = new HashMap<String,RestClientService>();

	/**
	 * Selects a specific client (HTTP by default) id available and uses it to send the request.
	 * @param request - The generic request to handle
	 * @return The generic returned response
	 */
	public static ResponsePrimitive sendRequest(RequestPrimitive request){
		LOGGER.info("the requestIndication RC: "+request);
		ResponsePrimitive response= new ResponsePrimitive();
		
		// Find the appropriate client from the map and send the request
		// Display to check the discovered protocols
		String protocol = request.getTo().split("://")[0];

		if(restClients.containsKey(protocol)){
			try{
				response = restClients.get(protocol).sendRequest(request);
				if(response.getResponseStatusCode()==null){
					throw new Exception();
				}
			}catch(Exception e){
				LOGGER.error("RestClient error",e);
				throw new InternalServerErrorException("RestClient Error", e);
			}
		}else{
			throw new NotImplementedException("No RestClient service found for protocol: " + protocol);
		}

		LOGGER.info(response);
		return response;
	}

	/**
	 * Gets RestClients
	 * @return restClients
	 */
	public static Map<String, RestClientService> getRestClients() {
		return restClients;
	}

	/**
	 * Sets RestClient
	 * @param sclClients
	 */
	public static void setRestClients(Map<String, RestClientService> sclClients) {
		RestClient.restClients = sclClients;
	}
}
