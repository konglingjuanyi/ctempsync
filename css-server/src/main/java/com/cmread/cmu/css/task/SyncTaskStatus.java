package com.cmread.cmu.css.task;

public enum SyncTaskStatus {
	CREATED(0), STARTED(1), SUCCESS(2), FAILED_TRYING(3), FAILED(4);
	
	private int value;
	
	private SyncTaskStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static SyncTaskStatus valueOf(int statusCode) {
		switch (statusCode) {
		case 0:
			return CREATED;
		case 1:
			return STARTED;
		case 2:
			return SUCCESS;
		case 3:
			return FAILED_TRYING;
		case 4:
			return FAILED;
		}
		return null;
	}
}
