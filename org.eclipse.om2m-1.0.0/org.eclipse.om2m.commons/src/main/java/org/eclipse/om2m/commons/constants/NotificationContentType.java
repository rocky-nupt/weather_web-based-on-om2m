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
 * Constants for notification content type
 *
 */
public class NotificationContentType {
	
	// private constructor
	private NotificationContentType(){}
	
	/** Big integer constant for WHOLE_RESOURCE */
	public static final BigInteger WHOLE_RESOURCE = BigInteger.valueOf(1);
	/** Big integer constant for MODIFIED_ATTRIBUTES */
	public static final BigInteger MODIFIED_ATTRIBUTES = BigInteger.valueOf(2);
	/** Big integer constant for REFERENCE_ONLY */
	public static final BigInteger REFERENCE_ONLY = BigInteger.valueOf(3);

}
