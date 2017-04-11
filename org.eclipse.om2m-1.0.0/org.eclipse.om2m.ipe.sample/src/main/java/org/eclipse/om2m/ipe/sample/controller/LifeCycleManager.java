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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.resource.AE;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.ipe.sample.RequestSender;
import org.eclipse.om2m.ipe.sample.constants.SampleConstants;
import org.eclipse.om2m.ipe.sample.gui.GUI;
import org.eclipse.om2m.ipe.sample.model.Lamp;
import org.eclipse.om2m.ipe.sample.model.SampleModel;
import org.eclipse.om2m.ipe.sample.util.ObixUtil;

public class LifeCycleManager {

	private static Log LOGGER = LogFactory.getLog(LifeCycleManager.class); 

	/**
	 * Handle the start of the plugin with the resource representation and the GUI
	 */
	public static void start(){
		Map<String, Lamp> lamps = new HashMap<String, Lamp>();
		for(int i=0; i<2; i++) {
			String lampId = Lamp.TYPE+"_"+i;
			lamps.put(lampId, new Lamp(lampId, false));
		}
		SampleModel.setModel(lamps);

		// Create initial resources for the 2 lamps
		for(int i=0; i<2; i++) {
			String lampId = Lamp.TYPE+"_"+i;
			createLampResources(lampId, false, SampleConstants.POA);
		}
		createLampAll(SampleConstants.POA);			

		// Start the GUI
		if(SampleConstants.GUI){
			GUI.init();
		}

	}

	/**
	 * Stop the GUI if it is present
	 */
	public static void stop(){
		if(SampleConstants.GUI){
			GUI.stop();
		}
	}

	/**
	 * Creates all required resources.
	 * @param appId - Application ID
	 * @param initValue - initial lamp value
	 * @param poa - lamp Point of Access
	 */
	private static void createLampResources(String appId, boolean initValue, String poa) {
		// Create the Application resource
		Container container = new Container();
		container.getLabels().add("lamp");
		container.setMaxNrOfInstances(BigInteger.valueOf(0));

		AE ae = new AE();
		ae.setRequestReachability(true);
		ae.getPointOfAccess().add(poa);
		ae.setAppID(appId);

		ResponsePrimitive response = RequestSender.createAE(ae, appId);
		// Create Application sub-resources only if application not yet created
		if(response.getResponseStatusCode().equals(ResponseStatusCode.CREATED)) {
			container = new Container();
			container.setMaxNrOfInstances(BigInteger.valueOf(10));
			// Create DESCRIPTOR container sub-resource
			LOGGER.info(RequestSender.createContainer(response.getLocation(), SampleConstants.DESC, container));
			// Create STATE container sub-resource
			LOGGER.info(RequestSender.createContainer(response.getLocation(), SampleConstants.DATA, container));

			String content;
			// Create DESCRIPTION contentInstance on the DESCRIPTOR container resource
			content = ObixUtil.getDescriptorRep(SampleConstants.CSE_ID, appId, SampleConstants.DATA);
			ContentInstance contentInstance = new ContentInstance();
			contentInstance.setContent(content);
			contentInstance.setContentInfo(MimeMediaType.OBIX);
			RequestSender.createContentInstance(
					SampleConstants.CSE_PREFIX + "/" + appId + "/" + SampleConstants.DESC, null, contentInstance);

			// Create initial contentInstance on the STATE container resource
			content = ObixUtil.getStateRep(appId, initValue);
			contentInstance.setContent(content);
			RequestSender.createContentInstance(
					SampleConstants.CSE_PREFIX + "/" + appId + "/" + SampleConstants.DATA, null, contentInstance);
		}
	}

	/**
	 * Create the LAMP_ALL container
	 * @param poa
	 */
	private static void createLampAll(String poa) {
		// Creation of the LAMP_ALL container
		AE ae = new AE();
		ae.setRequestReachability(true);
		ae.getPointOfAccess().add(poa);
		ae.setAppID("LAMP_ALL");
		ResponsePrimitive response = RequestSender.createAE(ae, "LAMP_ALL");

		// Create descriptor container if not yet created
		if(response.getResponseStatusCode().equals(ResponseStatusCode.CREATED)){
			// Creation of the DESCRIPTOR container
			Container cnt = new Container();
			cnt.setMaxNrOfInstances(BigInteger.valueOf(10));
			RequestSender.createContainer(SampleConstants.CSE_PREFIX + "/" + "LAMP_ALL", SampleConstants.DESC, cnt);

			// Create the description
			ContentInstance cin = new ContentInstance();
			cin.setContent(ObixUtil.createLampAllDescriptor());
			cin.setContentInfo(MimeMediaType.OBIX);
			RequestSender.createContentInstance(SampleConstants.CSE_PREFIX + "/" + "LAMP_ALL" + "/" + SampleConstants.DESC, null, cin);
		}
	}

}
