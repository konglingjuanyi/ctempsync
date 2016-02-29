package com.cmread.cmu.css.task;

public class TaskException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public TaskException(String message) {
		super(message);
	}

	public TaskException(String message, Exception cause) {
		super(message, cause);
	}

}
