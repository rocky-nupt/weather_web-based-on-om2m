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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.RequestStatus;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.MetaInformation;
import org.eclipse.om2m.commons.entities.RequestEntity;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.resource.Request;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.utils.Util.DateUtil;
import org.eclipse.om2m.core.controller.Controller;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;
import org.eclipse.om2m.core.entitymapper.EntityMapperFactory;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.urimapper.UriMapper;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

public class NonBlockingHandler {
	
	/** Private constructor to avoir creation of this object */
	private NonBlockingHandler(){}

	private static ExecutorService poolThread = Executors.newFixedThreadPool(10);

	private static Log LOGGER = LogFactory.getLog(NonBlockingHandler.class);

	public static ResponsePrimitive handle(RequestPrimitive request){
		LOGGER.info("Non blocking request received, building request object...");
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();
		CSEBaseEntity cseBaseEntity = dbs.getDAOFactory().getCSEBaseDAO().find(transaction, "/" + Constants.CSE_ID);
		
		ResponsePrimitive response = new ResponsePrimitive(request);
		RequestEntity requestEntity = new RequestEntity();
		try {
			if(request.getContent() != null){
				if(request.getRequestContentType().equals(MimeMediaType.OBJ)){
					requestEntity.setContent(DataMapperSelector.getDataMapperList().get(MimeMediaType.JSON).objToString(request.getContent()));
					requestEntity.setRequestContentType(MimeMediaType.JSON);
				} else {
					if(request.getContent() instanceof String && !((String)request.getContent()).isEmpty()){
						if(!request.getRequestContentType().equals(MimeMediaType.JSON)){
							Object resource = DataMapperSelector.getDataMapperList().
									get(request.getReturnContentType()).stringToObj((String)request.getContent());
							requestEntity.setContent(DataMapperSelector.getDataMapperList().get(MimeMediaType.JSON).objToString(resource));
							requestEntity.setRequestContentType(request.getRequestContentType());
						} else {
							requestEntity.setContent((String) request.getContent());
							requestEntity.setRequestContentType(request.getRequestContentType());
						}						
					}
				}
			}
		} catch (Exception e){
			throw new BadRequestException("Error in provided content", e);
		}
		requestEntity.setCreationTime(DateUtil.now());
		
		String requestIdentifier = Controller.generateId();
		requestEntity.setName(ShortName.REQ + "_" + requestIdentifier);
		requestEntity.setHierarchicalURI(cseBaseEntity.getHierarchicalURI() + "/" + requestEntity.getName());
		requestEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.REQ + Constants.PREFIX_SEPERATOR + requestIdentifier);
		requestEntity.setLastModifiedTime(DateUtil.now());
		MetaInformation metaInf = new MetaInformation();
		metaInf.setDeliveryAggregation(request.isDeliveryAggregation());
		metaInf.setDiscoveryResultType(request.getDiscoveryResultType());
		// TODO EventCat nblock handler
		metaInf.setGroupRequestIdentifier(request.getGroupRequestIdentifier());
		metaInf.setName(request.getName());
		metaInf.setOperationalExecutionTime(request.getOperationExecutionTime());
		metaInf.setOriginatingTimestamp(request.getOriginatingTimestamp());
		metaInf.setRequestExpirationTimestamp(request.getResultExpirationTimestamp());
		metaInf.setResourceType(request.getResourceType());
		metaInf.setResponseType(request.getResponseTypeInfo().getResponseType());
		metaInf.setResultContent(request.getResultContent());
		metaInf.setResultExpirationTimestamp(request.getResultExpirationTimestamp());
		if(request.getResultPersistence() != null){
			metaInf.setResultPersistence(request.getResultPersistence().toString());			
		}
		requestEntity.setMetaInformation(metaInf);
		
		requestEntity.setOperation(request.getOperation());
		requestEntity.setOriginator(request.getFrom());
		requestEntity.setParentID(cseBaseEntity.getResourceID());
		requestEntity.setResourceType(ResourceType.REQUEST);
		requestEntity.setReturnContentType(request.getReturnContentType());
		requestEntity.setStateTag(BigInteger.valueOf(0));
		requestEntity.setTarget(request.getTo());
		requestEntity.setRequestID(requestIdentifier);
		requestEntity.setRequestStatus(RequestStatus.PENDING);
		UriMapper.addNewUri(requestEntity.getHierarchicalURI(), requestEntity.getResourceID(), ResourceType.REQUEST);
		
		dbs.getDAOFactory().getRequestEntityDAO().create(transaction, requestEntity);
		
		RequestEntity requestDb = dbs.getDAOFactory().getRequestEntityDAO().find(transaction, requestEntity.getResourceID());
		cseBaseEntity.getChildReq().add(requestDb);
		dbs.getDAOFactory().getCSEBaseDAO().update(transaction, cseBaseEntity);
		transaction.commit();
		
		Request requestResource = EntityMapperFactory.getRequestMapper().mapEntityToResource(requestEntity, ResultContent.ATTRIBUTES);
		response.setContent(requestResource.getResourceID());
		response.setContentType(MimeMediaType.TEXT_PLAIN);
		
		response.setResponseStatusCode(ResponseStatusCode.ACCEPTED);
		response.setLocation(requestDb.getResourceID());
		
		poolThread.execute(new NonBlockingWorker(requestEntity.getResourceID(),request));
		
		transaction.close();
		return response;
	}

}
