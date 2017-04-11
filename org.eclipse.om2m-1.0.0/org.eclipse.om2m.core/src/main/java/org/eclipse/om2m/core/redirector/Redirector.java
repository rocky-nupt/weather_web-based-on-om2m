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
package org.eclipse.om2m.core.redirector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.CSEType;
import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.exceptions.Om2mException;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.RemoteCSE;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.core.comm.RestClient;
import org.eclipse.om2m.core.controller.AEController;
import org.eclipse.om2m.core.interworking.IpeSelector;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

/**
 * Re-target the REST request to the Distant CSE registered in the
 * {@link RemoteCSE} children.
 *
 */
public class Redirector {

	private static Log LOGGER = LogFactory.getLog(Redirector.class);

	/**
	 * Re-targets a request to a Distant SCL registered in the sclCollection.
	 * 
	 * @param requestIndication
	 *            - The generic request to handle.
	 * @return The generic returned response.
	 */
	public static ResponsePrimitive retarget(RequestPrimitive request) {
		String remoteCseId = "";
		ResponsePrimitive response = new ResponsePrimitive(request);

		try {
			remoteCseId = "/" + request.getTargetId().split("/")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.debug("Remote cse not found", e);
			throw new ResourceNotFoundException("Remote cse not found", e);
		}

		// get the database service
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();

		// get the dao of the parent
		DAO<RemoteCSEEntity> dao = dbs.getDAOFactory().getRemoteCSEbyCseIdDAO();
		RemoteCSEEntity csrEntity = dao.find(transaction, remoteCseId);
		if (csrEntity != null) {
			LOGGER.info("RemoteCSE found: " + csrEntity.getRemoteCseId());
			response = sendRedirectorRequest(request, csrEntity, transaction);
		} else {
			if (!Constants.CSE_TYPE.equalsIgnoreCase(CSEType.IN)) {
				LOGGER.info("Unknow CSE, sending request to registrar CSE: " + Constants.REMOTE_CSE_ID);
				csrEntity = dao.find(transaction, "/" + Constants.REMOTE_CSE_ID);
				// transfer the request and get the response
				response =sendRedirectorRequest(request, csrEntity, transaction);
			} else {
				// case nothing found
				throw new ResourceNotFoundException("RemoteCse with cseId " + remoteCseId + " has not been found");
			}
		}
		transaction.close();
		return response;
	}
	
	private static ResponsePrimitive sendRedirectorRequest(RequestPrimitive request, RemoteCSEEntity csrEntity, DBTransaction transaction){
		// test if the remoteCse is reachable
		if (!csrEntity.isRequestReachability()) {
			throw new Om2mException("Remote Cse is not request reachable", 
					ResponseStatusCode.TARGET_NOT_REACHABLE);
		}
		DBService dbs = PersistenceService.getInstance().getDbService();
		// get Point of Access
		String url = "";
		if (!csrEntity.getPointOfAccess().isEmpty()) {
			boolean done = false;
			int i = 0;
			// iterating on points of access while target are not reachable
			while (!done & i < csrEntity.getPointOfAccess().size()) {
				url = csrEntity.getPointOfAccess().get(i);
				// Remove a potential / added at the end of the poa
				if(url.endsWith("/")){
					LOGGER.debug("Removing / at the end of poa: " + url);
					url = url.substring(0, url.length() - 1);
				}
				
				if(request.getTo().startsWith("//")){
					url += request.getTo().replaceFirst("//", "/_/");
				} else if(request.getTo().startsWith("/")){
					url += request.getTo().replaceFirst("/", "/~/");
				} else {
					url+= "/" + request.getTo();
				}
				
				request.setTo(url);
				ResponsePrimitive response = RestClient.sendRequest(request);
				if(!(response.getResponseStatusCode()
						.equals(ResponseStatusCode.TARGET_NOT_REACHABLE))){
					done = true;
					if(i > 0){
						String poa = csrEntity.getPointOfAccess().get(i);
						csrEntity.getPointOfAccess().remove(i);
						csrEntity.getPointOfAccess().add(0, poa);
						dbs.getDAOFactory().getRemoteCSEDAO().update(transaction, csrEntity);
						transaction.commit();
					}
					return response;
				}
				i++;
			}
			// if we reach this point, there is no poa working
			ResponsePrimitive response = new ResponsePrimitive(request);
			response.setResponseStatusCode(ResponseStatusCode.TARGET_NOT_REACHABLE);
			response.setContent("Target is not reachable");
			response.setContentType(MimeMediaType.TEXT_PLAIN);
			return response;
		} else {
			// TODO to improve w/ polling channel policy
			throw new Om2mException("The point of access parameter is missing", ResponseStatusCode.TARGET_NOT_REACHABLE);
		}
	}

	public static ResponsePrimitive retargetNotify(RequestPrimitive request){
		ResponsePrimitive response = new ResponsePrimitive(request);
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction dbt = dbs.getDbTransaction();
		dbt.open();
		// get the AE 
		AeEntity ae =  dbs.getDAOFactory().getAeDAO().find(dbt, request.getTargetId());
		if(ae == null){
			dbt.close();
			throw new ResourceNotFoundException("AE resource " + request.getTargetId() + " not found.");
		}
	
		// FIXME use the correct originator when a notification is generated
		if(!request.getFrom().equals("/" + Constants.CSE_ID)){
			new AEController().checkACP(ae.getAccessControlPolicies(), request.getFrom(), Operation.NOTIFY);			
		}
		
		// Get point of access
		if(ae.getPointOfAccess().isEmpty() || !(ae.isRequestReachable())){
			throw new Om2mException("AE has no point of access", ResponseStatusCode.TARGET_NOT_REACHABLE);
		} else {
			boolean done = false ;
			int i = 0;
			// for each PoA
			while( !done && (i < ae.getPointOfAccess().size()) ){
				String poa = ae.getPointOfAccess().get(i);
				// if the PoA is a local IPE
				if(IpeSelector.getInterworkingList().containsKey(poa)){
					try{
						LOGGER.info("Sending notification to IPE: " + poa);
						response = IpeSelector.getInterworkingList().get(poa).doExecute(request);						
					} catch (Om2mException om2mE){
						LOGGER.info("Om2m exception caught in Redirector: " + om2mE.getMessage() );
						throw om2mE;
					} catch (Exception e){
						LOGGER.error("Exception caught in IPE execution",e);
						throw new Om2mException("IPE Internal Error", e, ResponseStatusCode.INTERNAL_SERVER_ERROR);
					}
					done = true;
				} else {
					request.setTo(poa);
					response = RestClient.sendRequest(request);
					if(!response.getResponseStatusCode().equals(ResponseStatusCode.TARGET_NOT_REACHABLE)){
						done = true;
						if(i > 0){
							ae.getPointOfAccess().remove(i);
							ae.getPointOfAccess().add(0, poa);
							dbs.getDAOFactory().getAeDAO().update(dbt, ae);
							dbt.commit();						
						}
					}
				}
				i++;
			}
		}
		dbt.close();
		return response;
	}

}
