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
package org.eclipse.om2m.core.controller;

import org.eclipse.om2m.commons.exceptions.NotImplementedException;
import org.eclipse.om2m.commons.exceptions.OperationNotAllowed;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;

/**
 * Controller for polling channel URI
 *
 */
public class PollingChannelUriController extends Controller {

	@Override
	public ResponsePrimitive doCreate(RequestPrimitive request) {
		throw new OperationNotAllowed("Create on PollingChannelUri is not allowed");
	}

	@Override
	public ResponsePrimitive doRetrieve(RequestPrimitive request) {
		throw new NotImplementedException("Retrieve operation on PollingChannelURI is not implemented");
	}

	@Override
	public ResponsePrimitive doUpdate(RequestPrimitive request) {
		throw new OperationNotAllowed("Update on PollingChannelUri is not allowed");
	}

	@Override
	public ResponsePrimitive doDelete(RequestPrimitive request) {
		throw new OperationNotAllowed("Delete on PollingChannelUri is not allowed");
	}

}
