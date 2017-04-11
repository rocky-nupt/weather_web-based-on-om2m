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

/**
 * Regroup the short names for resource names & attributes etc.
 *
 */
public class ShortName {

	/** Private constructor */
	private ShortName() {
	}

	// Resource and specialization type short names
	/** Short name for AccessControl Policy resource */
	public static final String ACP = "acp";
	/** Short name for AccessControlPolicyAnnc resource */
	public static final String ACPA = "acpA";
	/** Short name for ApplicationEntity resource */
	public static final String AE = "ae";
	/** Short name for ApplicationEntityAnnc resource */
	public static final String AEA = "aeA";
	/** Short name for Container resource */
	public static final String CNT = "cnt";
	/** Short name for ContainerAnnc resource */
	public static final String CNTA = "cntA";
	/** Short name for Content Instance resource */
	public static final String CIN = "cin";
	/** Short name for ContentInstanceAnnc resource */
	public static final String CINA = "cinA";
	/** Short name for CseBase resource */
	public static final String CSE_BASE = "cb";
	/** Short name for Delivery resource */
	public static final String DELIVERY = "dlv";
	/** Short name for EventConfig resource */
	public static final String EVENTCONFIG = "evcg";
	/** Short name for ExecInstance resource */
	public static final String EXECINSTANCE = "exin";
	/** Short name for FanOutPoint resource */
	public static final String FANOUTPOINT = "fopt";
	/** Short name for Group resource */
	public static final String GROUP = "grp";
	/** Short name for GroupAnnc resource */
	public static final String GROUPA = "grpA";
	/** Short name for LocationPolicy resource */
	public static final String LOCATIONPOLICY = "lcp";
	/** Short name for LocationPolicyAnnc resource */
	public static final String LOCATIONPOLICYA = "lcpA";
	/** Short name for M2mServiceSubscriptionProfile resource */
	public static final String MSSP = "mssp";
	/** Short name for MgmtCmd resource */
	public static final String MGC = "mgc";
	/** Short name for MgmtObj resource */
	public static final String MGO = "mgo";
	/** Short name for MgmtObjAnnc resource */
	public static final String MGOA = "mgoA";
	/** Short name for Node resource */
	public static final String NODE = "nod";
	/** Short name for NodeAnnc resource */
	public static final String NODE_ANNC = "nodA";
	/** Short name for PollingChannel resource */
	public static final String PCH = "pch";
	/** Short name for PollingChannelUri resource */
	public static final String POLLING_CHANNEL_URI = "pcu";
	/** Short name for RemoteCse resource */
	public static final String REMOTE_CSE = "csr";
	/** Short name for RemoteCseAnnc resource */
	public static final String CSRA = "csrA";
	/** Short name for Request resource */
	public static final String REQ = "req";
	/** Short name for Schedule resource */
	public static final String SCHEDULE = "sch";
	/** Short name for ScheduleAnnc resource */
	public static final String SCHA = "schA";
	/** Short name for Service subscribedAppRule resource */
	public static final String ASAR = "asar";
	/** Short name for ServiceSubscribedNode resource */
	public static final String SVSN = "svsn";
	/** Short name for StatsCollect resource */
	public static final String STCL = "stcl";
	/** Short name for StatsConfig resource */
	public static final String STCG = "stcg";
	/** Short name for Subscription resource */
	public static final String SUB = "sub";
	/** Short name for Firmware resource */
	public static final String FWR = "fwr";
	/** Short name for Software resource */
	public static final String SWR = "swr";
	/** Short name for Memory resource */
	public static final String MEM = "mem";
	/** Short name for AreaNwkInfo resource */
	public static final String ANI = "ani";
	/** Short name for AreaNwkDeviceInfo resource */
	public static final String ANDI = "andi";
	/** Short name for DeviceInfo resource */
	public static final String DVI = "dvi";
	/** Short name for DeviceCapability resource */
	public static final String DVC = "dvc";
	/** Short name for Reboot resource */
	public static final String RBT = "rbt";
	/** Short name for EventLog resource */
	public static final String EVL = "evl";
	/** Short name for CmdhPolicy resource */
	public static final String CMP = "cmp";
	/** Short name for ActiveCmdhPolicy resource */
	public static final String ACMP = "acmp";
	/** Short name for CmdhDefaults resource */
	public static final String CMDF = "cmdf";
	/** Short name for CmdhDefEcValue resource */
	public static final String CMDV = "cmdv";
	/** Short name for CmdhEcDefParamValues resource */
	public static final String CMPV = "cmpv";
	/** Short name for CmdhLimits resource */
	public static final String CML = "cml";
	/** Short name for CmdhNetworksAccessRules resource */
	public static final String CMNR = "cmnr";
	/** Short name for CmdhNwAccessRule resource */
	public static final String CMWR = "cmwr";
	/** Short name for CmdhBuffer resource */
	public static final String CMBF = "cmbf";

