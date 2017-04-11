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
package org.eclipse.om2m.core.notifier;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.NotificationContentType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResourceStatus;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.MgmtObjEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.ScheduleEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.exceptions.Om2mException;
import org.eclipse.om2m.commons.resource.Notification;
import org.eclipse.om2m.commons.resource.Notification.NotificationEvent;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.Resource;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.core.comm.RestClient;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;
import org.eclipse.om2m.core.entitymapper.EntityMapper;
import org.eclipse.om2m.core.entitymapper.EntityMapperFactory;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.router.Patterns;
import org.eclipse.om2m.core.router.Router;
import org.eclipse.om2m.core.thread.CoreExecutor;
import org.eclipse.om2m.core.urimapper.UriMapper;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;

/**
 * Notifies subscribers when a change occurs on a resource according to their subscriptions.
 */
public class Notifier {
	/** Logger */
	private static Log LOGGER = LogFactory.getLog(Notifier.class);

	/**
	 * Finds all resource subscribers and notifies them.
	 * @param statusCode - Notification status code
	 * @param resource - Notification resource
	 */
	public static void notify(List<SubscriptionEntity> listSubscription, ResourceEntity resource, int resourceStatus) {
		if (listSubscription != null){
			for(SubscriptionEntity sub : listSubscription){
				NotificationWorker worker = new NotificationWorker(sub, resourceStatus, resource);
				CoreExecutor.postThread(worker);
			}
		}
	}

	/**
	 * Used in DELETE procedure when a resource is deleted. It notifies the subscribed resource
	 * and the parent resource subscribed entities. 
	 * @param listSubs
	 * @param resourceDeleted
	 */
	public static void notifyDeletion(List<SubscriptionEntity> listSubs, ResourceEntity resourceDeleted){
		List<SubscriptionEntity> parentSubscriptions = getParentSubscriptions(resourceDeleted);
		if(parentSubscriptions != null){
			notify(parentSubscriptions, resourceDeleted, ResourceStatus.CHILD_DELETED);			
		}
		if(listSubs != null){
			notify(listSubs, resourceDeleted, ResourceStatus.DELETED);			
		}
	}

	public static void performVerificationRequest(RequestPrimitive request,
			SubscriptionEntity subscriptionEntity) {
		for(String uri : subscriptionEntity.getNotificationURI()){
			if(!uri.equals(request.getFrom())){
				Notification notification = new Notification();
				notification.setCreator(subscriptionEntity.getCreator());
				notification.setVerificationRequest(true);
				notification.setSubscriptionReference(subscriptionEntity.getHierarchicalURI());
				notification.setSubscriptionDeletion(false);
				RequestPrimitive notifRequest = new RequestPrimitive();
				notifRequest.setContent(DataMapperSelector.getDataMapperList().get(MimeMediaType.XML).objToString(notification));
				notifRequest.setFrom("/" + Constants.CSE_ID);
				notifRequest.setTo(uri);
				notifRequest.setOperation(Operation.NOTIFY);
				notifRequest.setRequestContentType(MimeMediaType.XML);
				notifRequest.setReturnContentType(MimeMediaType.XML);
				ResponsePrimitive resp = notify(notifRequest, uri);
				if(resp.getResponseStatusCode().equals(ResponseStatusCode.TARGET_NOT_REACHABLE)){
					throw new Om2mException("Error during the verification request", 
							ResponseStatusCode.SUBSCRIPTION_VERIFICATION_INITIATION_FAILED);
				}
				if(resp.getResponseStatusCode().equals(ResponseStatusCode.SUBSCRIPTION_CREATOR_HAS_NO_PRIVILEGE)
						|| resp.getResponseStatusCode().equals(ResponseStatusCode.SUBSCRIPTION_HOST_HAS_NO_PRIVILEGE)){
					throw new Om2mException(resp.getResponseStatusCode());
				}
			}
		}
	}

	public static ResponsePrimitive notify(RequestPrimitive request, String contact){
		// Check whether the subscription contact is protocol-dependent or not.
		LOGGER.info("Sending notify request to: " + contact);
		if(contact.matches(".*://.*")){ 
			// Contact = protocol-dependent -> direct notification using the rest client.
			request.setTo(contact);
			return RestClient.sendRequest(request);
		}else{
			request.setTargetId(contact);
			LOGGER.info("Sending notify request...");
			return new Router().doRequest(request);
		}
	}

