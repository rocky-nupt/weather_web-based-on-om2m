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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Request JPA entity
 *
 */
@Entity(name = DBEntities.REQUEST_ENTITY)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class RequestEntity extends ResourceEntity {

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CSEBaseEntity.class)
	@JoinTable(
			name = DBEntities.CSEB_REQ_JOIN,
			inverseJoinColumns={@JoinColumn(name=DBEntities.CSEB_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)},
			joinColumns={@JoinColumn(name=DBEntities.REQ_JOIN_ID, referencedColumnName=ShortName.RESOURCE_ID)}
			)
	protected CSEBaseEntity cseBaseParent;
	
	@Column(name = ShortName.STATETAG)
	protected BigInteger stateTag;
	@Column(name = ShortName.OPERATION)
	protected BigInteger operation;
	@Column(name = ShortName.TARGET)
	protected String target;
	@Column(name = ShortName.ORIGINATOR)
	protected String originator;
	@Column(name = ShortName.REQUEST_ID)
	protected String requestID;
	@Embedded
	protected MetaInformation metaInformation;
	@Column(name = ShortName.REQUEST_CONTENT)
	@Lob
	protected String content;
	@Column(name = ShortName.REQUEST_STATUS)
	protected BigInteger requestStatus;

	@Column(name = ShortName.CONTENT)
	protected String operationResultContent;
	@Column(name = ShortName.EVENT_CATEGORY)
	protected String operationResultEventCategory;
	@Column(name = ShortName.FROM)
	protected String operationResultFrom;
	@Column(name = ShortName.ORIGINATING_TIMESTAMP)
	protected String operationResultOriginatingTimestamp;
	@Column(name = ShortName.OPERATION_RESULT + ShortName.REQUEST_ID)
	protected String operationResultRequestIdentifier;
	@Column(name = ShortName.RESULT_EXPIRATION_TIMESTAMP)
	protected String operationResultResultExpirationTimestamp;
	@Column(name = ShortName.TO)
	protected String operationResultTo;
	@Column(name = ShortName.RESPONSE_STATUS_CODE)
	protected BigInteger operationResultResponseStatusCode;
	@Column(name = "requestContTy")
	protected String requestContentType;
	@Column(name = "resultContTy")
	protected String resultContentType;
	
	/**
	 * @return the stateTag
	 */
	public BigInteger getStateTag() {
		return stateTag;
	}
	/**
	 * @param stateTag the stateTag to set
	 */
	public void setStateTag(BigInteger stateTag) {
		this.stateTag = stateTag;
	}
	/**
	 * @return the operation
	 */
	public BigInteger getOperation() {
		return operation;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(BigInteger operation) {
		this.operation = operation;
	}
	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	/**
	 * @return the originator
	 */
	public String getOriginator() {
		return originator;
	}
	/**
	 * @param originator the originator to set
	 */
	public void setOriginator(String originator) {
		this.originator = originator;
	}
	/**
	 * @return the requestID
	 */
	public String getRequestID() {
		return requestID;
	}
	/**
	 * @param requestID the requestID to set
	 */
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	/**
	 * @return the metaInformation
	 */
	public MetaInformation getMetaInformation() {
		return metaInformation;
	}
	/**
	 * @param metaInformation the metaInformation to set
	 */
	public void setMetaInformation(MetaInformation metaInformation) {
		this.metaInformation = metaInformation;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the requestStatus
	 */
	public BigInteger getRequestStatus() {
		return requestStatus;
	}
	/**
	 * @param requestStatus the requestStatus to set
	 */
	public void setRequestStatus(BigInteger requestStatus) {
		this.requestStatus = requestStatus;
	}
	/**
	 * @return the operationResultContent
	 */
	public String getOperationResultContent() {
		return operationResultContent;
	}
	/**
	 * @param operationResultContent the operationResultContent to set
	 */
	public void setOperationResultContent(String operationResultContent) {
		this.operationResultContent = operationResultContent;
	}
	/**
	 * @return the operationResultEventCategory
	 */
	public String getOperationResultEventCategory() {
		return operationResultEventCategory;
	}
	/**
	 * @param operationResultEventCategory the operationResultEventCategory to set
	 */
	public void setOperationResultEventCategory(String operationResultEventCategory) {
		this.operationResultEventCategory = operationResultEventCategory;
	}
	/**
	 * @return the operationResultFrom
	 */
	public String getOperationResultFrom() {
		return operationResultFrom;
	}
	/**
	 * @param operationResultFrom the operationResultFrom to set
	 */
	public void setOperationResultFrom(String operationResultFrom) {
		this.operationResultFrom = operationResultFrom;
	}
	/**
	 * @return the operationResultOriginatingTimestamp
	 */
	public String getOperationResultOriginatingTimestamp() {
		return operationResultOriginatingTimestamp;
	}
	/**
	 * @param operationResultOriginatingTimestamp the operationResultOriginatingTimestamp to set
	 */
	public void setOperationResultOriginatingTimestamp(
			String operationResultOriginatingTimestamp) {
		this.operationResultOriginatingTimestamp = operationResultOriginatingTimestamp;
	}
	/**
	 * @return the operationResultRequestIdentifier
	 */
	public String getOperationResultRequestIdentifier() {
		return operationResultRequestIdentifier;
	}
	/**
	 * @param operationResultRequestIdentifier the operationResultRequestIdentifier to set
	 */
	public void setOperationResultRequestIdentifier(
			String operationResultRequestIdentifier) {
		this.operationResultRequestIdentifier = operationResultRequestIdentifier;
	}
	/**
	 * @return the operationResultResultExpirationTimestamp
	 */
	public String getOperationResultResultExpirationTimestamp() {
		return operationResultResultExpirationTimestamp;
	}
	/**
	 * @param operationResultResultExpirationTimestamp the operationResultResultExpirationTimestamp to set
	 */
	public void setOperationResultResultExpirationTimestamp(
			String operationResultResultExpirationTimestamp) {
		this.operationResultResultExpirationTimestamp = operationResultResultExpirationTimestamp;
	}
	/**
	 * @return the operationResultTo
	 */
	public String getOperationResultTo() {
		return operationResultTo;
	}
	/**
	 * @param operationResultTo the operationResultTo to set
	 */
	public void setOperationResultTo(String operationResultTo) {
		this.operationResultTo = operationResultTo;
	}
	/**
	 * @return the operationResultResponseStatusCode
	 */
	public BigInteger getOperationResultResponseStatusCode() {
		return operationResultResponseStatusCode;
	}
	/**
	 * @param operationResultResponseStatusCode the operationResultResponseStatusCode to set
	 */
	public void setOperationResultResponseStatusCode(
			BigInteger operationResultResponseStatusCode) {
		this.operationResultResponseStatusCode = operationResultResponseStatusCode;
	}
	
	/**
	 * @return the cseBase parent
	 */
	public CSEBaseEntity getCseBaseParent() {
		return cseBaseParent;
	}
	
	/**
	 * @param cseBaseParent
	 */
	public void setCseBaseParent(CSEBaseEntity cseBaseParent) {
		this.cseBaseParent = cseBaseParent;
	}
	
	/**
	 * @return the requestContentType
	 */
	public String getRequestContentType() {
		return requestContentType;
	}
	
	/**
	 * @param requestContentType the requestContentType to set
	 */
	public void setRequestContentType(String requestContentType) {
		this.requestContentType = requestContentType;
	}
	
	/**
	 * @return the resultContentType
	 */
	public String getReturnContentType() {
		return resultContentType;
	}
	
	/**
	 * @param resultContentType the resultContentType to set
	 */
	public void setReturnContentType(String resultContentType) {
		this.resultContentType = resultContentType;
	}
	
}
