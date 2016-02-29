package com.cmread.cmu.css.task.handler.builder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.ExecSequence;
import com.cmread.cmu.css.task.handler.OneToOneJob;

public class OneToOneJobBuilder implements OneToOneJob {

	private String fromDB;
	private String toDB;
	
	//private List<DataMap> dataMaps;
	private List<ExecSequence> execSequences;
	
	public OneToOneJobBuilder() {
		//this.dataMaps = new LinkedList<>();
		this.execSequences = new LinkedList<>();
	}

	// ************ 以下是实现的OneToOneJob的方法 ****************
	
	@Override
	public String getFromDB() {
		return fromDB;
	}

	@Override
	public String getToDB() {
		return toDB;
	}

//	public List<DataMap> getDataMaps() {
//		return dataMaps;
//	}

	@Override
	public List<ExecSequence> getExecSequence() {
		return this.execSequences;
	}

	// *********** 以下是Builder方法 ***********************
	
	public OneToOneJob fromDB(String fromDB) {
		this.fromDB = fromDB;
		return this;
	}

	public OneToOneJob toDB(String toDB) {
		this.toDB = toDB;
		return this;
	}

	public DataMapBuilder createDataMapBuilder() {
		return new DataMapBuilder();
	}

	/**
	 * 同时加入dataMap和
	 * @param dataMap
	 * @return
	 */
	public OneToOneJobBuilder next(Action... actions) {
		ExecSequenceImpl esi = new ExecSequenceImpl();
		for (Action action : actions) {
			//addDataMap(action.getDataMap());
			esi.addAction(action);
		}
		
		this.execSequences.add(esi);
		
		return this;
	}
	
	public OneToOneJobBuilder nextSequenceList(Action... actions) {
		for (Action action : actions) {
			next(action);
		}
		return this;
	}
	
//	public void addDataMap(DataMap dataMap) {
//	}
	
	public OneToOneJob toJob() {
		return this;
	}
	
	public class ExecSequenceImpl implements ExecSequence {

		private List<Action> actions;

		public ExecSequenceImpl() {
			this.actions = new ArrayList<>();
		}
		
		@Override
		public List<Action> getActions() {
			return this.actions;
		}
		
		public void addAction(Action action) {
			this.actions.add(action);
		}
		
	}

}
