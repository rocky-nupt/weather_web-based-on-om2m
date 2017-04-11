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
package org.eclipse.om2m.binding.http;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.om2m.binding.http.constants.HttpHeaders;
import org.eclipse.om2m.binding.http.constants.HttpParameters;
import org.eclipse.om2m.binding.service.RestClientService;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.resource.Attribute;
import org.eclipse.om2m.commons.resource.FilterCriteria;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.utils.Util;

/**
 *  Provides mapping from a protocol-independent request to a HTTP-specific request.
 */

@SuppressWarnings("restriction")
public class RestHttpClient implements RestClientService {
	/** Logger */
	private static Log LOGGER = LogFactory.getLog(RestHttpClient.class);
	/** implemented specific protocol name */
	private static String protocol ="http";

	/**
	 * gets the implemented specific protocol name
	 * @return protocol name
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Converts a protocol-independent {@link RequestPrimitive} object into a standard HTTP request and sends a standard HTTP request.
	 * Converts the received standard HTTP request into {@link ResponsePrimitive} object and returns it back.
	 * @param requestPrimitive - protocol independent request.
	 * @return protocol independent response.
	 */
	public ResponsePrimitive sendRequest(RequestPrimitive requestPrimitive) {
		LOGGER.info("Sending request: " + requestPrimitive);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		ResponsePrimitive responsePrimitive = new ResponsePrimitive(requestPrimitive);    	
		HttpUriRequest method = null;
		
		
		// Retrieve the url
		String url = requestPrimitive.getTo();
		if(!url.startsWith(protocol+"://")){
			if (url.startsWith("://")){
				url = protocol + url;
			} else if (url.startsWith("//")){
				url = protocol + ":" + url;
			} else {
				url = protocol + "://" + url ;
			}
		}

		
		Map<String, List<String>> parameters = getParameters(requestPrimitive);
		parameters.putAll(requestPrimitive.getQueryStrings());
		if(!parameters.isEmpty()){
			String queryString = "";
			for(String parameter : parameters.keySet()){
				for(String value : parameters.get(parameter)){
					queryString += "&" + parameter + "=" + value;					
				}
			}
			queryString = queryString.replaceFirst("&", "?");
			LOGGER.info("Query string generated: " + queryString);
			url += queryString;
		}
		
		try {
			// Set the operation
			BigInteger operation = requestPrimitive.getOperation();
			if (operation != null){
				if (operation.equals(Operation.CREATE)){
					method = new HttpPost(url);
					if(requestPrimitive.getContent() != null){
						((HttpPost) method).setEntity(
								new StringEntity((String)requestPrimitive.getContent()));						
					}
				} else if (operation.equals(Operation.RETRIEVE)){
					method = new HttpGet(url);
				} else if (operation.equals(Operation.UPDATE)){
					method = new HttpPut(url);
					if(requestPrimitive.getContent() != null){
						((HttpPut) method).setEntity(
								new StringEntity((String)requestPrimitive.getContent()));						
					}
				} else if (operation.equals(Operation.DELETE)){
					method = new HttpDelete(url);
				} else if (operation.equals(Operation.NOTIFY)){
					method = new HttpPost(url);
					if(requestPrimitive.getContent() != null){
						((HttpPost) method).setEntity(
								new StringEntity((String)requestPrimitive.getContent()));						
					}
				}
			} else {
				return null;
			}
			
			// Set the return content type
			method.addHeader(HttpHeaders.ACCEPT, requestPrimitive.getReturnContentType());

			// Set the request content type
			String contentTypeHeader = requestPrimitive.getRequestContentType(); 
			
			
			// Set the request identifier header
			if (requestPrimitive.getRequestIdentifier() != null){
				method.addHeader(HttpHeaders.REQUEST_IDENTIFIER, 
						requestPrimitive.getRequestIdentifier());
			}
			
			// Set the originator header
			if (requestPrimitive.getFrom() != null){
				method.addHeader(HttpHeaders.ORIGINATOR,
						requestPrimitive.getFrom());
			}
			
			// Add the content type header with the resource type for create operation
			if (requestPrimitive.getResourceType() != null){
				contentTypeHeader += ";ty=" + requestPrimitive.getResourceType().toString();
			}
			method.addHeader(HttpHeaders.CONTENT_TYPE, contentTypeHeader);
			
			// Add the notification URI in the case of non-blocking request
			if(requestPrimitive.getResponseTypeInfo() != null){
				String uris = "";
				for(String notifUri : requestPrimitive.getResponseTypeInfo().getNotificationURI()){
					uris += "&" + notifUri;
				}
				uris = uris.replaceFirst("&", "");
				method.addHeader(HttpHeaders.RESPONSE_TYPE, uris);
			}
			
			if (requestPrimitive.getName() != null){
				method.addHeader(HttpHeaders.NAME, requestPrimitive.getName());
			}
			
			LOGGER.info("Request to be send: " + method.toString());
			String headers = "";
			for (Header h : method.getAllHeaders()){
				headers += h.toString() + "\n" ;
			}
			LOGGER.info("Headers:\n" + headers);
			
			HttpResponse httpResponse = httpClient.execute(method);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(httpResponse.getFirstHeader(HttpHeaders.RESPONSE_STATUS_CODE) != null){
				responsePrimitive.setResponseStatusCode(new BigInteger(httpResponse.getFirstHeader(HttpHeaders.RESPONSE_STATUS_CODE).getValue()));
			} else {
				responsePrimitive.setResponseStatusCode(getResponseStatusCode(httpResponse, statusCode));				
			}
			if (statusCode != 204){
				if (httpResponse.getEntity() != null){
					responsePrimitive.setContent(Util.convertStreamToString
							(httpResponse.getEntity().getContent()));
					if(httpResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE) != null){
						responsePrimitive.setContentType(httpResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue());
					}
				}
			}
			if (statusCode == 201){
				String contentHeader = "";
				for (Header header : httpResponse.getHeaders(HttpHeaders.CONTENT_LOCATION)){
					contentHeader += header.getValue();
				}
				responsePrimitive.setLocation(contentHeader);
			}
			LOGGER.info("Http Client response: " + responsePrimitive);
			httpClient.close();
		} catch(HttpHostConnectException e){
			LOGGER.info("Target is not reachable: " + requestPrimitive.getTo());
			responsePrimitive.setResponseStatusCode(ResponseStatusCode.TARGET_NOT_REACHABLE);
			responsePrimitive.setContent("Target is not reachable: " + requestPrimitive.getTo());
			responsePrimitive.setContentType(MimeMediaType.TEXT_PLAIN);
		} catch (IOException e){
			LOGGER.error(url + " not found", e);
			responsePrimitive.setResponseStatusCode(ResponseStatusCode.TARGET_NOT_REACHABLE);
		} 
		return responsePrimitive;
	}

	private Map<String, List<String>> getParameters(RequestPrimitive request) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> list;
		if(request.getResultContent() != null){
			list = new ArrayList<String>();
			list.add(request.getResultContent().toString());
			map.put(HttpParameters.RESULT_CONTENT, list);
		}
		if(request.getDiscoveryResultType() != null){
			list = new ArrayList<String>();
			list.add(request.getDiscoveryResultType().toString());
			map.put(HttpParameters.DISCOVERY_RESULT_TYPE, list);
		}
		if(request.getFilterCriteria() != null){
			FilterCriteria filter = request.getFilterCriteria();
			if(!filter.getAttribute().isEmpty()){
				for(Attribute att : filter.getAttribute()){
					list = new ArrayList<String>();
					list.add(att.getValue().toString());
					map.put(att.getName(), list);
				}
			}
			if(!filter.getContentType().isEmpty()){
				list = new ArrayList<String>();
				list.addAll(filter.getContentType());
				map.put(HttpParameters.CONTENT_TYPE, list);		
			}
			if(filter.getCreatedAfter() != null){
				list = new ArrayList<String>();
				list.add(filter.getCreatedAfter());
				map.put(HttpParameters.CREATED_AFTER, list);
			}
			if(filter.getCreatedBefore() != null){
				list = new ArrayList<String>();
				list.add(filter.getCreatedBefore());
				map.put(HttpParameters.CREATED_BEFORE, list);
			}
			if(filter.getExpireAfter() != null){
				list = new ArrayList<String>();
				list.add(filter.getExpireAfter());
				map.put(HttpParameters.EXPIRE_AFTER,list);
			}
			if(filter.getExpireBefore() != null){
				list = new ArrayList<String>();
				list.add(filter.getExpireBefore());
				map.put(HttpParameters.EXPIRE_BEFORE, list);
			}
			if(filter.getFilterUsage() != null){
				list = new ArrayList<String>();
				list.add(filter.getFilterUsage().toString());
				map.put(HttpParameters.FILTER_USAGE, list);
			}
			if(!filter.getLabels().isEmpty()){
				list = new ArrayList<String>();
				list.addAll(filter.getLabels());
				map.put(HttpParameters.LABELS, list);
			}
			if(filter.getLimit() != null){
				list = new ArrayList<String>();
				list.add(filter.getLimit().toString());
				map.put(HttpParameters.LIMIT, list);
			}
			if(filter.getModifiedSince() != null){
				list = new ArrayList<String>();
				list.add(filter.getModifiedSince());
				map.put(HttpParameters.MODIFIED_SINCE, list);
			}
			if(filter.getResourceType() != null){
				list = new ArrayList<String>();
				list.add(filter.getResourceType().toString());
				map.put(HttpParameters.RESOURCE_TYPE, list);
			}
			if(filter.getSizeAbove() != null){
				list = new ArrayList<String>();
				list.add(filter.getSizeAbove().toString());
				map.put(HttpParameters.SIZE_ABOVE, list);
			}
			if(filter.getSizeBelow() != null){
				list = new ArrayList<String>();
				list.add(filter.getSizeBelow().toString());
				map.put(HttpParameters.SIZE_BELOW, list);
			}
			if(filter.getStateTagBigger() != null){
				list = new ArrayList<String>();
				list.add(filter.getStateTagBigger().toString());
				map.put(HttpParameters.STATE_TAG_BIGGER, list);
			}
			if(filter.getStateTagSmaller() != null){
				list = new ArrayList<String>();
				list.add(filter.getStateTagSmaller().toString());
				map.put(HttpParameters.STATE_TAG_SMALLER, list);
			}
			if(filter.getUnmodifiedSince() != null){
				list = new ArrayList<String>();
				list.add(filter.getUnmodifiedSince());
				map.put(HttpParameters.UNMODIFIED_SINCE, list);
			}
		}
		return map;
	}

	/**
	 * Converts a standard HTTP status code into a protocol-independent {@link ResponseStatusCode} object.
	 * @param statusCode - standard HTTP status code.
	 * @return protocol independent status.
	 */
	private BigInteger getResponseStatusCode(HttpResponse response, int statusCode) {
		if(response.getHeaders(HttpHeaders.RESPONSE_STATUS_CODE) != null 
				&& response.getHeaders(HttpHeaders.RESPONSE_STATUS_CODE).length > 0){
			return new BigInteger(response.getHeaders(HttpHeaders.RESPONSE_STATUS_CODE)[0].getValue());
		} else {			
			switch(statusCode){
			case 200: return ResponseStatusCode.OK;
			case 202: return ResponseStatusCode.ACCEPTED;
			case 201: return ResponseStatusCode.CREATED; 
			case 204: return ResponseStatusCode.DELETED;
			case 400: return ResponseStatusCode.BAD_REQUEST;
			case 403: return ResponseStatusCode.ACCESS_DENIED;
			case 404: return ResponseStatusCode.NOT_FOUND;
			case 405: return ResponseStatusCode.OPERATION_NOT_ALLOWED;
			case 409: return ResponseStatusCode.CONFLICT;
			case 500: return ResponseStatusCode.INTERNAL_SERVER_ERROR;
			case 501: return ResponseStatusCode.NOT_IMPLEMENTED;
			case 503: return ResponseStatusCode.SERVICE_UNAVAILABLE;
			default: return ResponseStatusCode.INTERNAL_SERVER_ERROR;
			}
		}
	}

	/**
	 * Converts a protocol independent parameters into a standard HTTP parameters.
	 * @param params - protocol independent parameters map.
	 * @return standard HTTP query string.
	 */
	private static String getQueryFromParams(Map<String, List<String>> params){
		String query;
		List<String> values = new ArrayList<String>();
		String name;
		if (params != null) {
			query="?";
			Iterator<String> it = params.keySet().iterator();
			while(it.hasNext()){
				name = it.next().toString();
				values = params.get(name);
				for(int i=0;i<values.size();i++){
					query = query+name+"="+values.get(i)+"&";
				}
			}
			return query;
		}
		return null;
	}
}
