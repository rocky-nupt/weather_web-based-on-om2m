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
package org.eclipse.om2m.core.entitymapper;

import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.entities.RequestEntity;
import org.eclipse.om2m.commons.resource.MetaInformation;
import org.eclipse.om2m.commons.resource.OperationResult;
import org.eclipse.om2m.commons.resource.PrimitiveContent;
import org.eclipse.om2m.commons.resource.Request;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;

/**
 * Entity mapper for Request resource.
 *
 */
public class RequestMapper extends EntityMapper<RequestEntity, Request> {

	@Override
	protected void mapAttributes(RequestEntity entity, Request resource) {
		if(entity.getContent() != null){
			PrimitiveContent pc = new PrimitiveContent();
			pc.getAny().add(DataMapperSelector.
					getDataMapperList().get(MimeMediaType.JSON).
					stringToObj(entity.getContent()));
			resource.setContent(pc);
		}
		resource.setMetaInformation(mapMetaInformations(entity.getMetaInformation()));
		resource.setOperation(entity.getOperation());
		resource.setOperationResult(mapOperationResult(entity));
		resource.setOriginator(entity.getOriginator());
		resource.setRequestID(entity.getRequestID());
		resource.setRequestStatus(entity.getRequestStatus());
		resource.setStateTag(entity.getStateTag());
		resource.setTarget(entity.getTarget());
	}

	@Override
	protected void mapChildResourceRef(RequestEntity entity, Request resource) {
		// TODO subscriptions childs
	}

	@Override
	protected void mapChildResources(RequestEntity entity, Request resource) {
		// TODO subscription childs
	}

	@Override
	protected Request createResource() {
		return new Request();
	}
	
	/**
	 * Map the MetaInforamtion resource
	 * @param entityMetaInf to map
	 * @return the mapped MetaInformation
	 */
	private MetaInformation mapMetaInformations(org.eclipse.om2m.commons.entities.MetaInformation entityMetaInf){
		MetaInformation metaInf = new MetaInformation();
		metaInf.setDeliveryAggregation(entityMetaInf.getDeliveryAggregation());
		metaInf.setDiscoveryResultType(entityMetaInf.getDiscoveryResultType());
		// TODO EventCategory request mapper
		//		metaInf.setEventCategory(entityMetaInf.getEventCategory());
		// TODO FilterCriteria request mapper
		//		metaInf.setFilterCriteria(entityMetaInf.getFilterCriteria());
		metaInf.setGroupRequestIdentifier(entityMetaInf.getGroupRequestIdentifier());
		metaInf.setName(entityMetaInf.getName());
		metaInf.setOperationalExecutionTime(entityMetaInf.getOperationalExecutionTime());
		metaInf.setOriginatingTimestamp(entityMetaInf.getOriginatingTimestamp());
		metaInf.setRequestExpirationTimestamp(entityMetaInf.getRequestExpirationTimestamp());
		metaInf.setResourceType(entityMetaInf.getResourceType());
		metaInf.setResultContent(entityMetaInf.getResultContent());
		metaInf.setResultExpirationTimestamp(entityMetaInf.getResultExpirationTimestamp());
		metaInf.setResultPersistence(entityMetaInf.getResultPersistence());
		return metaInf;
	}
	
	/**
	 * Map the Operation Result resource
	 * @param entity that has operation result attributes
	 * @return the mapped OperationResult
	 */
	private OperationResult mapOperationResult(RequestEntity entity) {
		OperationResult result = new OperationResult();
		if(entity.getOperationResultContent() != null){
			PrimitiveContent pc = new PrimitiveContent();
			pc.getAny().add(DataMapperSelector.
					getDataMapperList().get(MimeMediaType.JSON).
					stringToObj(entity.getOperationResultContent()));
			result.setContent(pc);
		}
		result.setEventCategory(entity.getOperationResultEventCategory());
		result.setFrom(entity.getOperationResultFrom());
		result.setOriginatingTimestamp(entity.getOperationResultOriginatingTimestamp());
		result.setRequestIdentifier(entity.getOperationResultRequestIdentifier());
		result.setResponseStatusCode(entity.getOperationResultResponseStatusCode());
		result.setResultExpirationTimestamp(entity.getOperationResultResultExpirationTimestamp());
		result.setTo(entity.getOperationResultTo());		
		return result;
	}
	
}
