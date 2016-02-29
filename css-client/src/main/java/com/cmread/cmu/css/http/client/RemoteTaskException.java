package com.cmread.cmu.css.http.client;

public class RemoteTaskException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public RemoteTaskException(String message) {
		super(message);
	}

	public RemoteTaskException(String message, Exception cause) {
		super(message, cause);
	}

}
