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
package org.eclipse.om2m.webapp.resourcesbrowser.xml;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *  HTTP Servlet forwarding to the AJAX web interface index page.
 */
public class WelcomeServlet extends HttpServlet {
    /** Serial Version UID */
    private static final long serialVersionUID = 1L;
    @Override
    protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
    	String cseBaseContext = (System.getProperty("org.eclipse.om2m.cseBaseContext", "/om2m").equals("/") ?
    			"/~":System.getProperty("org.eclipse.om2m.cseBaseContext", "/om2m") + "/");
    	httpServletResponse.sendRedirect(Activator.globalContext+Activator.uiContext+Activator.sep+"welcome/index.html?context="+System.getProperty("org.eclipse.om2m.globalContext","")+cseBaseContext+"&"+"cseId="+System.getProperty("org.eclipse.om2m.cseBaseId", "in-cse"));
    }
}
