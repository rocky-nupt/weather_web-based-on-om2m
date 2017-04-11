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
package org.eclipse.om2m.ipe.sample.controller;

import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.core.service.CseService;
import org.eclipse.om2m.ipe.sample.RequestSender;
import org.eclipse.om2m.ipe.sample.constants.SampleConstants;
import org.eclipse.om2m.ipe.sample.model.SampleModel;
import org.eclipse.om2m.ipe.sample.util.ObixUtil;

public class SampleController {
	
	public static CseService CSE;
	protected static String AE_ID;
	
	public static void setLampState(String lampId, boolean value){
		// Set the value in the "real world" model
		SampleModel.setLampState(lampId, value);
		// Send the information to the CSE
		String targetID = SampleConstants.CSE_PREFIX + "/" + lampId + "/" + SampleConstants.DATA;
		ContentInstance cin = new ContentInstance();
		cin.setContent(ObixUtil.getStateRep(lampId, value));
		cin.setContentInfo(MimeMediaType.OBIX + ":" + MimeMediaType.ENCOD_PLAIN);
		RequestSender.createContentInstance(targetID, null, cin);
	}
	
	public static String getFormatedLampState(String lampId){
		return ObixUtil.getStateRep(lampId, getLampState(lampId));
	}
	
	public static boolean getLampState(String lampId){
		return SampleModel.getLampValue(lampId);
	}
	
	public static void toggleLamp(String lampId){
		boolean newState = !SampleModel.getLampValue(lampId);
		setLampState(lampId, newState);
	}
	
	public static void setAllOn(){
		setLampState(SampleConstants.LAMP_0, true);
		setLampState(SampleConstants.LAMP_1, true);
	}
	
	public static void setAllOff(){
		setLampState(SampleConstants.LAMP_0, false);
		setLampState(SampleConstants.LAMP_1, false);
	}
	
	public static void toogleAll(){
		boolean newState = !(SampleModel.getLampValue(SampleConstants.LAMP_0) 
				&& SampleModel.getLampValue(SampleConstants.LAMP_1));
		setLampState(SampleConstants.LAMP_0, newState);
		setLampState(SampleConstants.LAMP_1, newState);
	}

	public static void setCse(CseService cse){
		CSE = cse;
	}
	
}
