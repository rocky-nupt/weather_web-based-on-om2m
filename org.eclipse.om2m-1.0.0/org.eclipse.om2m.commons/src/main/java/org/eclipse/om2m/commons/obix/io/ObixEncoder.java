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
package org.eclipse.om2m.commons.obix.io;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.obix.Obj;

/**
 * Obix encoder to convert an oBIX object to its string representation
 * 
 * @author Francois Aissaoui
 *
 */
public class ObixEncoder {

	private static final Log LOG = LogFactory.getLog(ObixEncoder.class);

	/**
	 * Method not allowed.
	 */
	private ObixEncoder(){
		// Not allowed
	}
	
	/**
	 * Convert an oBIX java object to its string representation
	 * 
	 * @param obj
	 *            The object to convert
	 * @return the String representation of the object
	 */
	public static String toString(Obj obj) {
		try {
			Marshaller marshaller = ObixMapper.getInstance().getJAXBContext()
					.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			OutputStream outputStream = new ByteArrayOutputStream();
			marshaller.marshal(obj, outputStream);
			return outputStream.toString();
		} catch (JAXBException e) {
			LOG.error("Error in encoding oBIX object", e);
		}
		return null;
	}

}
