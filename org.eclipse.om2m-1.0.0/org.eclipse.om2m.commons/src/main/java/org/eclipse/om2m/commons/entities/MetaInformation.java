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
package org.eclipse.om2m.commons.entities;

import java.math.BigInteger;

import javax.persistence.Embeddable;

/**
 * Embeddable meta information for entities
 *
 */
@Embeddable
public class MetaInformation {

	protected BigInteger resourceType;
	protected String name;
	protected String originatingTimestamp;
	protected String requestExpirationTimestamp;
	protected String resultExpirationTimestamp;
	protected String operationalExecutionTime;
	protected BigInteger responseType;
	protected String resultPersistence;
	protected BigInteger resultContent;
	protected String eventCatType;
	protected BigInteger eventCatNo;
	protected Boolean deliveryAggregation;
	protected String groupRequestIdentifier;
	
	// TODO filter criteria

	protected BigInteger discoveryResultType;

	/**
	 * @return the resourceType
	 */
	public BigInteger getResourceType() {
		return resourceType;
	}

	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(BigInteger resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the originatingTimestamp
	 */
	public String getOriginatingTimestamp() {
		return originatingTimestamp;
	}

	/**
	 * @param originatingTimestamp the originatingTimestamp to set
	 */
	public void setOriginatingTimestamp(String originatingTimestamp) {
		this.originatingTimestamp = originatingTimestamp;
	}

	/**
	 * @return the requestExpirationTimestamp
	 */
	public String getRequestExpirationTimestamp() {
		return requestExpirationTimestamp;
	}

	/**
	 * @param requestExpirationTimestamp the requestExpirationTimestamp to set
	 */
	public void setRequestExpirationTimestamp(String requestExpirationTimestamp) {
		this.requestExpirationTimestamp = requestExpirationTimestamp;
	}

	/**
	 * @return the resultExpirationTimestamp
	 */
	public String getResultExpirationTimestamp() {
		return resultExpirationTimestamp;
	}

	/**
	 * @param resultExpirationTimestamp the resultExpirationTimestamp to set
	 */
	public void setResultExpirationTimestamp(String resultExpirationTimestamp) {
		this.resultExpirationTimestamp = resultExpirationTimestamp;
	}

	/**
	 * @return the operationalExecutionTime
	 */
	public String getOperationalExecutionTime() {
		return operationalExecutionTime;
	}

	/**
	 * @param operationalExecutionTime the operationalExecutionTime to set
	 */
	public void setOperationalExecutionTime(String operationalExecutionTime) {
		this.operationalExecutionTime = operationalExecutionTime;
	}

	/**
	 * @return the responseType
	 */
	public BigInteger getResponseType() {
		return responseType;
	}

	/**
	 * @param responseType the responseType to set
	 */
	public void setResponseType(BigInteger responseType) {
		this.responseType = responseType;
	}

	/**
	 * @return the resultPersistence
	 */
	public String getResultPersistence() {
		return resultPersistence;
	}

	/**
	 * @param resultPersistence the resultPersistence to set
	 */
	public void setResultPersistence(String resultPersistence) {
		this.resultPersistence = resultPersistence;
	}

	/**
	 * @return the resultContent
	 */
	public BigInteger getResultContent() {
		return resultContent;
	}

	/**
	 * @param resultContent the resultContent to set
	 */
	public void setResultContent(BigInteger resultContent) {
		this.resultContent = resultContent;
	}

	/**
	 * @return the eventCatType
	 */
	public String getEventCatType() {
		return eventCatType;
	}

	/**
	 * @param eventCatType the eventCatType to set
	 */
	public void setEventCatType(String eventCatType) {
		this.eventCatType = eventCatType;
	}

	/**
	 * @return the eventCatNo
	 */
	public BigInteger getEventCatNo() {
		return eventCatNo;
	}

	/**
	 * @param eventCatNo the eventCatNo to set
	 */
	public void setEventCatNo(BigInteger eventCatNo) {
		this.eventCatNo = eventCatNo;
	}

	/**
	 * @return the deliveryAggregation
	 */
	public Boolean getDeliveryAggregation() {
		return deliveryAggregation;
	}

	/**
	 * @param deliveryAggregation the deliveryAggregation to set
	 */
	public void setDeliveryAggregation(Boolean deliveryAggregation) {
		this.deliveryAggregation = deliveryAggregation;
	}

	/**
	 * @return the groupRequestIdentifier
	 */
	public String getGroupRequestIdentifier() {
		return groupRequestIdentifier;
	}

	/**
	 * @param groupRequestIdentifier the groupRequestIdentifier to set
	 */
	public void setGroupRequestIdentifier(String groupRequestIdentifier) {
		this.groupRequestIdentifier = groupRequestIdentifier;
	}

	/**
	 * @return the discoveryResultType
	 */
	public BigInteger getDiscoveryResultType() {
		return discoveryResultType;
	}

	/**
	 * @param discoveryResultType the discoveryResultType to set
	 */
	public void setDiscoveryResultType(BigInteger discoveryResultType) {
		this.discoveryResultType = discoveryResultType;
	}
	
	

}