	// Resource attributes short names
	/** Short name for Resource ID attribute */
	public static final String RESOURCE_ID = "ri";
	/** Short name for CreationTime attribute */	
	public static final String RESOURCE_TYPE = "ty";
	/** Short name for CreationTime attribute */
	public static final String CREATION_TIME = "ct";
	/** Short name for LastModifiedTime attribute */
	public static final String LAST_MODIFIED_TIME = "lt";
	/** Short name for ParentID attribute */
	public static final String PARENT_ID = "pi";
	/** Short name for ResourceName attribute */
	public static final String RESOURCE_NAME = "rn";
	/** Short name for Labels attribute */
	public static final String LABELS = "lbl";
	/** Short name for ExpirationTime attribute */
	public static final String EXPIRATION_TIME = "et";
	/** Short name for AnnounceTo attribute */
	public static final String ANNOUNCE_TO = "at";
	/** Short name for AnnouncedAttribute attribute */
	public static final String ANNOUNCED_ATTRIBUTE = "aa";
	/** Short name for PointOfAccess attribute */
	public static final String POA = "poa";
	/** Short name for NodeLink attribute */
	public static final String NODE_LINK = "nl";
	/** Short name for NodeLink attribute */
	public static final String ACP_IDS = "acpi";
	/** Short name for Child Resource */
	public static final String CHILD_RESOURCE = "ch";
	
	// Attributes for CSEBase Entity
	/** Short name for SupportedResourceTypes attribute */
	public static final String SRT = "srt";
	/** Short name for CSE-ID attribute */
	public static final String CSE_ID = "csi";
	/** Short name for CseType attribute */
	public static final String CSE_TYPE = "cst";
	
	// Specific attributes for AccessControlPolicy Entity
	/** Short name for privileges attribute */
	public static final String PRIVILEGES = "pv";
	/** Short name for privileges attribute */
	public static final String SELF_PRIVILEGES = "pvs";
	/** Short name for acr attribute */
	public static final String ACR = "acr";
	/** Short name for access control originators attribute */
	public static final String ACOR = "acor";
	/** Short name for access control operations attribute */
	public static final String ACOP = "acop";
	/** Short name for access control contexts attribute */
	public static final String ACCO = "acco";
	/** Short name for access control window attribute */
	public static final String ACTW = "actw";
	/** Short name for access control ip adresses attribute */
	public static final String ACIP = "acip";
	/** Short name for ipv4 address attribute */
	public static final String IPV4 = "ipv4";
	/** Short name for ipv6 address attribute */
	public static final String IPV6 = "ipv6";
	/** Short name for access control location region attribute */
	public static final String ACLR = "aclr";
	/** Short name for country code attribute */
	public static final String ACCC = "accc";
	/** Short name for circ region attribute */
	public static final String ACCR = "accr";
	
	
	
	// Attributes for Application Entity
	/** Short name for App Name Attribute */
	public static final String APP_NAME = "apn";
	/** Short name for App-ID Attribute */
	public static final String APP_ID = "api";
	/** Short name for AE-ID Attribute */
	public static final String AE_ID = "aei";
	/** Short name for Ontology Reference Attribute */
	public static final String ONTOLOGY_REF = "or";
	
	// Attributes for Container Entity
	/** Short name for Creator attribute */
	public static final String CREATOR = "cr";
	/** Short name for StateTag attribute */
	public static final String STATETAG = "st";
	/** Short name for Max Number Of Instances attribute */
	public static final String MAX_NR_OF_INSTANCES = "mni";
	/** Short name for Max Byte Size attribute */
	public static final String MAX_BYTE_SIZE = "mbs";
	/** Short name for Max Instance Age attribute */
	public static final String MAX_INSTANCE_AGE = "mia";
	/** Short name for Current Byte Size attribute */
	public static final String CURRENT_BYTE_SIZE = "cbs";
	/** Short name for location ID attribute */	
	public static final String LOCATION_ID = "li";
	/** Short name for Latest attribute */	
	public static final String LATEST = "la";
	/** Short name for Oldest attribute */	
	public static final String OLDEST = "ol";
	/** Short name for current number of instances attribute */	
	public static final String CURRENT_NUMBER_OF_INSTANCES = "cni";
	
	
	// Attributes for Content Instance
	/** Short name for ContentSize attribute */
	public static final String CONTENT_SIZE = "cs";
	/** Short name for ContentInfo attribute */
	public static final String CONTENT_INFO = "cnf";
	/** Short name for Content attribute */
	public static final String CONTENT = "con";
	/** Short Name fot expiration counter*/
	public static final String EXPIRATION_COUNTER = "exc";
	
