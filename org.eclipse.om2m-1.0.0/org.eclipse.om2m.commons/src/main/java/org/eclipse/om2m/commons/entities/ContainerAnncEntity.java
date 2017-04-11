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
package org.eclipse.om2m.commons.entities;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.eclipse.om2m.commons.constants.ShortName;

/**
 * Container announced JPA entity
 *
 */
@Entity(name = ShortName.CNT_ANNC)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ContainerAnncEntity extends ResourceEntity {

	// TODO acp ids ???

	@Column(name = ShortName.EXPIRATION_TIME)
	protected String expirationTime;
	@Column(name = ShortName.LINK)
	protected String link;
	
	@Column(name= ShortName.STATETAG)
	protected BigInteger stateTag;
	@Column(name= ShortName.MAX_NR_OF_INSTANCES)
	protected BigInteger maxNrOfInstances;
	@Column(name= ShortName.MAX_BYTE_SIZE)
	protected BigInteger maxByteSize;
	@Column(name= ShortName.MAX_INSTANCE_AGE)
	protected BigInteger maxInstanceAge;
	@Column(name= ShortName.CURRENT_NUMBER_OF_INSTANCES)
	protected BigInteger currentNrOfInstances;
	@Column(name= ShortName.CURRENT_BYTE_SIZE)
	protected BigInteger currentByteSize;
	@Column(name= ShortName.LOCATION_ID)
	protected String locationID;
	@Column(name= ShortName.ONTOLOGY_REF)
	protected String ontologyRef;

	// TODO add link cnt
	// TODO add link cntA
	// TODO add link cin
	// TODO add link cinA
	// TODO add link sub

}
