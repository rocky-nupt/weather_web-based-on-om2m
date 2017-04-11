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
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.binding.http.constants.HttpHeaders;
import org.eclipse.om2m.binding.http.constants.HttpParameters;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.resource.FilterCriteria;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.resource.ResponseTypeInfo;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.utils.Util;
import org.eclipse.om2m.core.service.CseService;

/**
 *  Provides mapping from a HTTP-specific request to a protocol-independent request.
 */
public class RestHttpServlet extends HttpServlet {
	/** Logger */
	private static Log LOGGER = LogFactory.getLog(RestHttpServlet.class);
	/** Serial Version UID */
	private static final long serialVersionUID = 1L;
	/** Discovered CSE service */
	private static CseService cse;

	/**
	 * Converts a {@link HttpServletRequest} to a {@link RequestIndication} and uses it to invoke the SCL service.
	 * Converts the received {@link ResponseConfirm} to a {@link HttpServletResponse} and returns it back.
	 */
	@Override
	protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		LOGGER.info("----------------------------------------------------------------------------------------------");
		// Create generic request
		RequestPrimitive request = new RequestPrimitive();

		// Get the URI and split it to retrieve targetID
		String uri = httpServletRequest.getRequestURI();

		// Remove the context (ex: '/api')
		String targetID = uri ;
		if (Activator.CSE_BASE_CONTEXT.length() > 1 && uri.length() > Activator.CSE_BASE_CONTEXT.length()){
			targetID = uri.substring(Activator.CSE_BASE_CONTEXT.length());
		}
		
		if(targetID.startsWith("/~")){
			targetID = targetID.replaceFirst("/~/", "/");
		} else if(targetID.startsWith("/_")){
			targetID = targetID.replaceFirst("/_/", "//");
		} else if(targetID.startsWith("/")){
			targetID = targetID.replaceFirst("/", "");
		}
		
		// Set the To parameter of the primitive request
		request.setTo(targetID);
		// Get the body of the request
		String content = null;
		try {
			if(httpServletRequest.getInputStream().available() > 0){
				content = Util.convertStreamToString(httpServletRequest.getInputStream());				
			}
		} catch (IOException e) {
			LOGGER.error("Error reading httpServletRequest InputStream",e);
		}
		if (content != null && !content.isEmpty()){    		
			request.setContent(content);
		}	

		// Get request parameters
		mapParameters(httpServletRequest, request);

		// Get the oneM2M operation
		LOGGER.info("Built RequestPrimitive: " + request.toString());
		try{
			request.setOperation(getOneM2MOperation(httpServletRequest));			
		} catch (BadRequestException e){
			httpServletResponse.setStatus(400);
			httpServletResponse.getWriter().print(e.getMessage());
			httpServletResponse.setContentType(MimeMediaType.TEXT_PLAIN);
			return;
		}

		mapHeaders(httpServletRequest, request);

		request.getQueryStrings().putAll(getParamsFromQuery(httpServletRequest.getQueryString()));

		// Perform the request in the CSE
		ResponsePrimitive response = null ; 
		if (cse != null){
			response = cse.doRequest(request);
		} else {
			response = new ResponsePrimitive();
			response.setResponseStatusCode(ResponseStatusCode.SERVICE_UNAVAILABLE);
			response.setContent("CSE service is not available.");
			response.setContentType(MimeMediaType.TEXT_PLAIN);
		}

		// Set location header
		if (response.getLocation() != null){
			httpServletResponse.setHeader(HttpHeaders.CONTENT_LOCATION, response.getLocation());
		}
		// Set the request identifier header
		if (response.getRequestIdentifier() != null){
			httpServletResponse.setHeader(HttpHeaders.REQUEST_IDENTIFIER, response.getRequestIdentifier());
		}
		// Set the Origin header
		if (response.getFrom() != null){
			httpServletResponse.setHeader(HttpHeaders.ORIGINATOR, response.getFrom());
		}

		// Set HTTP status code
		int httpStatus = getHttpStatusCode(response.getResponseStatusCode());
		httpServletResponse.setStatus(httpStatus);
		httpServletResponse.setHeader(
				HttpHeaders.RESPONSE_STATUS_CODE, response.getResponseStatusCode().toString());