	public static final String NOTIFICATION_URI = "nu";
	public static final String NOTIFICATION_FORWARDING_URI = "nfu";
	//TODO see if relevant
	public static final String TYPE = "ty";
	
	// specific attributes for Group resource
	/** Short Name for the member type attribute */
	public static final String MEMBER_TYPE = "mt";
	/** Short Name for the current number of members attribute */
	public static final String CURRENT_NUM_MEMBERS = "cnm";
	/** Short Name for the member type attribute */
	public static final String MAX_NUM_MEMBERS = "mnm";
	/** Short Name for the memberID type attribute */
	public static final String MEMBER_ID = "mid";
	/** Short Name for the member acp id attribute */
	public static final String MEMBER_ACP_ID = "macp";
	/** Short Name for the member type validated attribute */
	public static final String MEMBER_TYPE_VALIDATED = "mtv";
	/** Short Name for the consistency strategy attribute */
	public static final String CONSISTENCY_STRATEGY = "csy";
	/** Short Name for the consistency strategy attribute */
	public static final String GROUP_NAME = "gn";
	
	// Specific attributes for remoteCSE resource
	/** Short Name for the M2M-EXT-ID attribute */
	public static final String M2M_EXT_ID = "mei";
	public static final String TRIGGER_RECIPIENT_ID = "tri";
	public static final String REQUEST_REACHABILITY = "rr";
	public static final String REMOTE_CSE_CSEBASE = "cb";
	
	// Specific attributes for Subscription resource
	/** Short Name for the Event Notification Criteria attribute */
	public static final String EVENT_NOTIFICATION_CRITERIA = "enc";
	/** Short name for group id */
	public static final String GROUP_ID = "gpi";
	/** Short name for the batch notify attribute */
	public static final String BATCH_NOTIFY = "bn";
	/** Short name for rate limit */
	public static final String RATE_LIMIT = "rl";
	/** Short name for pre subscription notify */
	public static final String PRE_SUBSCRIPTION_NOTIFY = "psn";
	/** Short name for pending notification */
	public static final String PENDING_NOTIFICATION = "pn";
	/** Short name for notification storage priority */
	public static final String NOTIFICATION_STORAGE_PRIORITY = "nsp";
	/** Short name for latest notify */
	public static final String LATEST_NOTIFY = "ln";
	/** Short name for notification content type */
	public static final String NOTIFICATION_CONTENT_TYPE = "nct";
	/** Short name for Notification event cat */
	public static final String NOTIFICATION_EVENT_CAT = "nec";
	/** Short name for subscriber uri */
	public static final String SUBSCRIBER_URI = "su";
	
	// attributes for rate limit
	/** short name for max number of notify */
	public static final String MAX_NR_OF_NOTIFY = "mnn";
	/** short name for time window */
	public static final String TIME_WINDOW = "tww";
	
	// attributes for Schedule
	/** short name for schedule element */
	public static final String SCHEDULE_ELEMENT = "se";
	/** short name for schedule entry */
	public static final String SCHEDULE_ENTRY = "sce";
	
	// attributes for announced resource
	public static final String LINK = "LINK"; // TODO to update when available
	public static final String AE_ANNC = ShortName.AE + "A";
	public static final String CNT_ANNC = ShortName.CNT + "A";

