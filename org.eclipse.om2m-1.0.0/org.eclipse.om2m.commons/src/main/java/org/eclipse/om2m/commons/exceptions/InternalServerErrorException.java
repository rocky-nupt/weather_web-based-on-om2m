package org.eclipse.om2m.commons.exceptions;

import org.eclipse.om2m.commons.constants.ResponseStatusCode;

public class InternalServerErrorException extends Om2mException{

	private static final long serialVersionUID = -5330070529424377694L;
	
	public InternalServerErrorException(){
		super(ResponseStatusCode.INTERNAL_SERVER_ERROR);
	}
	
	public InternalServerErrorException(String message){
		super(message, ResponseStatusCode.INTERNAL_SERVER_ERROR);
	}
	
	public InternalServerErrorException(String message, Throwable cause){
		super(message, cause, ResponseStatusCode.INTERNAL_SERVER_ERROR);
	}
	
}
