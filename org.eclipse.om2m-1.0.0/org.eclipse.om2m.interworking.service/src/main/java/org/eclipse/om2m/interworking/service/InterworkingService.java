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
package org.eclipse.om2m.interworking.service;

import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
/**
 * Interworking Proxy Entity (IPE) interface.
 */
public interface InterworkingService {
    /**
     * Executes a resource via a specific Interworking Proxy Entity.
     * @param RequestPrimitive - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponsePrimitive doExecute(RequestPrimitive RequestPrimitive);

    /**
     * Returns the ApocPath id required for the {@link InterworkingProxyController} to dispatch
     * a received request to the correct specific Interworking Proxy Entity (IPE).
     * @return Application point of contact
     */
    public String getAPOCPath();
}
