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
package org.eclipse.om2m.core;

import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.AccessControl;
import org.eclipse.om2m.commons.constants.CSEType;
import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.commons.entities.AccessControlOriginatorEntity;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AccessControlRuleEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.resource.AccessControlPolicy;
import org.eclipse.om2m.commons.resource.RemoteCSE;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.utils.Util.DateUtil;
import org.eclipse.om2m.core.comm.RestClient;
import org.eclipse.om2m.core.controller.Controller;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.urimapper.UriMapper;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

/**
 * Initializer for CSE entity
 *
 */
public class CSEInitializer {

	/** Logger */
	private static final Log LOGGER = LogFactory.getLog(CSEInitializer.class);
	
	private static String contentFormat = System.getProperty("org.eclipse.om2m.registration.contentFormat", MimeMediaType.XML);
	/** Private constructor */
	private CSEInitializer(){
	}

	private static String acpAdminId;

	/**
	 * Initialize the current launching CSE.
	 */
	public static void init() throws InterruptedException{
		LOGGER.info("Initializating the cseBase");
		// Check the existence of the cseBase in the database
		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();
		CSEBaseEntity cseBase = dbs.getDAOFactory().getCSEBaseDAO().find(transaction, "/" + Constants.CSE_ID);
		// if the cseBase is not initialized, then create the base resources
		if(cseBase == null){
			// Create AccessRight resource
			LOGGER.info("Create AccessControlPolicy resource");
			initACP();
			
			// Create CSEBase resource
			LOGGER.info("Create CSEBase resource");
			initCSEBase();			

			if(!Constants.CSE_TYPE.equalsIgnoreCase(CSEType.IN) && Constants.CSE_AUTHENTICATION){
				LOGGER.info("Register CSE to another CSE.");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						registerCSE();
					}
				}).start();
			}
		} else {
			LOGGER.info("cseBase already initialized");
		}

	}

	/**
	 * Creates the root {@link CseBase} resource in DataBase.
	 */
	private static void initCSEBase() {
		CSEBaseEntity cseBaseEntity = new CSEBaseEntity();
		DBService db = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = db.getDbTransaction();
		transaction.open();
		String acpUri = UriMapper.getNonHierarchicalUri("/" + Constants.CSE_ID +"/" + Constants.CSE_NAME + "/" + Constants.ADMIN_PROFILE_ID); 
		AccessControlPolicyEntity acpEntity = db.getDAOFactory().
				getAccessControlPolicyDAO().find
				(transaction, acpUri);
		cseBaseEntity.getAccessControlPolicies().add(acpEntity);
		cseBaseEntity.getChildAccessControlPolicies().add(acpEntity);
		cseBaseEntity.setCreationTime(DateUtil.now());
		cseBaseEntity.setCseid(Constants.CSE_ID);
		cseBaseEntity.setCseType(CSEType.toBigInteger(Constants.CSE_TYPE));
		cseBaseEntity.setLastModifiedTime(DateUtil.now());
		cseBaseEntity.setName(Constants.CSE_NAME);
		cseBaseEntity.setResourceID("/" + Constants.CSE_ID);
		cseBaseEntity.setResourceType(BigInteger.valueOf(ResourceType.CSE_BASE));
		cseBaseEntity.setHierarchicalURI("/" + Constants.CSE_ID + "/" + Constants.CSE_NAME);
		// generating array of supported resources types
		int[] supportedResources = {
				ResourceType.ACCESS_CONTROL_POLICY,
				ResourceType.AE,
				ResourceType.CONTAINER,
				ResourceType.CONTENT_INSTANCE,
				ResourceType.CSE_BASE,
				ResourceType.GROUP,
				ResourceType.NODE,
				ResourceType.POLLING_CHANNEL,
				ResourceType.REMOTE_CSE,
				ResourceType.REQUEST,
				ResourceType.SUBSCRIPTION
		};
		
		for(int rt : supportedResources){
			cseBaseEntity.getSupportedResourceType().add(BigInteger.valueOf(rt));
		}

		cseBaseEntity.getPointOfAccess().add("http://" + Constants.CSE_IP+ ":" + Constants.CSE_PORT + Constants.CSE_CONTEXT);

		UriMapper.addNewUri(cseBaseEntity.getHierarchicalURI(), cseBaseEntity.getResourceID(), ResourceType.CSE_BASE);
		if (db.getDAOFactory().getCSEBaseDAO().find(transaction, cseBaseEntity.getResourceID()) == null){
			db.getDAOFactory().getCSEBaseDAO().create(transaction, cseBaseEntity);
		} else {
			db.getDAOFactory().getCSEBaseDAO().update(transaction, cseBaseEntity);
		}
		transaction.commit();
	}

	/**
	 * Sends {@link CseBase} resource in DataBase.
	 */
	private static void registerCSE() {
		RemoteCSE remoteCSE = new RemoteCSE();
		switch(Constants.CSE_TYPE.toLowerCase()){
		case CSEType.ASN:
			remoteCSE.setCseType(CSEType.ASN_CSE);
			break;
		case CSEType.MN:
			remoteCSE.setCseType(CSEType.MN_CSE);
			break;
		default:
			break;
		}
		String httpPoa = "http://" + Constants.CSE_IP + ":" + Constants.CSE_PORT + Constants.CSE_CONTEXT;
		if (Constants.CSE_CONTEXT.length() > 1){
			httpPoa += "/" ;
		}
		remoteCSE.getPointOfAccess().add(httpPoa);
		remoteCSE.setCSEID("/" + Constants.CSE_ID);
		remoteCSE.setCSEBase("//" + Constants.M2M_SP_ID + remoteCSE.getCSEID());
		remoteCSE.setRequestReachability(new Boolean(true));
		remoteCSE.setName(Constants.CSE_ID);
		
		String representation = DataMapperSelector.getDataMapperList().get(contentFormat).objToString(remoteCSE);

		RequestPrimitive request = new RequestPrimitive();
		request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
		String remotePoa = "http://" + Constants.REMOTE_CSE_IP + ":" + Constants.REMOTE_CSE_PORT + "/~" + Constants.REMOTE_CSE_CONTEXT;
		if (Constants.REMOTE_CSE_CONTEXT.length() > 1 && !Constants.REMOTE_CSE_CONTEXT.equals("/~")){
			remotePoa += "/";
		}
		remotePoa += Constants.REMOTE_CSE_ID;
		request.setTo(remotePoa);
		request.setContent(representation);
		request.setOperation(Operation.CREATE);
		request.setResourceType(BigInteger.valueOf(ResourceType.REMOTE_CSE));
		request.setRequestContentType(contentFormat);
		request.setRequestIdentifier("001");

		boolean registered = false ; 
		while(!registered){
			ResponsePrimitive response = RestClient.sendRequest(request);
			LOGGER.info("response after registration: " + response);
			if(response.getResponseStatusCode().equals(ResponseStatusCode.CREATED)
					|| response.getResponseStatusCode().equals(ResponseStatusCode.CONFLICT)){
				registered = true;
				break;
			} else {
				try {
					LOGGER.info("Error in registration to another CSE. Retrying in 10s");
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					LOGGER.debug("Interrupted Exception", e);
				}
			}
		}
		LOGGER.info("Successfully registered to " + Constants.REMOTE_CSE_ID);
		RemoteCSEEntity remoteCseEntity = new RemoteCSEEntity();
		remoteCseEntity.setCreationTime(DateUtil.now());
		remoteCseEntity.setLastModifiedTime(DateUtil.now());
		remoteCseEntity.setParentID("/" + Constants.CSE_ID);

		DBService dbs = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();
		remoteCseEntity.getAccessControlPolicies().add(
				dbs.getDAOFactory().getAccessControlPolicyDAO().find(
						transaction, acpAdminId));
		remoteCseEntity.setResourceType(BigInteger.valueOf(ResourceType.REMOTE_CSE));
		remoteCseEntity.setHierarchicalURI(
				"/" + Constants.CSE_ID + "/" + Constants.CSE_NAME + "/" + Constants.REMOTE_CSE_ID);
		String generatedId = Controller.generateId("", "");
		remoteCseEntity.setResourceID(
				"/" + Constants.CSE_ID + "/" + ShortName.REMOTE_CSE + 
				Constants.PREFIX_SEPERATOR + generatedId);
		remoteCseEntity.setName(Constants.REMOTE_CSE_NAME);
		UriMapper.addNewUri(remoteCseEntity.getHierarchicalURI(), 
				remoteCseEntity.getResourceID(), ResourceType.REMOTE_CSE);
		remoteCseEntity.setRemoteCseId("/"+Constants.REMOTE_CSE_ID);
		remoteCseEntity.setRemoteCseUri("//" + Constants.M2M_SP_ID + remoteCseEntity.getRemoteCseId());
		String poa = "http://" + Constants.REMOTE_CSE_IP + ":" + Constants.REMOTE_CSE_PORT + Constants.REMOTE_CSE_CONTEXT;
		if (Constants.REMOTE_CSE_CONTEXT.length() > 1){
			poa += "/";
		}
		remoteCseEntity.getPointOfAccess().add(poa);
		remoteCseEntity.setRequestReachability(true);
		CSEBaseEntity cseBase = dbs.getDAOFactory().getCSEBaseDAO().find(transaction, "/" + Constants.CSE_ID);
		cseBase.getRemoteCses().add(remoteCseEntity);
		dbs.getDAOFactory().getCSEBaseDAO().update(transaction, cseBase);
		dbs.getDAOFactory().getRemoteCSEDAO().create(transaction, remoteCseEntity);
		transaction.commit();
		transaction.close();
	}

	/**
	 * Creates a default {@link AccessControlPolicy} resource in DataBase.
	 */
	private static void initACP() {
		AccessControlPolicyEntity acp = new AccessControlPolicyEntity();
		acp.setParentID("/" + Constants.CSE_ID);
		acp.setCreationTime(DateUtil.now());
		acp.setLastModifiedTime(DateUtil.now());
		acp.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.ACP + Constants.PREFIX_SEPERATOR + Controller.generateId());
		acp.setName(Constants.ADMIN_PROFILE_ID);
		acp.setResourceType(BigInteger.valueOf(ResourceType.ACCESS_CONTROL_POLICY));
		acp.setHierarchicalURI("/" + Constants.CSE_ID + "/" + Constants.CSE_NAME + "/" + acp.getName());
		UriMapper.addNewUri(acp.getHierarchicalURI(), acp.getResourceID(), ResourceType.ACCESS_CONTROL_POLICY);

		// Self-privileges - all rights for admin
		AccessControlRuleEntity ruleEntity = new AccessControlRuleEntity();
		AccessControlOriginatorEntity originatorEntity = new AccessControlOriginatorEntity(Constants.ADMIN_REQUESTING_ENTITY);
		ruleEntity.getAccessControlOriginators().add(originatorEntity);
		ruleEntity.setCreate(true);
		ruleEntity.setRetrieve(true);
		ruleEntity.setUpdate(true);
		ruleEntity.setDelete(true);
		ruleEntity.setNotify(true);
		ruleEntity.setDiscovery(true);
		acp.getSelfPrivileges().add(ruleEntity);
		
		// Privileges - all rights for admin
		ruleEntity = new AccessControlRuleEntity();
		ruleEntity.setCreate(true);
		ruleEntity.setRetrieve(true);
		ruleEntity.setUpdate(true);
		ruleEntity.setDelete(true);
		ruleEntity.setNotify(true);
		ruleEntity.setDiscovery(true);
		ruleEntity.getAccessControlOriginators().add(new AccessControlOriginatorEntity(Constants.ADMIN_REQUESTING_ENTITY));
		ruleEntity.getAccessControlOriginators().add(new AccessControlOriginatorEntity("/" + Constants.CSE_ID));
		acp.getPrivileges().add(ruleEntity);
		
		// privileges for ALL originators (read + discovery)
		ruleEntity = new AccessControlRuleEntity();
		ruleEntity.setRetrieve(true);
		ruleEntity.setDiscovery(true);
		ruleEntity.getAccessControlOriginators().add(new AccessControlOriginatorEntity(Constants.GUEST_REQUESTING_ENTITY));
		ruleEntity.getAccessControlOriginators().add(new AccessControlOriginatorEntity(AccessControl.ORIGINATOR_ALL));
		acp.getPrivileges().add(ruleEntity);

		acpAdminId = acp.getResourceID() ;

		DBService db = PersistenceService.getInstance().getDbService();
		DBTransaction transaction = db.getDbTransaction();
		transaction.open();
		if (db.getDAOFactory().getAccessControlPolicyDAO().find(transaction, acp.getResourceID()) == null){
			db.getDAOFactory().getAccessControlPolicyDAO().create(transaction, acp);
		} else {
			db.getDAOFactory().getAccessControlPolicyDAO().update(transaction, acp);
		}
		transaction.commit();
		transaction.close();
	}
	
	
	public static void unregisterCse(){
		RequestPrimitive request = new RequestPrimitive();
		request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
		request.setOperation(Operation.DELETE);;
		String remotePoa = "http://" + Constants.REMOTE_CSE_IP + ":" + Constants.REMOTE_CSE_PORT + Constants.REMOTE_CSE_CONTEXT ;;
		if (Constants.REMOTE_CSE_CONTEXT.length() > 1){
			remotePoa += "/";
		}
		remotePoa += "/" +Constants.REMOTE_CSE_ID + "/" + Constants.CSE_ID;
		request.setTo(remotePoa);
		request.setResultContent(ResultContent.NOTHING);
		LOGGER.info("Sending unregistration request");
		ResponsePrimitive response = RestClient.sendRequest(request);
		LOGGER.info("Unregistration response:\n" + response);
	}


}