		// Set the message body
		if (response.getContent() != null){
			// Set the Content-Type header
			httpServletResponse.setContentType(response.getContentType());
			
			String body = (String) response.getContent(); 
			PrintWriter out = null;
			try {
				out = httpServletResponse.getWriter();
			} catch (IOException e) {
				LOGGER.error("Error reading httpServletResponse Writer",e);
			}
			out.println(body);
			out.close();
		}
	}

	/**
	 * Return the oneM2M method from http request informations.
	 * @param httpServletRequest
	 * @return
	 */
	private static BigInteger getOneM2MOperation(HttpServletRequest httpServletRequest) {
		BigInteger result = null ;
		switch(httpServletRequest.getMethod()){
		case "GET":
			return Operation.RETRIEVE;
		case "POST":
			if (httpServletRequest.getHeader(HttpHeaders.CONTENT_TYPE) != null &&
			httpServletRequest.getHeader(HttpHeaders.CONTENT_TYPE).contains("ty=")){
				result = Operation.CREATE;
			} else {
				result = Operation.NOTIFY;
			}
			return result;
		case "PUT":
			return Operation.UPDATE;
		case "DELETE":
			return Operation.DELETE;
		default:
			throw new BadRequestException("HTTP Operation not supported: " + httpServletRequest.getMethod());
		}
	}

	/**
	 * Converts a standard HTTP status code into a  {@link StatusCode} object.
	 * @param statusCode - protocol-independent status code.
	 * @param isEmptyBody - request body existence
	 * @return standard HTTP status code.
	 */
	public static int getHttpStatusCode(BigInteger statusCode){
		if (statusCode.equals(ResponseStatusCode.OK)
				|| statusCode.equals(ResponseStatusCode.UPDATED)
				|| statusCode.equals(ResponseStatusCode.DELETED)){
			return 200;
		} else if (statusCode.equals(ResponseStatusCode.CREATED)){
			return 201;
		} else if (statusCode.equals(ResponseStatusCode.ACCEPTED)){
			return 202 ;
		} else if (statusCode.equals(ResponseStatusCode.BAD_REQUEST)
				|| statusCode.equals(ResponseStatusCode.CONTENTS_UNACCEPTABLE)
				|| statusCode.equals(ResponseStatusCode.MAX_NUMBER_OF_MEMBER_EXCEEDED)
				|| statusCode.equals(ResponseStatusCode.MEMBER_TYPE_INCONSISTENT)
				|| statusCode.equals(ResponseStatusCode.INVALID_CMDTYPE)
				|| statusCode.equals(ResponseStatusCode.INSUFFICIENT_ARGUMENTS)
				|| statusCode.equals(ResponseStatusCode.ALREADY_COMPLETED)
				|| statusCode.equals(ResponseStatusCode.COMMAND_NOT_CANCELLABLE)){
			return 400;
		} else if (statusCode.equals(ResponseStatusCode.ACCESS_DENIED)
				|| statusCode.equals(ResponseStatusCode.SUBSCRIPTION_CREATOR_HAS_NO_PRIVILEGE)
				|| statusCode.equals(ResponseStatusCode.NO_PRIVILEGE)
				|| statusCode.equals(ResponseStatusCode.ALREADY_EXISTS)
				|| statusCode.equals(ResponseStatusCode.TARGET_NOT_SUBSCRIBABLE)
				|| statusCode.equals(ResponseStatusCode.SUBSCRIPTION_HOST_HAS_NO_PRIVILEGE)){
			return 403;
		} else if (statusCode.equals(ResponseStatusCode.NOT_FOUND)
				|| statusCode.equals(ResponseStatusCode.TARGET_NOT_REACHABLE)
				|| statusCode.equals(ResponseStatusCode.EXTERNAL_OBJECT_NOT_FOUND)
				|| statusCode.equals(ResponseStatusCode.EXTERNAL_OBJECT_NOT_REACHABLE)){
			return 404;
		} else if (statusCode.equals(ResponseStatusCode.OPERATION_NOT_ALLOWED)){
			return 405;
		} else if (statusCode.equals(ResponseStatusCode.REQUEST_TIMEOUT)){
			return 408;
		} else if (statusCode.equals(ResponseStatusCode.CONFLICT)
				|| statusCode.equals(ResponseStatusCode.GROUP_REQUEST_IDENTIFIER_EXISTS)){
			return 409;
		} else if (statusCode.equals(ResponseStatusCode.INTERNAL_SERVER_ERROR)
				|| statusCode.equals(ResponseStatusCode.SUBSCRIPTION_VERIFICATION_INITIATION_FAILED)
				|| statusCode.equals(ResponseStatusCode.MGMT_SESSION_CANNOT_BE_ESTABLISHED)
				|| statusCode.equals(ResponseStatusCode.MGMT_SESSION_ESTABLISHMENT_TIMEOUT)
				|| statusCode.equals(ResponseStatusCode.MGMT_CONVERSION_ERROR)
				|| statusCode.equals(ResponseStatusCode.MGMT_CANCELATION_FAILURE)){
			return 500;
		} else if (statusCode.equals(ResponseStatusCode.NOT_IMPLEMENTED)
				|| statusCode.equals(ResponseStatusCode.NON_BLOCKING_REQUEST_NOT_SUPPORTED)){
			return 501;
		} else if (statusCode.equals(ResponseStatusCode.SERVICE_UNAVAILABLE)){
			return 503;
		}
		return 501;
	}

	/**
	 * Get the registered cse
	 * @return registered cse
	 */
	public static CseService getScl() {
		return cse;
	}

	/**
	 * Set the registered cse
	 * @param cse to register
	 */
	public static void setCse(CseService cse) {
		RestHttpServlet.cse = cse;
	}

	/**
	 * Method used to map uri parameters to generic oneM2M request primitive.
	 * @param request http request
	 * @param primitive oneM2M generic request
	 */
	private void mapParameters(HttpServletRequest request, RequestPrimitive primitive){
		if (request.getParameter(HttpParameters.RESPONSE_TYPE) != null){
			if(primitive.getResponseTypeInfo() == null){
				primitive.setResponseTypeInfo(new ResponseTypeInfo());
			}
			primitive.getResponseTypeInfo().setResponseType(new BigInteger(request.getParameter(HttpParameters.RESPONSE_TYPE)));;
		}
		if(request.getParameter(HttpParameters.RESULT_CONTENT) != null){
			primitive.setResultContent(new BigInteger(request.getParameter(HttpParameters.RESULT_CONTENT)));
		}
		if (request.getParameter(HttpParameters.RESULT_PERSISTENCE) != null){
			try {
				Duration duration = DatatypeFactory.newInstance().
						newDuration(request.getParameter(HttpParameters.RESULT_PERSISTENCE));
				primitive.setResultPersistence(duration);
			} catch (DatatypeConfigurationException e) {
				LOGGER.debug("Error in Duration creation",e);
			}
		}
		if (request.getParameter(HttpParameters.DELIVERY_AGGREGATION) != null){
			primitive.setDeliveryAggregation(Boolean.parseBoolean(request.getParameter(HttpParameters.DELIVERY_AGGREGATION)));
		}

		if (request.getParameter(HttpParameters.DISCOVERY_RESULT_TYPE) != null){
			primitive.setDiscoveryResultType(new BigInteger(request.getParameter(HttpParameters.DISCOVERY_RESULT_TYPE)));
		}

		if(request.getParameter(HttpParameters.FILTER_USAGE) != null){
			FilterCriteria filterCriteria = new FilterCriteria();
			filterCriteria.setFilterUsage(new BigInteger(request.getParameter(HttpParameters.FILTER_USAGE)));
			if(request.getParameter(HttpParameters.LIMIT) != null){
				filterCriteria.setLimit(new BigInteger(request.getParameter(HttpParameters.LIMIT)));
			}
			if(request.getParameter(HttpParameters.LABELS) != null){
				filterCriteria.getLabels().addAll(Arrays.asList(request.getParameterValues(HttpParameters.LABELS)));
			}
			if(request.getParameter(HttpParameters.RESOURCE_TYPE) != null){
				filterCriteria.setResourceType(new BigInteger(request.getParameter(HttpParameters.RESOURCE_TYPE)));
			}
			primitive.setFilterCriteria(filterCriteria);
		}
	}

	private void mapHeaders(HttpServletRequest httpServletRequest,
			RequestPrimitive request) {
		// Get the originator from the X-M2M-Origin header

		String authorization = httpServletRequest.getHeader(HttpHeaders.ORIGINATOR);
		if (authorization != null) {
			request.setFrom(authorization);
		}

		// Get the request identifier
		String requestIdentifier = httpServletRequest.getHeader(HttpHeaders.REQUEST_IDENTIFIER) ;
		if (requestIdentifier != null){
			request.setRequestIdentifier(requestIdentifier.trim());
		}

		// Get Request Content type
		String contentTypeHeaders = httpServletRequest.getHeader(HttpHeaders.CONTENT_TYPE);
		if (contentTypeHeaders != null){
			LOGGER.info("Content type headers: " + contentTypeHeaders);
			for (String contentTypeHeader : contentTypeHeaders.split(";")){
				LOGGER.info("Header value: "+ contentTypeHeader);
				if(contentTypeHeader.trim().startsWith("ty=")){
					String resourceType = contentTypeHeader.trim().split("ty=")[1];
					request.setResourceType(new BigInteger(resourceType));
				}
				switch(contentTypeHeader.trim()){
				case MimeMediaType.XML: case MimeMediaType.XML_RESOURCE :
					request.setRequestContentType(MimeMediaType.XML);
					break;
				case MimeMediaType.JSON: case MimeMediaType.JSON_RESOURCE:
					request.setRequestContentType(MimeMediaType.JSON);
					break;
				default:
					break;
				}
			}
		} else{
			request.setRequestContentType(MimeMediaType.XML);
		}

		if(request.getRequestContentType() == null){
			request.setRequestContentType(MimeMediaType.XML);
		}

		// Get dataTypes
		String acceptHeaders = httpServletRequest.getHeader(HttpHeaders.ACCEPT);	
		LOGGER.info("Accept header: " + acceptHeaders);
		if (acceptHeaders != null){
			for (String acceptHeader : acceptHeaders.split(";")){
				switch(acceptHeader.trim()){
				case MimeMediaType.XML: case MimeMediaType.XML_RESOURCE:
					request.setReturnContentType(MimeMediaType.XML);
					break;
				case MimeMediaType.JSON: case MimeMediaType.JSON_RESOURCE:
					request.setReturnContentType(MimeMediaType.JSON);
					break;
				default:
					break;
				}				
			}
		} else {
			request.setReturnContentType(request.getRequestContentType());
		}

		if(request.getReturnContentType() == null){
			request.setReturnContentType(request.getRequestContentType());
		}

		// Map name header
		String nameHeader = httpServletRequest.getHeader(HttpHeaders.NAME);
		if (nameHeader != null){
			request.setName(nameHeader);
		}

		// Map Response Type Uri for non-blocking request
		String rtuHeader = httpServletRequest.getHeader(HttpHeaders.RESPONSE_TYPE);
		if(rtuHeader != null){
			LOGGER.info("Response Type URI header: " + rtuHeader);
			if(request.getResponseTypeInfo() == null){
				request.setResponseTypeInfo(new ResponseTypeInfo());
			}
			for(String notifUri : rtuHeader.split("&")){
				request.getResponseTypeInfo().getNotificationURI().add(notifUri.trim());
			}
		}

	}

	public static String httpRequestToString(HttpServletRequest request, String content){
		String heads = "{\n";
		Enumeration<String> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {

			String headerName = headerNames.nextElement();
			heads += "\t" + (headerName) + ": ";

			Enumeration<String> headers = request.getHeaders(headerName);
			while (headers.hasMoreElements()) {
				String headerValue = headers.nextElement();
				heads += headerValue + "\n";
			}
		}
		heads += "}";
		return "HttpRequest [method=" + request.getMethod() + ", URI=" + request.getRequestURI() + ", representation=" + content +
				", queryString=" + request.getQueryString() + ", headers :\n" + heads + "]";
	}

	public static String httpResponseToString(int statusCode, String errorMessage){
		return "HttpResponse [statusCode=" + statusCode + ", errorMessage="+ errorMessage +"]";
	}

	/**
	 * Converts a standard HTTP query String into a protocol independent parameters map.
	 * @param query - standard HTTP query String.
	 * @return protocol independent parameters map.
	 */
	public static Map<String, List<String>> getParamsFromQuery(String query){
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		if (query != null) {
			String[] pairs = query.split("[&]");
			for (String pair : pairs) {
				String[] param = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = param[0];
				}
				if (param.length > 1) {
					value = param[1];
				}
				if (parameters.containsKey(key)) {
					parameters.get(key).add(value);
				} else {
					List<String> values = new ArrayList<String>();
					values.add(value);
					parameters.put(key,values);
				}
			}
		}
		return parameters;
	}

}
