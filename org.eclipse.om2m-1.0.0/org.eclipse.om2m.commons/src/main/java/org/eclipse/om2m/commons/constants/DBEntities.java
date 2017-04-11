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
package org.eclipse.om2m.commons.constants;

import org.eclipse.om2m.commons.entities.AccessControlContextEntity;
import org.eclipse.om2m.commons.entities.AccessControlOriginatorEntity;
import org.eclipse.om2m.commons.entities.AccessControlRuleEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.entities.ContentInstanceEntity;
import org.eclipse.om2m.commons.entities.LabelEntity;
import org.eclipse.om2m.commons.entities.PollingChannelEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.entities.UriMapperEntity;
import org.eclipse.om2m.commons.resource.AccessControlPolicy;

/**
 * Class referencing all persisted entities names and their attributes.
 * 
 */
public class DBEntities {
	
	// private constructor
	private DBEntities() {
	};

	// ENTITIES NAMES
	/** Name of the table for hierarchical uris */
	public static final String HIERARCHICAL_URI = "huri";
	/** Name for Non hierarchical uri */
	public static final String NONHIERARCHICAL_URI = "nhuri";
	
	/** Name used for the persisted {@link AccessControlContextEntity} entity */
	public static final String ACCESSCONTROLCONTEXT_ENTITY = "ACC";

	/** Name used for the persisted {@link AccessControlRuleEntity} entity */
	public static final String ACCESSCONTROLRULE_ENTITY = "ACR";

	/** Name used for the persisted {@link AccessControlOriginatorEntity} entity */
	public static final String ACCESSCONTROLORIGINATOR_ENTITY = "ACO";

	/** Name used for the persisted {@link AccessControlPolicy} entity */
	public static final String ACCESSCONTROLPOLICY_ENTITY = "ACP";

	/** Name used for the persisted {@link CSEBaseEntity} entity */
	public static final String CSEBASE_ENTITY = "CSEB";
	
	/** Name used for the persisted {@link AeEntity} entity */
	public static final String AE_ENTITY = "AE";
			
	/** Name used for the persisted {@link ContainerEntity} entity */
	public static final String CONTAINER_ENTITY = "CNT";

	/** Name used for the persisted {@link ContentInstanceEntity} entity */
	public static final String CONTENTINSTANCE_ENTITY = "CIN";

	/** Name used for the persisted {@link SubscriptionEntity} entity */
	public static final String SUBSCRIPTION_ENTITY = "SUB";
	
	/** Name used for the persisted {@link RemoteCSEEntity} entity */
	public static final String REMOTECSE_ENTITY = "CSR";

	/** Name used for the persisted {@link UriMapperEntity} entity */
	public static final String URI_MAPPER_ENTITY = "URI_MAPPER";
	
	/** Name used for the persisted {@link LabelEntity} entity */
	public static final String LABEL_ENTITY = "LBL";
	
	/** Name used for the persisted {@link PollingChannelEntity} entity */
	public static final String POLLING_CHANNEL_ENTITY = "PCH";

	public static final String REQUEST_ENTITY = "REQ";
	
	public static final String NODE_ENTITY = "NODE";
	
	
	/** Name of the GROUP entity */
	public static final String GROUP_ENTITY = "GRP";
	// JOIN-LINKS TABLES NAMES AND COLUMNS
	// AccessControlPolicy - AccessControlRules - self privileges
	/**
	 * Name of the join table between AccessControlPolicy and its self
	 * privileges
	 */
	public static final String ACPACR_SEFPRIVILEGES = "ACP_ACR_PVS";
	/** Name of the AccessControlPolicy Resource Id in the join table */
	public static final String ACPID_COLUMN = "ACP_RI";
	/** Name of the AccessControlRule Id in the join table */
	public static final String ACCESSCONTROLRULE_ID = "ACR_ID";
	/** Name of the AccessControlRule Id in the join table */
	public static final String ACRID_COLUMN = "ACR_ID";
	
	// AccessControlPolicy - AccessControlRules - Privileges
	/** Name of the join table between AccessControlPolicy and its privileges */
	public static final String ACPACR_PRIVILEGES = "ACP_ACR_PV";

	// CSEBase - AccessControlPolicies
	/** Name of the join table between CSEBase and its own AccessControlPolicy */
	public static final String CSEBACP_JOIN = "CSEB_ACP";
	/** Name of the join table between CSEBase and its child AccessControlPolicy */
	public static final String CSEBCHILDACP_JOIN = "CSEB_CHILDACP";
	/** CSE base id in join table */
	public static final String CSEB_JOIN_ID = "CSEB_ID";
	/** ACP id in join table */
	public static final String ACP_JOIN_ID = "ACP_ID";
	
	// CSB - AE
	/** Name of the join table between CSEBase and ApplicationEntities */
	public static final String CSEBAE_JOIN = "CSEB_AE";

	// AE - ACP
	/** Name of the join table between AE and its AccessControlPolicies */
	public static final String AEACP_JOIN = "AE_ACP";
	/** Id of AE in join table */
	public static final String AE_JOINID = "AE_ID";
	
	// AE - ch ACP
	/** Name of the join table between AE and child ACP */
	public static final String AEACPCHILD_JOIN = "AE_CHACP";
	
	// CNT - CNT
	/** Name of the join table between ContainerEntity and its child ContainerEntities */
	public static final String CNTCNTCHILD_JOIN = "CNT_CHCNT";
	/** Name of the Container ID */
	public static final String CNT_JOIN_ID = "CNT_ID";
	/** Name of the Child Container ID */
	public static final String CNTCH_JOIN_ID = "CNTCH_ID";

