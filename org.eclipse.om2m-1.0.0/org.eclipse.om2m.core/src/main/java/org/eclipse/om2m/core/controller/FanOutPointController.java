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
package org.eclipse.om2m.core.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.resource.AggregatedResponse;
import org.eclipse.om2m.commons.resource.PrimitiveContent;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.core.router.Router;
import org.eclipse.om2m.core.thread.CoreExecutor;

/**
 * Controller for fan out point handling (virtual resource)
 *
 */
public class FanOutPointController extends Controller {
	
	private String foptSuffix;
	
	public FanOutPointController(){
		super();
	}
	
	public FanOutPointController(String foptSuffix){
		this.foptSuffix = foptSuffix;
	}

	/**
	 * Fan out the request verifying access rights etc
	 * @param request
	 * @return response primitive including aggregated response(s)
	 */
	protected ResponsePrimitive fanOutRequest(RequestPrimitive request) {
		String targetGroup = request.getTargetId();
		AggregatedResponse aggResp = new AggregatedResponse();
//		ArrayList<RequestPrimitive> requests = new ArrayList<>();
		ResponsePrimitive resp = new ResponsePrimitive(request);
		List<Future<ResponsePrimitive>> listOfResponse = new ArrayList<Future<ResponsePrimitive>>();
		
		// retrieve the parent group
		GroupEntity group = dbs.getDAOFactory().getGroupDAO().find(transaction, targetGroup);
		// check authorization of the originator 
		List<AccessControlPolicyEntity> acpList = group.getAccessControlPolicies();
		checkACP(acpList, request.getFrom(), request.getOperation()) ;
		
		// TODO validate member types if not retrieve

//		ArrayList<FanOutSender> threads = new ArrayList<>();
		// fanout request to each member
		for (String to : group.getMemberIDs()){
			RequestPrimitive fanRequest = request.cloneParameters();
			// if a suffix is provided add it to the request uri
			LOGGER.info("Suffix in FanOutController " + this.foptSuffix);
			if(this.foptSuffix != null){
				fanRequest.setTo(to + foptSuffix);				
			} else {
				fanRequest.setTo(to);
			}
			LOGGER.info(fanRequest.getTo());
			fanRequest.setReturnContentType(MimeMediaType.OBJ);
			listOfResponse.add(CoreExecutor.submit(new FanOutWorker(fanRequest)));
		}

		for(Future<ResponsePrimitive> response : listOfResponse){
			try {
				aggResp.getResponsePrimitive().add(response.get());
			} catch (InterruptedException | ExecutionException e) {
				LOGGER.error("FanOutCallable exception", e);
				ResponsePrimitive responsePrimitive = new ResponsePrimitive();
				PrimitiveContent content = new PrimitiveContent();
				content.getAny().add(e.getMessage());
				responsePrimitive.setContent(content);
				responsePrimitive.setResponseStatusCode(ResponseStatusCode.INTERNAL_SERVER_ERROR);
			}
		}
		// sub group creation?

		resp.setResponseStatusCode(ResponseStatusCode.OK);
		resp.setContent(aggResp);
		return resp;
	}

	@Override
	public ResponsePrimitive doCreate(RequestPrimitive request) {
		// fan out the request
		return fanOutRequest(request);
	}

	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		// fan out the request
		return fanOutRequest(request);
	}

	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		// fan out the request
		return fanOutRequest(request);
	}

	@Override
	public ResponsePrimitive doDelete(RequestPrimitive request) {
		// fan out the request
		return fanOutRequest(request);
	}
	
	/**
	 * This inner class defines the Callable that will return the 
	 * response of a request for the FanOutPoint broadcast.
	 *
	 */
	private static class FanOutWorker implements Callable<ResponsePrimitive>{
		
		/** The request that will be fan out to the router. */
		private RequestPrimitive request;
		
		/**
		 * Main constructor with the request to be fan out.
		 * @param request request to fan out
		 */
		public FanOutWorker(RequestPrimitive request){
			this.request = request;
		}
		
		/**
		 * Implementation of the call() method from Callable<T>. 
		 * It sends the request to the router and retrieve the result
		 * that will be send to the caller of the class.
		 */
		@Override
		public ResponsePrimitive call() throws Exception {
			ResponsePrimitive resp = new Router().doRequest(request);
			resp.setPrimitiveContent(new PrimitiveContent());
			resp.getPritimitiveContent().getAny().add(resp.getContent());
			return resp;
		}
		
	}

}
