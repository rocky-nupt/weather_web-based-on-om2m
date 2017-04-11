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
package org.eclipse.om2m.commons.obix;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
/**
 * Op oBIX object representinbg an operation.
 * @author Francois
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "op")
@XmlRootElement
public class Op extends Obj{

	@XmlTransient
    protected Contract in;
	@XmlTransient
    protected Contract out;
    @XmlAttribute(name = "href")
    protected String href;
    @XmlAttribute(name = "null")
    protected Boolean _null;
    @XmlAttribute(name = "displayName")
    protected String displayName;
    @XmlAttribute(name = "display")
    protected String display;
    @XmlAttribute(name = "icon")
    @XmlSchemaType(name = "anyURI")
    protected String icon;
    @XmlAttribute(name = "precision")
    protected Integer precision;
    @XmlAttribute(name = "status")
    protected Status status;
    @XmlAttribute(name = "unit")
    protected String unit;
    @XmlAttribute(name = "writable")
    protected Boolean writable;
    
    public Op(){}

    /**
	 * @return the in
	 */
	public Contract getIn() {
		return in;
	}

	/**
	 * @param in the in to set
	 */
	public void setIn(Contract in) {
		this.in = in;
	}

	/**
	 * @return the out
	 */
	public Contract getOut() {
		return out;
	}

	/**
	 * @param out the out to set
	 */
	public void setOut(Contract out) {
		this.out = out;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @param href the href to set
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * @return the _null
	 */
	public Boolean get_null() {
		return _null;
	}

	/**
	 * @param _null the _null to set
	 */
	public void set_null(Boolean _null) {
		this._null = _null;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the display
	 */
	public String getDisplay() {
		return display;
	}

	/**
	 * @param display the display to set
	 */
	public void setDisplay(String display) {
		this.display = display;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return the precision
	 */
	public Integer getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the writable
	 */
	public Boolean getWritable() {
		return writable;
	}

	/**
	 * @param writable the writable to set
	 */
	public void setWritable(Boolean writable) {
		this.writable = writable;
	}

	@XmlAttribute(name="in")
    public String getInString(){
    	if (in != null){
    		return in.get(0).getHref(); 
    	}
    	return null ; 
    }
    
    @XmlAttribute(name="out")
    public String getOutString(){
    	if(out != null){
    		return out.get(0).getHref();
    	}
    	return null ;
    }
    
    public void setHref(Uri uri){
    	this.href = uri.getVal();
    }

}
