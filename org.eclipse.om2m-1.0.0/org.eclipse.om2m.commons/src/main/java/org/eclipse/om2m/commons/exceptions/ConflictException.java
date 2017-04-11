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

import org.eclipse.om2m.commons.constants.ResponseStatusCode;

public class ConflictException extends Om2mException {

	private static final long serialVersionUID = 1174293111515837251L;

	public ConflictException(){
		super(ResponseStatusCode.CONFLICT);
	}
	
	public ConflictException(String message){
		super(message, ResponseStatusCode.CONFLICT);
	}
	
	public ConflictException(String message, Throwable cause){
		super(message, cause, ResponseStatusCode.CONFLICT);
	}
}
