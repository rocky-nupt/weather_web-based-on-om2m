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
package org.eclipse.om2m.datamapping.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.datamapping.service.DataMapperService;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
/**
 * Datamapper (JAXB) implementing DataMapper service
 */
public class Mapper implements DataMapperService {

	/** XML Mapper logger */
	private static Log LOGGER = LogFactory.getLog(Mapper.class);
	/** JAXB Context, entry point to the JAXB API */
	private JAXBContext context;
	/** Resource package name used for JAXBContext instantiation */
	private String resourcePackage = "org.eclipse.om2m.commons.resource";
	private String mediaType;

	/**
	 * Private constructor that will create the JAXB context.
	 */
	public Mapper(String mediaType) {
		this.mediaType=mediaType;
		try {
			if(context==null){
				if(mediaType.equals(MimeMediaType.JSON)){
					ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); 
					InputStream iStream = classLoader.getResourceAsStream("json-binding.xml"); 
					Map<String, Object> properties = new HashMap<String, Object>(); 
					properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, iStream);
					context = JAXBContext.newInstance(resourcePackage, classLoader , properties);
				} else {
					context = JAXBContext.newInstance(resourcePackage);
				}
			}
		} catch (JAXBException e) { 
			LOGGER.error("Create JAXBContext error", e);
		}
	}

	/**
	 * Converts a resource Java object into resource XML representation.
	 * 
	 * @param object
	 *            - resource Java object
	 * @return resource XML representation
	 */
	@Override
	public String objToString(Object obj) {
		try {
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			OutputStream outputStream = new ByteArrayOutputStream();
			marshaller.setProperty(MarshallerProperties.MEDIA_TYPE,mediaType);
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
			marshaller.marshal(obj, outputStream);
			return outputStream.toString();
		} catch (JAXBException e) {
			LOGGER.error("JAXB marshalling error!", e);
		}
		return null;
	}

	/**
	 * Converts a resource XML representation data into resource Java object.
	 * 
	 * @param representation
	 *            - resource XML representation
	 * @return resource Java object
	 */
	@Override
	public Object stringToObj(String representation) {
		if(representation.isEmpty()){
			return null;
		}
		StringReader stringReader = new StringReader(representation);
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, mediaType);
			unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);

			return unmarshaller.unmarshal(stringReader);
		} catch (JAXBException e) {
			LOGGER.error("JAXB unmarshalling error!", e);
		}
		return null;
	}

	@Override
	public String getServiceDataType() {
		return mediaType;
	}

}
