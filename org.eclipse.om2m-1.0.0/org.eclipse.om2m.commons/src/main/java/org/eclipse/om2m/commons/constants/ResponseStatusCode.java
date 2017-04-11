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
package org.eclipse.om2m.commons.constants;

import java.math.BigInteger;

/**
 * Constants for response status codes
 *
 */
public class ResponseStatusCode {
	
	/** Private constructor */
	private ResponseStatusCode(){}
	
	// Informationnal responses
	public static final BigInteger ACCEPTED = BigInteger.valueOf(1000);
	
	// Successful responses
	public static final BigInteger OK = BigInteger.valueOf(2000);
	public static final BigInteger CREATED = BigInteger.valueOf(2001);
	public static final BigInteger DELETED = BigInteger.valueOf(2002);
	public static final BigInteger UPDATED = BigInteger.valueOf(2004);

	// Originator error responses
	public static final BigInteger BAD_REQUEST = BigInteger.valueOf(4000);
	public static final BigInteger NOT_FOUND = BigInteger.valueOf(4004);
	public static final BigInteger OPERATION_NOT_ALLOWED = BigInteger.valueOf(4005);
	public static final BigInteger REQUEST_TIMEOUT = BigInteger.valueOf(4008);
	public static final BigInteger SUBSCRIPTION_CREATOR_HAS_NO_PRIVILEGE= BigInteger.valueOf(4101);
	public static final BigInteger CONTENTS_UNACCEPTABLE = BigInteger.valueOf(4102);
	public static final BigInteger ACCESS_DENIED = BigInteger.valueOf(4103);
	public static final BigInteger GROUP_REQUEST_IDENTIFIER_EXISTS = BigInteger.valueOf(4104);
	public static final BigInteger CONFLICT = BigInteger.valueOf(4105);

	// Receiver error responses
	public static final BigInteger INTERNAL_SERVER_ERROR = BigInteger.valueOf(5000);
	public static final BigInteger NOT_IMPLEMENTED = BigInteger.valueOf(5001);
	public static final BigInteger TARGET_NOT_REACHABLE = BigInteger.valueOf(5103);
	public static final BigInteger NO_PRIVILEGE = BigInteger.valueOf(5105);
	public static final BigInteger ALREADY_EXISTS = BigInteger.valueOf(5106);
	public static final BigInteger TARGET_NOT_SUBSCRIBABLE = BigInteger.valueOf(5203);
	public static final BigInteger SUBSCRIPTION_VERIFICATION_INITIATION_FAILED = BigInteger.valueOf(5204);
	public static final BigInteger SUBSCRIPTION_HOST_HAS_NO_PRIVILEGE = BigInteger.valueOf(5205);
	public static final BigInteger NON_BLOCKING_REQUEST_NOT_SUPPORTED = BigInteger.valueOf(5206);
	
	public static final BigInteger SERVICE_UNAVAILABLE = BigInteger.valueOf(5042);
	
	// Network service error responses
	public static final BigInteger EXTERNAL_OBJECT_NOT_REACHABLE = BigInteger.valueOf(6003);
	public static final BigInteger EXTERNAL_OBJECT_NOT_FOUND = BigInteger.valueOf(6005);
	public static final BigInteger MAX_NUMBER_OF_MEMBER_EXCEEDED = BigInteger.valueOf(6010);
	public static final BigInteger MEMBER_TYPE_INCONSISTENT = BigInteger.valueOf(6011);
	public static final BigInteger MGMT_SESSION_CANNOT_BE_ESTABLISHED = BigInteger.valueOf(6020);
	public static final BigInteger MGMT_SESSION_ESTABLISHMENT_TIMEOUT = BigInteger.valueOf(6021);
	public static final BigInteger INVALID_CMDTYPE= BigInteger.valueOf(6022);
	public static final BigInteger INVALID_ARGUMENTS = BigInteger.valueOf(6023);
	public static final BigInteger INSUFFICIENT_ARGUMENTS = BigInteger.valueOf(6024);
	public static final BigInteger MGMT_CONVERSION_ERROR = BigInteger.valueOf(6025);
	public static final BigInteger MGMT_CANCELATION_FAILURE = BigInteger.valueOf(6026);
	public static final BigInteger ALREADY_COMPLETED = BigInteger.valueOf(6028);
	public static final BigInteger COMMAND_NOT_CANCELLABLE = BigInteger.valueOf(6029);

}
