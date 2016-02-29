package com.cmread.cmu.css.task.handler.builder;

import java.util.ArrayList;
import java.util.List;

import com.cmread.cmu.css.db.utils.ActionFilter;
import com.cmread.cmu.css.db.utils.DiffSyncFilter;
import com.cmread.cmu.css.db.utils.RowActioin;
import com.cmread.cmu.css.task.handler.Action;
import com.cmread.cmu.css.task.handler.DataMap;

public class DataMapBuilder implements DataMap {

	private TableData fromTable;
	private TableData toTable;
	private List<DiffSyncFilter> diffSyncFilters;

	public DataMapBuilder() {
	}

	// ******************** 以下是DataMap需要实现的接口 *************************

	@Override
	public TableData getFromData() {
		return this.fromTable;
	}

	@Override
	public TableData getToData() {
		return this.toTable;
	}
	
	@Override
	public List<DiffSyncFilter> getDiffSyncFilters() {
		return this.diffSyncFilters;
	}
	
	public void addDiffSyncFilter(DiffSyncFilter filter) {
		if (this.diffSyncFilters == null) {
			this.diffSyncFilters = new ArrayList<>(1);
		}
		this.diffSyncFilters.add(filter);
	}

	// ********************* 以下是builder接口 ********************************

	/*
	 * 暂时放在这里吧，以防以后from和to需要不同的builder 目前看是无用的，仅作锚点来用；
	 */
	public TableDataBuilder from() {
		TableDataBuilder fromBuilder = new TableDataBuilder();
		this.fromTable = fromBuilder;
		return fromBuilder;
	}

	// 同from
	public TableDataBuilder to() {
		TableDataBuilder toBuilder = new TableDataBuilder();
		this.toTable = toBuilder;
		return toBuilder;
	}

	public Action deleteAction() {
		return action(Action.Type.Delete);
	}

	public Action insertAction() {
		return action(Action.Type.Insert);
	}

	public Action updateAction() {
		return action(Action.Type.Update);
	}
	
	public Action diffSyncAction() {
		return action(Action.Type.DiffSync);
	}
	
	public Action diffSyncAction(ActionFilter actionFilter) {
		return new ActionImpl(toDataMap(), Action.Type.DiffSync, actionFilter);
	}
	
	public Action action(Action.Type actionType) {
		return new ActionImpl(toDataMap(), actionType);
	}

	public DataMap toDataMap() {
		return this;
	}

	public class TableDataBuilder implements TableData {

		private String tableName;
		private String sql;
		
		private List<String> condNameList;
		private List<String> condValueList;
		private String[] primaryKey;
		private String[] orderBy;
		
		private List<Filter> filterList;
		private List<Condation> condList;
		
		public TableDataBuilder() {
			this.condNameList = new ArrayList<>();
			this.condValueList = new ArrayList<>();
			this.primaryKey = new String[0];
			this.orderBy = new String[0];
			this.filterList = new ArrayList<>();
			this.condList = new ArrayList<>();
		}

		@Override
		public String getTableName() {
			return this.tableName;
		}
		
		@Override
		public String[] getPrimaryKey() {
			return this.primaryKey;
		}
		
		@Override
		public String[] getOrderBy() {
			return this.orderBy;
		}
		
		@Override
		public String[] getCondNames() {
			for (Condation cond : this.condList) {
				this.condNameList.add(cond.getName());
			}
			return this.condNameList.toArray(new String[this.condNameList.size()]);
		}

		@Override
		public String[] getCondValues() {
			for (Condation cond : this.condList) {
				this.condValueList.add(cond.getValue());
			}
			return this.condValueList.toArray(new String[this.condValueList.size()]);
		}

		@Override
		public String getSql() {
			return this.sql;
		}

		@Override
		public List<Filter> getFilters() {
			return this.filterList;
		}

		public TableDataBuilder tableName(String tableName) {
			this.tableName = tableName;
			return this;
		}
		
		public TableDataBuilder sql(String sql) {
			this.sql = sql;
			return this;
		}

		public TableDataBuilder cond(String name, String value) {
			this.condNameList.add(name);
			this.condValueList.add(value);

			return this;
		}
		
		public TableDataBuilder cond(Condation condition) {
			this.condList.add(condition);
			return this;
		}
		
		public TableDataBuilder primaryKey(String... primaryKeys) {
			this.primaryKey = primaryKeys;
			return this;
		}
		
		public TableDataBuilder orderBy(String... orderBy) {
			this.orderBy = orderBy;
			return this;
		}

		public TableDataBuilder addFilter(Filter dataMapFilter) {
			this.filterList.add(dataMapFilter);
			return this;
		}

	}
	
	public static interface Condation {
		String getName();
		String getValue();
	}

	public class ActionImpl implements Action {

		private DataMap dataMap;
		private Action.Type type;
		private ActionFilter actionFilter;

		public ActionImpl(DataMap dataMap, Type actionType, ActionFilter filter) {
			this.dataMap = dataMap;
			this.type = actionType;
			this.actionFilter = filter;
		}
		
		public ActionImpl(DataMap dataMap, Type actionType) {
			this.dataMap = dataMap;
			this.type = actionType;
			this.actionFilter = null;
		}

		@Override
		public DataMap getDataMap() {
			return this.dataMap;
		}

		@Override
		public Type getType() {
			return this.type;
		}

		@Override
		public ActionFilter getActionFilter() {
			return this.actionFilter;
		}

		@Override
		public RowActioin[] getSubActions() {
			return new RowActioin[0];
		}
	}

}
