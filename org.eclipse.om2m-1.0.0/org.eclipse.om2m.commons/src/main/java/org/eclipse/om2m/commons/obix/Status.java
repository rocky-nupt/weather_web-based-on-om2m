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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
/**
 * Status oBIX enumeration
 * @author Francois Aissaoui
 *
 */
@XmlType(name = "status")
@XmlEnum
public enum Status {

    @XmlEnumValue("disabled")
    DISABLED("disabled"),
    @XmlEnumValue("fault")
    FAULT("fault"),
    @XmlEnumValue("down")
    DOWN("down"),
    @XmlEnumValue("unackedAlarm")
    UNACKED_ALARM("unackedAlarm"),
    @XmlEnumValue("alarm")
    ALARM("alarm"),
    @XmlEnumValue("unacked")
    UNACKED("unacked"),
    @XmlEnumValue("overridden")
    OVERRIDDEN("overridden"),
    @XmlEnumValue("ok")
    OK("ok");
    private final String value;

   

    
    Status(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Status fromValue(String v) {
        for (Status c: Status.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