	/**
	 * Used to retrieve the subscription list of the parent resource
	 * @param resource 
	 * @return
	 */
	private static List<SubscriptionEntity> getParentSubscriptions(
			ResourceEntity resourceDeleted) {
		List<SubscriptionEntity> result;
		// Get parent id
		String[] ids = resourceDeleted.getHierarchicalURI().split("/");
		String parentHierarchicalId = resourceDeleted.getHierarchicalURI().replace("/" + ids[ids.length - 1], "");
		String parentId = UriMapper.getNonHierarchicalUri(parentHierarchicalId);
		// get parent entity
		DBService dbs = PersistenceService.getInstance().getDbService();

		DAO<?> dao = Patterns.getDAO(parentId, dbs);
		DBTransaction transaction = dbs.getDbTransaction();
		transaction.open();
		ResourceEntity parentEntity = (ResourceEntity) dao.find(transaction, parentId);
		// get the sub list from parent
		switch(parentEntity.getResourceType().intValue()){
		case ResourceType.ACCESS_CONTROL_POLICY:
			AccessControlPolicyEntity acp = (AccessControlPolicyEntity) parentEntity;
			result = acp.getChildSubscriptions();
			break;
		case ResourceType.AE:
			AeEntity ae = (AeEntity) parentEntity;
			result = ae.getSubscriptions();
			break;
		case ResourceType.CONTAINER:
			ContainerEntity cnt = (ContainerEntity) parentEntity;
			result = cnt.getSubscriptions();
			break;
		case ResourceType.CSE_BASE:
			CSEBaseEntity csb = (CSEBaseEntity) parentEntity;
			result = csb.getSubscriptions();
			break;
		case ResourceType.GROUP:
			GroupEntity group = (GroupEntity) parentEntity;
			result = group.getSubscriptions();
			break;
		case ResourceType.REMOTE_CSE:
			RemoteCSEEntity csr = (RemoteCSEEntity) parentEntity;
			result = csr.getSubscriptions();
			break;
		case ResourceType.SCHEDULE:
			ScheduleEntity schedule = (ScheduleEntity) parentEntity;
			result = schedule.getSubscriptions();
			break;
		default:
			result = new ArrayList<SubscriptionEntity>();
		}
		transaction.close();
		return result;
	}

	/**
	 * Worker that perform the notification task for a subscription
	 *
	 */
	static class NotificationWorker implements Runnable {
		/** resource status of the notification */
		private int resourceStatus;
		/** the subscription to handle */
		private SubscriptionEntity sub;
		/** the resource to be sent */
		private ResourceEntity resource;

		public NotificationWorker(SubscriptionEntity sub, int resourceStatus, ResourceEntity resource) {
			this.resourceStatus = resourceStatus;
			this.sub = sub;
			this.resource = resource;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			final RequestPrimitive request = new RequestPrimitive();
			Notification notification = new Notification();
			NotificationEvent notifEvent = new NotificationEvent();
			notification.setNotificationEvent(notifEvent);

			// Set attributes of notification object
			notifEvent.setResourceStatus(BigInteger.valueOf(resourceStatus));
			notification.setCreator(sub.getCreator());

			// Set request parameters
			request.setOperation(Operation.NOTIFY);
			request.setFrom("/" + Constants.CSE_ID);

			if(resourceStatus == ResourceStatus.DELETED){
				notification.setSubscriptionDeletion(true);
			} else {
				notification.setSubscriptionDeletion(false);
			}

			notification.setSubscriptionReference(sub.getHierarchicalURI());

			// Get the representation of the content
			Resource serializableResource;
			EntityMapper mapper ;
			if (sub.getNotificationContentType() != null){
				if (resource.getResourceType().equals(ResourceType.MGMT_OBJ)) {
					mapper = EntityMapperFactory.getMapperForMgmtObj((MgmtObjEntity) resource);
				} else {
					mapper = EntityMapperFactory.
							getMapperFromResourceType(resource.getResourceType().intValue());
				}
				if(sub.getNotificationContentType().equals(NotificationContentType.MODIFIED_ATTRIBUTES)){
					serializableResource = (Resource)mapper.mapEntityToResource(resource, ResultContent.ATTRIBUTES);
					notification.getNotificationEvent().setRepresentation(serializableResource);
					request.setRequestContentType(MimeMediaType.XML);
				} else if(sub.getNotificationContentType().equals(NotificationContentType.WHOLE_RESOURCE)){
					serializableResource = (Resource) mapper.mapEntityToResource(resource, ResultContent.ATTRIBUTES);
					notification.getNotificationEvent().setRepresentation(serializableResource);
					request.setRequestContentType(MimeMediaType.XML);
				} 
			} 
			// Set the content
			request.setContent(DataMapperSelector.getDataMapperList().get(MimeMediaType.XML).objToString(notification));
			// For each notification URI: send the notify request
			for(final String uri : sub.getNotificationURI()){
				CoreExecutor.postThread(new Runnable(){
					public void run() {
						Notifier.notify(request, uri);    					
					};
				});
			}
		}
	}

}