	// attributes for request resource
	public static final String OPERATION = "op";
	public static final String TO = "to";
	public static final String FROM = "fr";
	public static final String NAME = "nm";
	public static final String REQUEST_CONTENT = "pc";
	public static final String ORIGINATING_TIMESTAMP = "ot";
	public static final String REQUEST_EXPIRATION_TIMESTAMP = "rqet";
	public static final String RESULT_EXPIRATION_TIMESTAMP = "rset";
	public static final String OPERATION_EXECUTION_TIME = "oet";
	public static final String RESPONSE_TYPE = "rt";
	public static final String RESULT_PERSISTENCE = "rp";
	public static final String RESULT_CONTENT = "rcn";
	public static final String EVENT_CATEGORY = "ec";
	public static final String DELIVERY_AGGREGATION = "da";
	public static final String GROUP_REQUEST_IDENTIFIER = "gid";
	public static final String FILTER_CRITERIA = "fc";
	public static final String CREATED_BEFORE = "crb";
	public static final String CREATED_AFTER = "cra";
	public static final String MODIFIED_SINCE = "ms";
	public static final String UNMODIFIED_SINCE = "us";
	public static final String STATETAG_SMALLER = "sts";
	public static final String STATETAG_BIGGER = "stb";
	public static final String EXPIRE_BEFORE = "exb";
	public static final String EXPIRE_AFTER = "exa";
	public static final String FILTER_RESOURCETYPE = "rty";
	public static final String CONTENT_TYPE = "cty";
	public static final String LIMIT = "lim";
	public static final String ATTRIBUTE = "atr";
	public static final String FILTER_USAGE = "fu";
	public static final String DISCOVERY_RESULT_TYPE = "drt";
	public static final String RESPONSE_STATUS_CODE = "rsc";
	public static final String ORIGINATOR = "og";
	public static final String META_INFORMATION = "mi";
	public static final String REQUEST_STATUS = "rs";
	public static final String OPERATION_RESULT = "ol";
	public static final String REQUEST_OPERATION = "opn";
	public static final String REQUEST_ID = "rid";
	public static final String TARGET = "tg";
	public static final String EVENT_CAT_TYPE = "ect";
	public static final String EVENT_CAT_NO = "ecn";
	public static final String PRIMITIVE_CONTENT = "pc";
	
	// attributes for filter criteria
	public static final String SIZE_ABOVE = "sza";
	public static final String SIZE_BELOW = "szb";
	
	// Attributes for Event Notification Criteria
	public static final String OPERATION_MONITOR = "om";
	public static final String RESOURCE_STATUS = "rss";
	
	// Attributes for Batch Notify
	public static final String NUMBER = "num";
	public static final String DURATION = "dur";
	
	// Attributes for Notification
	public static final String NOTIFICATION = "sgn";
	public static final String NOTIFICATION_EVENT = "nev";
	public static final String VERIFICATION_REQUEST = "vrq";
	public static final String SUBSCRIPTION_DELETION = "sud";
	public static final String SUBSCRIPTION_REFERENCE = "sur";
	
	// Attribute for Notification Event
	public static final String REPRESENTATION = "rep";
	
	// Attributes for OperationMonitor
	public static final String OM_OPERATION = "opr";
	public static final String OM_ORIGINATOR = "org";
	
	public static final String AGGREGATED_RESPONSE = "agr";
	public static final String RESPONSE_PRIMITIVE = "rsp";
	public static final String REQUEST_PRIMITIVE = "rqp";
	public static final String REQUEST_IDENTIFIER = "rqi";
	
	// Attributes for Node
	public static final String NODE_ID = "ni";
	public static final String HOSTED_CSE_LINK = "hcl";
	
	// Short names for mgmt objects generic attributes
	public static final String DESCRIPTION = "des";
	public static final String MGMT_DEF = "mgd";
	public static final String OBJ_IDS = "obis";
	public static final String OBJ_PATHS = "obps";
	
	// Short names for mgmt objects specialization
	public static final String MEMORY = "mem";
	public static final String BATTERY = "bat";
	public static final String FIRMWARE = "fwr";
	public static final String SOFTWARE = "swr";
	public static final String DEVICE_INFO = "dvi";
	public static final String DEVICE_CAPABILITY = "dvc";
	public static final String REBOOT = "rbo";
	public static final String EVENT_LOG = "evl";
	public static final String CMDH_POLICY = "cmp";
	public static final String ACTIVE_CMDH_POLICY = "acmp";
	
	// short names for area nwk info
	public static final String AREA_NWK_TYPE = "ant";
	public static final String LIST_DEVICES = "ldv";
	
	// short names for area nwk device info
	public static final String STATUS = "ss";
	public static final String DEV_ID = "dvd";
	public static final String DEV_TYPE = "dvt";
	public static final String AREA_NWK_ID = "awi";
	public static final String SLEEP_INTERVAL = "sli";
	public static final String SLEEP_DURATION = "sld";
	public static final String LIST_OF_NEIGHBORS = "lnh";
	
	// short name for URI List
	public static final String URI_LIST = "uril";
}
