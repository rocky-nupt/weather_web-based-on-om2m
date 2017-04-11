package org.eclipse.om2m.commons.exceptions;

import org.eclipse.om2m.commons.constants.ResponseStatusCode;

public class OperationNotAllowed extends Om2mException {

	private static final long serialVersionUID = 1205039181936865704L;

	public OperationNotAllowed(){
		super("Operation is not Allowed", ResponseStatusCode.OPERATION_NOT_ALLOWED);
	}
	
	public OperationNotAllowed(String message){
		super(message, ResponseStatusCode.OPERATION_NOT_ALLOWED);
	}
	
	public OperationNotAllowed(String message, Throwable cause){
		super(message, cause, ResponseStatusCode.OPERATION_NOT_ALLOWED);
	}
	
}
