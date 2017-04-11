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
package org.eclipse.om2m.commons.resource;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Home made discovery result class.
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "discovery")
public class DiscoveryResult {

	@XmlList
	@XmlElement(name="ref")
	List<ResourceRef> references ;
	
	/**
	 * @return the references
	 */
	public List<ResourceRef> getReferences() {
		if(references == null){
			references = new ArrayList<>();
		}
		return references;
	}

	/**
	 * @param references the references to set
	 */
	public void setReferences(List<ResourceRef> references) {
		this.references = references;
	}



	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ResourceRef {
		
		@XmlAttribute(name="ty")
		public BigInteger resourceType;
		
		@XmlValue
		public String uri ;

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
		 * @return the uri
		 */
		public String getUri() {
			return uri;
		}

		/**
		 * @param uri the uri to set
		 */
		public void setUri(String uri) {
			this.uri = uri;
		} 
		
		public void setResourceType(int resourceType){
			this.resourceType = BigInteger.valueOf(resourceType);
		}
	}
	
}