	// AE - CNT
	/** Name of the join table between ApplicationEntities and its child ContainerEntities */
	public static final String AECNTCHILD_JOIN = "AE_CNT_JOIN";

	// CSEB - CNT
	/** Name of the join table between CSEBaseEntity and its child ContainerEntities */
	public static final String CSEB_CNT_JOIN = "CSEB_CNT_JOIN";
	/** Name of the join table between CNT and ACP */
	public static final String CNTACP_JOIN = "CNT_ACP_JOIN";
	/** Name of the join table between CNT and child CIN */
	public static final String CNTCINCHILD_JOIN = "CNT_CIN_JOIN";
	/** ID of CIN in the join table */
	public static final String CINCH_JOIN_ID = "CINCH_ID";

	// SUB - ACP
	/** Name of the join table between Subscription entity and its AccessControl policies */
	public static final String SUBACP_JOIN = "SUB_ACP_JOIN";
	/** name of the ID for subscription entity in the join tables */
	public static final String SUB_JOIN_ID = "SUB_ID";

	/** name of the join table between CSEBase and remoteCSE */
	public static final String CSBCSR_JOIN = "CSB_CSR";
	/** ID of the CSR in join table */
	public static final String CSR_JOIN_ID = "CSR_ID";
	
	// CSR - ACP
	/** Name of the join table between CSR and ACP */
	public static final String CSRACP_JOIN = "CSR_ACP_JOIN";
	
	/** Name of the join table between GRP & ACP */
	public static final String GRPACP_JOIN = "GRP_ACP_JOIN";
	/** ID of GRP in join table */
	public static final String GRP_JOIN_ID = "grp_id";

	/** Name of the join table between CSEB & GRP */
	public static final String CSEB_GRP_JOIN = "CSB_GRP_JOIN";
	/** Name of the join table between CSR & GRP */
	public static final String CSR_GRP_JOIN = "CSR_GRP_JOIN";
	/** Name of the join table between AE & ch GRP */
	public static final String AEGRPCHILD_JOIN = "AE_CHGRP_JOIN";
	/** Name of the join table between CSR & AE */
	public static final String CSRAECHILD_JOIN = "CSR_CHAE_JOIN";
	/** Name of the join table between CSR & ch CNT */
	public static final String CSRCNTCHILD_JOIN = "CSR_CHCNT_JOIN";
	/** Name of the join table between CSR & ch GRP */
	public static final String CSRGRPCHILD_JOIN = "CSR_CHGRP_JOIN";
	/** Name of the join table between CSR & ch ACP */
	public static final String CSRACPCHILD_JOIN = "CSR_ACPCH_JOIN";

	// SUB - AE
	/** Name of the join table between AE & SUB */
	public static final String AESUB_JOIN = "AE_SUB_JOIN";
	/** Name of the join table between CNT & SUB */
	public static final String CNTSUB_JOIN = "CNT_SUB_JOIN";
	/** Name of the join table between CSR & SUB */
	public static final String CSRSUB_JOIN = "CSR_SUB_JOIN";
	/** Name of the join table between CSB & SUB */
	public static final String CSBSUB_JOIN = "CSB_SUB_JOIN";
	/** Name of the join table between GRP & SUB */
	public static final String GRPSUB_JOIN = "GRP_SUB_JOIN";
	/** Name of the join table between ACP & SUB */
	public static final String ACPSUB_JOIN = "ACP_SUB_JOIN";
	/** Name of the join table between SCH & SUB */
	public static final String SCHSUB_JOIN = "SCH_SUB_JOIN";
	/** Name of the join table between AE & PCH */
	public static final String AEPCH_JOIN = "AE_PCH_JOIN";
	/** Name of the join table between CSR & PCH */
	public static final String CSRPCH_JOIN = "CSR_PCH_JOIN";
	/** Name of the join table between ACP & PCH */
	public static final String ACPPCH_JOIN = "ACP_PCH_JOIN";
	/** ID of SCH in join table */
	public static final String SCH_JOIN_ID = "SCH_JOIN_ID";
	/** ID of PCH in join table */
	public static final String PCH_JOIN_ID = "PCH_JOIN_ID";
	
	// CSEB - REQ
	/** Name of the join table between CSEB & REQ*/
	public static final String CSEB_REQ_JOIN = "CSEB_REQ_JOIN";
	/** ID of the REQ in join table */
	public static final String REQ_JOIN_ID = "REQ_JOIN_ID";
	
	// NODE - ACP
	public static final String ACPNOD_JOIN = "NOD_ACP_JOIN";
	public static final String NOD_JOIN_ID = "NOD_JOIN_ID";
	public static final String CSBNOD_CH_JOIN = "CSB_NOD_CH_JOIN";
	public static final String CSRNOD_CH_JOIN = "CSR_NOD_CH_JOIN";
	public static final String NODSUB_JOIN = "NOD_SUB_JOIN";
	
	// MGMT OBJ
	public static final String ANISUB_JOIN = "ANI_SUB_JOIN";
	public static final String ANIACP_JOIN = "ANI_ACP_JOIN";
	public static final String ANI_JOIN_ID = "ANI_JOIN_ID";
	public static final String ANINOD_JOIN = "ANI_NOD_JOIN";
	public static final String ANDISUB_JOIN = "ANDI_SUB_JOIN";
	public static final String ANDI_JOIN_ID = "ANDI_JOIN_ID";
	public static final String ANDINOD_JOIN = "ANDI_NOD_JOIN";
	public static final String ANDIACP_JOIN = "ANDI_ACP_JOIN";
	
}

