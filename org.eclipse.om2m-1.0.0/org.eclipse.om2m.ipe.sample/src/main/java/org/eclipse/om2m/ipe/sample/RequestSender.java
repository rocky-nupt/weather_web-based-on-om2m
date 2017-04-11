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

import java.math.BigInteger;

import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.resource.AE;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.Resource;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.ipe.sample.controller.SampleController;

public class RequestSender {
	
	/**
	 * Private constructor to avoid creation of this object
	 */
	private RequestSender(){}
	
	public static ResponsePrimitive createResource(String targetId, String name, Resource resource, int resourceType){
		RequestPrimitive request = new RequestPrimitive();
		request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
		request.setTargetId(targetId);
		request.setResourceType(BigInteger.valueOf(resourceType));
		request.setRequestContentType(MimeMediaType.OBJ);
		request.setReturnContentType(MimeMediaType.OBJ);
		request.setContent(resource);
		request.setName(name);
		request.setOperation(Operation.CREATE);
		return SampleController.CSE.doRequest(request);
	}
	
	public static ResponsePrimitive createAE(AE resource, String name){
		return createResource("/" + Constants.CSE_ID, name, resource, ResourceType.AE);
	}
	
	public static ResponsePrimitive createContainer(String targetId, String name, Container resource){
		return createResource(targetId, name, resource, ResourceType.CONTAINER);
	}
	
	public static ResponsePrimitive createContentInstance(String targetId, String name, ContentInstance resource){
		return createResource(targetId, name, resource, ResourceType.CONTENT_INSTANCE);
	}
	
	public static ResponsePrimitive createContentInstance(String targetId, ContentInstance resource){
		return createContentInstance(targetId, null, resource);
	}

	public static ResponsePrimitive getRequest(String targetId){
		RequestPrimitive request = new RequestPrimitive();
		request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
		request.setTargetId(targetId);
		request.setReturnContentType(MimeMediaType.OBJ);
		request.setOperation(Operation.RETRIEVE);
		request.setRequestContentType(MimeMediaType.OBJ);
		return SampleController.CSE.doRequest(request);
	}
	
}
