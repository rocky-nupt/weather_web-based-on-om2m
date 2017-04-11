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
package org.eclipse.om2m.ipe.sample.model;

public class Lamp {

    /** Default Lamps location */
    public final static String LOCATION = "Home";
    /** Toggle */
    public final static String TOGGLE = "toggle";
    /** Default Lamps type */
    public final static String TYPE = "LAMP";
    /** Lamp state */
    private boolean state = false;
    /** Lamp ID */
    private String lampId;
    
    public Lamp(String lampId, boolean initState){
    	this.lampId = lampId;
    	this.state = initState;
    }
    
	/**
	 * @return the state
	 */
	public boolean getState() {
		return state;
	}
	
	/**
	 * @param state the state to set
	 */
	public void setState(boolean state) {
		this.state = state;
	}

	/**
	 * @return the lampId
	 */
	public String getLampId() {
		return lampId;
	}

	/**
	 * @param lampId the lampId to set
	 */
	public void setLampId(String lampId) {
		this.lampId = lampId;
	}
	
}
