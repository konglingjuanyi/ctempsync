package com.cmread.cmu.css.task.schedule;

import java.util.Date;
import java.util.Vector;

public class TaskEventNotifier {
	
    private Vector<TaskEventListener> listeners;

    public TaskEventNotifier() {
        listeners = new Vector<TaskEventListener>();
    }

    public synchronized void addListener(TaskEventListener o) {
        if (o == null)
            throw new NullPointerException();
        if (!listeners.contains(o)) {
            listeners.addElement(o);
        }
    }

    public synchronized void deleteObserver(TaskEventListener o) {
        listeners.removeElement(o);
    }

    public void notifyListeners(TaskLifecycleEvent arg) {
    	for (int i=0; i<listeners.size(); ++i) {
    		listeners.get(i).onEvent(arg);
    	}
    }

    public synchronized void deleteListeners() {
        listeners.removeAllElements();
    }

	public void taskBegin(Task syncTask, Date date) {
		TaskLifecycleEvent event = new TaskLifecycleEvent();
		event.setType(TaskLifecycleEvent.EventType.BEGIN);
		event.setTask(syncTask);
		event.setTime(date);
		event.setRetrying(false);
		event.setSuccess(false);
		
		notifyListeners(event);
	}

	public void taskFinish(Task task, Date date, boolean success, boolean retrying) {
		TaskLifecycleEvent event = new TaskLifecycleEvent();
		event.setType(TaskLifecycleEvent.EventType.FINISH);
		event.setTask(task);
		event.setTime(date);
		event.setRetrying(retrying);
		event.setSuccess(success);
		
		notifyListeners(event);
	}

}
