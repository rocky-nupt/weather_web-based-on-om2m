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
import javax.xml.bind.annotation.XmlType;
/**
 * Abstime oBIX object.
 * @author Francois Aissaoui
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "abstime")
@XmlRootElement
public class Abstime extends Obj{

    @XmlAttribute(name = "val")
    @XmlSchemaType(name = "anyURI")
    protected String val;
    @XmlAttribute(name = "min")
    protected Long min;
    @XmlAttribute(name = "max")
    protected Long max;
    @XmlAttribute(name = "tz")
    protected String tz;
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
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "href")
    protected String href;
    @XmlAttribute(name = "null")
    protected Boolean _null;

    /**
     * Default empty contructor
     */
    public Abstime(){}

	/**
	 * @return the val
	 */
	public String getVal() {
		return val;
	}

	/**
	 * @param val the val to set
	 */
	public void setVal(String val) {
		this.val = val;
	}

	/**
	 * @return the min
	 */
	public Long getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(Long min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public Long getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(Long max) {
		this.max = max;
	}

	/**
	 * @return the tz
	 */
	public String getTz() {
		return tz;
	}

	/**
	 * @param tz the tz to set
	 */
	public void setTz(String tz) {
		this.tz = tz;
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

}
