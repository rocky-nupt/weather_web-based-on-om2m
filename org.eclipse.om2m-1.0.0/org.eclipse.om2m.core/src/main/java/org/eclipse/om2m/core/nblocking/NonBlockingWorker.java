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
package org.eclipse.om2m.core.nblocking;

import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.RequestStatus;
import org.eclipse.om2m.commons.constants.ResponseType;
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.entities.RequestEntity;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;
import org.eclipse.om2m.core.entitymapper.EntityMapperFactory;
import org.eclipse.om2m.core.notifier.Notifier;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.router.Patterns;
import org.eclipse.om2m.core.router.Router;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

public class NonBlockingWorker extends Thread {

	private String requestIdentifier;
	private RequestPrimitive request;
	private static final Log LOGGER = LogFactory.getLog(NonBlockingWorker.class);
	private boolean async = false;
	
	public NonBlockingWorker(String requestIdentifier, RequestPrimitive request) {
		super();
		this.requestIdentifier = requestIdentifier;
		this.request = request;
	}

	@Override
	public void run() {
		// Get the db service & transaction
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();
		
		RequestEntity managedRequest = dbs.getDAOFactory().getRequestEntityDAO().
				find(transaction, requestIdentifier);
		async = request.getResponseTypeInfo().getResponseType().equals(ResponseType.NON_BLOCKING_REQUEST_ASYNCH);
		
		request.getResponseTypeInfo().setResponseType(ResponseType.BLOCKING_REQUEST);
		
		// Case of retargeting, changing the request status to forwarded
		if(!Patterns.match(Patterns.NON_RETARGETING_PATTERN, request.getTo())){
			managedRequest.setRequestStatus(RequestStatus.FORWARDED);
			managedRequest.setStateTag(managedRequest.getStateTag().add(BigInteger.valueOf(1)));
			dbs.getDAOFactory().getRequestEntityDAO().update(transaction, managedRequest);
			transaction.commit();
			transaction.close();
			transaction = dbs.getDbTransaction();
			transaction.open();
			managedRequest = dbs.getDAOFactory().getRequestEntityDAO().
					find(transaction, requestIdentifier);
		}
		
		request.setReturnContentType(managedRequest.getReturnContentType());
		// Perform the request
		ResponsePrimitive response = new Router().doRequest(request);
		
		if(response.getResponseStatusCode().intValue() >= 4000){
			managedRequest.setRequestStatus(RequestStatus.FAILED);
			LOGGER.info("Request " + requestIdentifier + " failed.");
		} else {
			LOGGER.info("Request " + requestIdentifier + " completed");
			managedRequest.setRequestStatus(RequestStatus.COMPLETED);
		}
		
		if(response.getContent() != null){
			if(request.getReturnContentType().equals(MimeMediaType.OBJ)){
				managedRequest.setOperationResultContent(DataMapperSelector.
						getDataMapperList().get(MimeMediaType.JSON).objToString(response.getContent()));
			} else if(request.getReturnContentType().equals(MimeMediaType.XML)){
				Object objReseult = DataMapperSelector.getDataMapperList().
						get(MimeMediaType.XML).stringToObj((String)response.getContent());
				String jsonResult = DataMapperSelector.getDataMapperList().get(MimeMediaType.JSON).objToString(objReseult);
				managedRequest.setOperationResultContent(jsonResult);
			}
			else {
				managedRequest.setOperationResultContent((String) response.getContent());				
			}
		}
		managedRequest.setOperationResultEventCategory(response.getEventCategory());
		managedRequest.setOperationResultFrom(response.getFrom());
		managedRequest.setOperationResultOriginatingTimestamp(response.getOriginatingTimestamp());
		managedRequest.setOperationResultRequestIdentifier(response.getRequestIdentifier());
		managedRequest.setOperationResultResponseStatusCode(response.getResponseStatusCode());
		managedRequest.setOperationResultResultExpirationTimestamp(response.getResultExpirationTimestamp());
		managedRequest.setOperationResultTo(response.getTo());
		dbs.getDAOFactory().getRequestEntityDAO().update(transaction, managedRequest);
		transaction.commit();
		
		if(async){
			LOGGER.info("Asynchronous case, notifying URIs.");
			String representation = DataMapperSelector.
					getDataMapperList().get(request.getReturnContentType()).
					objToString(
							EntityMapperFactory.getRequestMapper().
							mapEntityToResource(managedRequest, ResultContent.ATTRIBUTES)
					);
			for(String uriNotif : request.getResponseTypeInfo().getNotificationURI()){
				RequestPrimitive notifRequest = new RequestPrimitive();
				notifRequest.setTo(uriNotif);
				notifRequest.setContent(representation);
				notifRequest.setFrom("/" + Constants.CSE_ID);
				notifRequest.setOperation(Operation.NOTIFY);
				notifRequest.setRequestContentType(request.getReturnContentType());
				Notifier.notify(notifRequest, uriNotif);
			}
		}
		
		transaction.close();
	}

	/**
	 * @return the requestIdentifier
	 */
	public String getRequestIdentifier() {
		return requestIdentifier;
	}

	/**
	 * @param requestIdentifier the requestIdentifier to set
	 */
	public void setRequestIdentifier(String requestIdentifier) {
		this.requestIdentifier = requestIdentifier;
	}
	
}
