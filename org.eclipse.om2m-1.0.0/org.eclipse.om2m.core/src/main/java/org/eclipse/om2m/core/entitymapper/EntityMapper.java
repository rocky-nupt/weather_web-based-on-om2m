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

import java.math.BigInteger;

import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.entities.LabelEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.Resource;

/**
 * Generic entity mapper
 *
 * @param <E> type of the Entity
 * @param <R> type of the Resource
 */
public abstract class EntityMapper<E extends ResourceEntity, R extends Resource> {
	
	/**
	 * Method used to map generic attributes to resource object
	 * @param entity
	 * @param resource
	 */
	private void mapGenericAttributes(E entity, R resource){
		resource.setCreationTime(entity.getCreationTime());
		resource.setLastModifiedTime(entity.getLastModifiedTime());
		resource.setName(entity.getName());
		resource.setParentID(entity.getParentID());
		resource.setResourceID(entity.getResourceID());
		resource.setResourceType(entity.getResourceType());
		for (LabelEntity lbl : entity.getLabelsEntities()){
			resource.getLabels().add(lbl.getLabel());
		}	
	}
	
	/**
	 * Main method that use the ResultContent from the RequestPrimitive
	 * @param entity to map 
	 * @param request currently performing 
	 * @return the mapped serializable resource
	 */
	public R mapEntityToResource(E entity, RequestPrimitive request){
		BigInteger resultContent = request.getResultContent();
		if(resultContent == null){
			resultContent = ResultContent.ATTRIBUTES;
		} else {
			if(request.getOperation().equals(Operation.CREATE)){
				resultContent = ResultContent.ATTRIBUTES;
			}
		}
		return mapEntityToResource(entity, resultContent);
	}
	
	/**
	 * Aux method to map entity using a specific resultContent
	 * @param entity to map
	 * @param resultContent to use
	 * @return the mapped serializable resource
	 */
	public R mapEntityToResource(E entity, BigInteger resultContent){
		R result = createResource();
		if(resultContent.equals(ResultContent.ATTRIBUTES) 
				|| resultContent.equals(ResultContent.ATTRIBUTES_AND_CHILD_REF)
				|| resultContent.equals(ResultContent.ATTRIBUTES_AND_CHILD_RES)
				|| resultContent.equals(ResultContent.HIERARCHICAL_ADRESS)
				|| resultContent.equals(ResultContent.HIERARCHICAL_AND_ATTRIBUTES)){
			mapGenericAttributes(entity, result);
			mapAttributes(entity, result);
		}
		if(resultContent.equals(ResultContent.ATTRIBUTES_AND_CHILD_REF)
				|| resultContent.equals(ResultContent.CHILD_REF)){
			mapChildResourceRef(entity, result);
		}
		if(resultContent.equals(ResultContent.ATTRIBUTES_AND_CHILD_RES)){
			mapChildResources(entity, result);
		}
		return result;
	}
	
	/**
	 * Map main attributes of a resource (not the generic attributes)
	 * @param entity to map
	 * @param resource result
	 */
	protected abstract void mapAttributes(E entity, R resource);
	
	/**
	 * Map child resource references of the resource
	 * @param entity to map 
	 * @param resource resource
	 */
	protected abstract void mapChildResourceRef(E entity, R resource);
	
	/**
	 * Map child reosurces using their attributes
	 * @param entity to map
	 * @param resource
	 */
	protected abstract void mapChildResources(E entity, R resource);
	
	/**
	 * Method use to create the object to return corresponding
	 * to the R type.
	 * @return the created empty resource
	 */
	protected abstract R createResource();
}
