package com.cmread.cmu.css.db.utils;

import java.util.ArrayList;
import java.util.List;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.RowKey;
import com.cmread.cmu.css.db.RowSet;

public class RowSetDiff {
	
	/*
	 * 比较两个数据集，自动按照字段名匹配；
	 * 目前假设两个数据集的key必须相同，如果不同的话需要做映射，那么就需要修改此方法，将primarykeyname换为一个映射类
	 * 
	 * 可以将这个方法提取到外面，作为一个Compare类实行；
	 * 
	 */
	public static RowSetDiff diff(RowSet from, RowSet to, String[] primaryKeyNames, List<DiffSyncFilter> dsFilters) {
		RowSetDiff diff = new RowSetDiff();

		for (Row fromRow : from.getRows()) {
			RowKey fromRowKey = fromRow.getPrimaryKey(primaryKeyNames);

			// 先找到键值一样的记录
			Row toRow = to.getRow(fromRowKey);
			if (toRow == null) {
				diff.addFrowNewRow(fromRow, fromRowKey);
			} else {
				if (dsFilters != null) {
					for (DiffSyncFilter dsf : dsFilters) {
						dsf.sameRowFilter(fromRow, toRow);
					}
				}
				RowDiff rowDiff = RowDiff.diffRow(fromRow, toRow);
				if (rowDiff.hasDiffFields()) {
					diff.addFromToDiffRow(fromRow, toRow, fromRowKey, rowDiff);
				}
			}
		}

		for (Row toRow : to.getRows()) {
			RowKey toRowKey = toRow.getPrimaryKey(primaryKeyNames);

			Row fromRow = from.getRow(toRowKey);
			if (fromRow == null) {
				diff.addToNewRow(toRow, toRowKey);
			}
		}
		
		return diff;
	}
	
	private List<SqlDataSetDiffItem> diffRowList;
	
	public RowSetDiff() {
		this.diffRowList = new ArrayList<>();
	}
	
	public List<SqlDataSetDiffItem> getDiffRows() {
		return this.diffRowList;
	}

	private void addToNewRow(Row toRow, RowKey pk) {
		this.diffRowList.add(createDiffRow(DiffRowType.TO_NEW, pk, null, toRow, null));
	}

	private void addFromToDiffRow(Row fromRow, Row toRow, RowKey pk, RowDiff rowDiff) {
		this.diffRowList.add(createDiffRow(DiffRowType.FROM_TO_DIFF, pk, fromRow, toRow, rowDiff));
	}

	private void addFrowNewRow(Row fromRow, RowKey pk) {
		this.diffRowList.add(createDiffRow(DiffRowType.FROM_NEW, pk, fromRow, null, null));
	}
	
	private SqlDataSetDiffItem createDiffRow(DiffRowType diffRowType, RowKey pk, Row fromRow, Row toRow, RowDiff rowDiff) {
		return new SqlDataSetDiffItem(diffRowType, pk, fromRow, toRow, rowDiff);
	}
	
	public static enum DiffRowType {
		FROM_NEW, TO_NEW, FROM_TO_DIFF
	}
	
	public class SqlDataSetDiffItem {

		private DiffRowType diffType;
		private RowKey primaryKey;
		private Row fromRow;
		private Row toRow;
		private RowDiff rowDiff;
		
		// FIXME this is a ugly construct :(
		public SqlDataSetDiffItem(DiffRowType diffType, RowKey primaryKey, Row fromRow, Row toRow, RowDiff rowDiff) {
			this.diffType = diffType;
			this.primaryKey = primaryKey;
			this.fromRow = fromRow;
			this.toRow = toRow;
			this.rowDiff = rowDiff;
		}
		
		public DiffRowType getDiffType() {
			return this.diffType;
		}
		
		public RowKey getPrimaryKey() {
			return this.primaryKey;
		}

		public Row getFromRow() {
			return fromRow;
		}

		public Row getToRow() {
			return toRow;
		}
		
		public RowDiff getRowDiff() {
			return this.rowDiff;
		}

	}
	
}
