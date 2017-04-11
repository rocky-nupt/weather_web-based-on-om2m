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
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Contract oBIX object.
 * 
 * @author Francois
 *
 */
public class Contract {
	
	/**
	 * Default constructor.
	 */
	public Contract() {	}

	/**
	 * Construct from a space separated list.
	 */
	public Contract(String list) {
		this(parse(list));
		this.string = list;
	}

	/**
	 * Construct from string list.
	 */
	public Contract(String[] list) {
		this.list = new Uri[list.length];
		for (int i = 0; i < list.length; ++i) {
			Uri uri = new Uri();
			uri.setVal(list[i]);
			this.list[i] = uri;
		}
	}

	/**
	 * Construct from uri list.
	 */
	public Contract(Uri[] list) {
		if (list != null) {
			this.list = Arrays.copyOf(list, list.length);
		}
	}

	/**
	 * Parse a space separated list of uris.
	 */
	public static Uri[] parse(String list) {
		StringTokenizer st = new StringTokenizer(list, " ");
		ArrayList<Uri> acc = new ArrayList<Uri>();
		while (st.hasMoreTokens()) {
			acc.add(new Uri(st.nextToken()));
		}
		return (Uri[]) acc.toArray(new Uri[acc.size()]);
	}

	/**
	 * The primary Uri is always the first, and is supposed to represent a
	 * contract that merges the entire rest of the list into one fetchable uri.
	 */
	public Uri primary() {
		return list[0];
	}

	/**
	 * Return the length of the uri list.
	 */
	public int size() {
		return list.length;
	}

	/**
	 * Get the uri at the specified index.
	 */
	public Uri get(int index) {
		return list[index];
	}

	/**
	 * Get unsafe reference to list uris.
	 */
	public Uri[] list() {
		return list;
	}

	/**
	 * Return true if this contract list contains the specified uri.
	 */
	public boolean contains(Uri uri) {
		for (int i = 0; i < list.length; ++i) {
			if (list[i].equals(uri)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If this a contract with a size of one for "obix:Obj".
	 */
	public boolean containsOnlyObj() {
		return list.length == 1 && list[0].getVal().equals("obix:obj");
	}

	/**
	 * HashCode of Contract
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(list);
		result = prime * result + ((string == null) ? 0 : string.hashCode());
		return result;
	}

	/**
	 * Equality based on the list of uris.
	 */
	public boolean equals(Object that) {
		if (that instanceof Contract) {
			return toString().equals(that.toString());
		}
		return false;
	}

	/**
	 * Encode to a Java expression.
	 */
	public String encodeJava() {
		// might want to escape funny chars
		return "new Contract(\"" + toString() + "\")";
	}

	/**
	 * Return space separated list of uris.
	 */
	public String toString() {
		if (string == null) {
			StringBuffer s = new StringBuffer();
			for (int i = 0; i < list.length; ++i) {
				if (i > 0) {
					s.append(' ');
				}
				s.append(list[i].getVal());
			}
			string = s.toString();
		}
		return string;
	}

	static final Contract OBJ = new Contract("obix:obj");

	Uri[] list;
	String string;

}
