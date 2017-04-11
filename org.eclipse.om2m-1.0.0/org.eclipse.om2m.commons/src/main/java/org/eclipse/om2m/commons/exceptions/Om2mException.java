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
package org.eclipse.om2m.commons.exceptions;

import java.math.BigInteger;

import org.eclipse.om2m.commons.constants.ResponseStatusCode;

public class Om2mException extends RuntimeException {

	private static final long serialVersionUID = 3306932500552711777L;
	
	private BigInteger errorStatusCode;
	
	public Om2mException(){
		super();
		this.errorStatusCode = ResponseStatusCode.INTERNAL_SERVER_ERROR;
	}
	
	public Om2mException(BigInteger errorStatusCode){
		this.errorStatusCode = errorStatusCode;
	}

	public Om2mException(String message, BigInteger errorStatusCode){
		super(message);
		this.errorStatusCode = errorStatusCode;
	}
	
	public Om2mException(String message, Throwable cause, BigInteger errorStatusCode){
		super(message, cause);
		this.errorStatusCode = errorStatusCode;
	}

	/**
	 * @return the errorStatusCode
	 */
	public BigInteger getErrorStatusCode() {
		return errorStatusCode;
	}

	/**
	 * @param errorStatusCode the errorStatusCode to set
	 */
	public void setErrorStatusCode(BigInteger errorStatusCode) {
		this.errorStatusCode = errorStatusCode;
	}
	
}
