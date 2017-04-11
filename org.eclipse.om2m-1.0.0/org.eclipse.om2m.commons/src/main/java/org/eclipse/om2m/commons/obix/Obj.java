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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
/***
 * Obj oBIX object. Base and generic object of oBIX.
 * @author Francois Aissaoui
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obj", propOrder = {
    "objGroup"
})
@XmlRootElement
public class Obj {

    @XmlElements({
        @XmlElement(name = "obj", type = Obj.class),
        @XmlElement(name = "bool", type = Bool.class),
        @XmlElement(name = "int", type = Int.class),
        @XmlElement(name = "real", type = Real.class),
        @XmlElement(name = "str", type = Str.class),
        @XmlElement(name = "enum", type = Enum.class),
        @XmlElement(name = "abstime", type = Abstime.class),
        @XmlElement(name = "reltime", type = Reltime.class),
        @XmlElement(name = "date", type = Date.class),
        @XmlElement(name = "time", type = Time.class),
        @XmlElement(name = "uri", type = Uri.class),
        @XmlElement(name = "list", type = org.eclipse.om2m.commons.obix.List.class),
        @XmlElement(name = "ref", type = Ref.class),
        @XmlElement(name = "err", type = Err.class),
        @XmlElement(name = "op", type = Op.class),
        @XmlElement(name = "feed", type = Feed.class)
    })
    protected java.util.List<Object> objGroup;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "href")
    protected String href;
    @XmlTransient
    protected Contract is;
    @XmlAttribute(name = "null")
    protected Boolean _null;

    public Obj(){}

    /**
     * Gets the value of the objGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the objGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObjGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Obj }
     * {@link Bool }
     * {@link Int }
     * {@link Real }
     * {@link Str }
     * {@link Enum }
     * {@link Abstime }
     * {@link Reltime }
     * {@link Date }
     * {@link Time }
     * {@link Uri }
     * {@link org.eclipse.om2m.commons.obix.List }
     * {@link Ref }
     * {@link Err }
     * {@link Op }
     * {@link Feed }
     * 
     * 
     */
    public java.util.List<Object> getObjGroup() {
        if (objGroup == null) {
            objGroup = new ArrayList<Object>();
        }
        return this.objGroup;
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
	 * @return the is
	 */
	public Contract getIs() {
		return is;
	}



	/**
	 * @param is the is to set
	 */
	public void setIs(Contract is) {
		this.is = is;
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
	 * @param objGroup the objGroup to set
	 */
	public void setObjGroup(java.util.List<Object> objGroup) {
		this.objGroup = objGroup;
	}



	public void add(Object obj){
    	getObjGroup().add(obj);
    }
    
    @XmlAttribute(name="is")
    public String getIsString(){
    	if (is != null){
    		return is.get(0).getVal() ; 
    	}
    	return null ;
    }
}
