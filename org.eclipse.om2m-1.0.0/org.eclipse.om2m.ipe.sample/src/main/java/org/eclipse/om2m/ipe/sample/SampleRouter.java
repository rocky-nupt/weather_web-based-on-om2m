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
package org.eclipse.om2m.ipe.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.interworking.service.InterworkingService;
import org.eclipse.om2m.ipe.sample.constants.Operations;
import org.eclipse.om2m.ipe.sample.constants.SampleConstants;
import org.eclipse.om2m.ipe.sample.controller.SampleController;

public class SampleRouter implements InterworkingService{

	private static Log LOGGER = LogFactory.getLog(SampleRouter.class);

	@Override
	public ResponsePrimitive doExecute(RequestPrimitive request) {
		ResponsePrimitive response = new ResponsePrimitive(request);
		if(request.getQueryStrings().containsKey("op")){
			String operation = request.getQueryStrings().get("op").get(0);
			Operations op = Operations.getOperationFromString(operation);
			String lampid= null;
			if(request.getQueryStrings().containsKey("lampid")){
				lampid = request.getQueryStrings().get("lampid").get(0);
			}
			LOGGER.info("Received request in Sample IPE: op=" + operation + " ; lampid=" + lampid);
			switch(op){
			case SET_ON:
				SampleController.setLampState(lampid, true);
				response.setResponseStatusCode(ResponseStatusCode.OK);
				break;
			case SET_OFF:
				SampleController.setLampState(lampid, false);
				response.setResponseStatusCode(ResponseStatusCode.OK);
				break;
			case TOGGLE:
				SampleController.toggleLamp(lampid);
				response.setResponseStatusCode(ResponseStatusCode.OK);
				break;
			case ALL_ON:
				SampleController.setAllOn();
				response.setResponseStatusCode(ResponseStatusCode.OK);
				break;
			case ALL_OFF:
				SampleController.setAllOff();
				response.setResponseStatusCode(ResponseStatusCode.OK);
				break;
			case ALL_TOGGLE:
				SampleController.toogleAll();
            	response.setResponseStatusCode(ResponseStatusCode.OK);
            	break;
			case GET_STATE:
				// Shall not get there...
				throw new BadRequestException();
			case GET_STATE_DIRECT:
				String content = SampleController.getFormatedLampState(lampid);
				response.setContent(content);
				request.setReturnContentType(MimeMediaType.OBIX);
				response.setResponseStatusCode(ResponseStatusCode.OK);
				break;
			default:
				throw new BadRequestException();
			}
		}
		if(response.getResponseStatusCode() == null){
			response.setResponseStatusCode(ResponseStatusCode.BAD_REQUEST);
		}
		return response;
	}

	@Override
	public String getAPOCPath() {
		return SampleConstants.POA;
	}
	
}
